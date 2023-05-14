package com.ptsmods.morecommands.commands.elevated;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.mixin.common.accessor.MixinBlockBoxAccessor;
import dev.architectury.event.events.common.TickEvent;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.commands.SetBlockCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

// I have promised myself not to add any world altering commands to this mod, cuz WorldEdit exists, but I just couldn't let this limitation go unnoticed.
// Thanks to a friend of mine for suggesting this idea btw, he said he hated the 32768 block limit of the fill command, so I removed it,
// and to make sure it wouldn't freeze your world or server when used, I made it fill in parts.
public class FillCommand extends Command {
    private static final BlockInput AIR_BLOCK_ARGUMENT = new BlockInput(Blocks.AIR.defaultBlockState(), Collections.emptySet(), null);
    private static final Map<Runnable, Integer> queue = new HashMap<>();
    private static final List<Runnable> tasks = new ArrayList<>();

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        try {
            Field childrenField = CommandNode.class.getDeclaredField("children");
            childrenField.setAccessible(true);
            Map<String, CommandNode<CommandSourceStack>> children = (Map<String, CommandNode<CommandSourceStack>>) childrenField.get(dispatcher.getRoot());

            Field literalsField = CommandNode.class.getDeclaredField("literals");
            literalsField.setAccessible(true);
            Map<String, LiteralCommandNode<CommandSourceStack>> literals = (Map<String, LiteralCommandNode<CommandSourceStack>>) literalsField.get(dispatcher.getRoot());

            children.remove("fill");
            literals.remove("fill");
        } catch (Exception e) {
            log.error("Could not unregister fill command.", e);
        }

        TickEvent.SERVER_PRE.register(server -> {
            List<Runnable> copy = new ArrayList<>(tasks);
            tasks.clear();
            copy.forEach(Runnable::run);
            for (Map.Entry<Runnable, Integer> entry : new HashMap<>(queue).entrySet())
                if (entry.getValue() == 0) {
                    tasks.add(entry.getKey());
                    queue.remove(entry.getKey());
                }
                else queue.put(entry.getKey(), entry.getValue() - 1);
        });

        Compat compat = Compat.get();
        dispatcher.register(literalReqOp("fill")
                .then(Commands.argument("from", BlockPosArgument.blockPos())
                        .then(Commands.argument("to", BlockPosArgument.blockPos())
                                .then(Commands.argument("block", compat.createBlockStateArgument())
                                        .executes((commandContext) -> execute(commandContext, newBlockBox(BlockPosArgument.getLoadedBlockPos(commandContext, "from"),
                                                BlockPosArgument.getLoadedBlockPos(commandContext, "to")), BlockStateArgument.getBlock(commandContext, "block"), Mode.REPLACE, null))
                                        .then(literal("replace")
                                                .executes((commandContext) -> execute(commandContext, newBlockBox(BlockPosArgument.getLoadedBlockPos(commandContext, "from"),
                                                        BlockPosArgument.getLoadedBlockPos(commandContext, "to")), BlockStateArgument.getBlock(commandContext, "block"), Mode.REPLACE, null))
                                                .then(Commands.argument("filter", compat.createBlockStateArgument())
                                                        .executes((commandContext) -> execute(commandContext, newBlockBox(BlockPosArgument.getLoadedBlockPos(commandContext, "from"),
                                                                BlockPosArgument.getLoadedBlockPos(commandContext, "to")), BlockStateArgument.getBlock(commandContext, "block"), Mode.REPLACE,
                                                                BlockPredicateArgument.getBlockPredicate(commandContext, "filter")))))
                                        .then(literal("keep")
                                                .executes((commandContext) -> execute(commandContext, newBlockBox(BlockPosArgument.getLoadedBlockPos(commandContext, "from"),
                                                        BlockPosArgument.getLoadedBlockPos(commandContext, "to")), BlockStateArgument.getBlock(commandContext, "block"), Mode.REPLACE,
                                                        (cachedBlockPosition) -> cachedBlockPosition.getLevel().isEmptyBlock(cachedBlockPosition.getPos()))))
                                        .then(literal("outline")
                                                .executes((commandContext) -> execute(commandContext, newBlockBox(BlockPosArgument.getLoadedBlockPos(commandContext, "from"),
                                                        BlockPosArgument.getLoadedBlockPos(commandContext, "to")), BlockStateArgument.getBlock(commandContext, "block"), Mode.OUTLINE, null)))
                                        .then(literal("hollow")
                                                .executes((commandContext) -> execute(commandContext, newBlockBox(BlockPosArgument.getLoadedBlockPos(commandContext, "from"),
                                                        BlockPosArgument.getLoadedBlockPos(commandContext, "to")), BlockStateArgument.getBlock(commandContext, "block"), Mode.HOLLOW, null)))
                                        .then(literal("destroy")
                                                .executes((commandContext) -> execute(commandContext, newBlockBox(BlockPosArgument.getLoadedBlockPos(commandContext, "from"),
                                                        BlockPosArgument.getLoadedBlockPos(commandContext, "to")), BlockStateArgument.getBlock(commandContext, "block"), Mode.DESTROY, null)))))));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/fill";
    }

    @Override
    public Collection<String> getRegisteredNodes() {
        return Lists.newArrayList("fill");
    }

    private static int execute(CommandContext<CommandSourceStack> ctx, BoundingBox range, BlockInput block, Mode mode, Predicate<BlockInWorld> filter) {
        ServerLevel serverWorld = ctx.getSource().getLevel();
        MixinBlockBoxAccessor rangeAccess = (MixinBlockBoxAccessor) range;
        Iterator<BlockPos> iterator = BlockPos.betweenClosed(rangeAccess.getMinX_(), rangeAccess.getMinY_(), rangeAccess.getMinZ_(), rangeAccess.getMaxX_(), rangeAccess.getMaxY_(), rangeAccess.getMaxZ_()).iterator();
        AtomicInteger count = new AtomicInteger(0);
        AtomicReference<Runnable> run = new AtomicReference<>();
        run.set(() -> {
            long start = System.currentTimeMillis();
            while (true) {
                BlockPos blockPos = iterator.next();
                BlockInput blockStateArgument = mode.filter.filter(range, blockPos, block, serverWorld);
                if (blockStateArgument != null) {
                    Clearable.tryClear(serverWorld.getBlockEntity(blockPos));
                    if ((filter == null || filter.test(new BlockInWorld(serverWorld, blockPos, true))) && blockStateArgument.place(serverWorld, blockPos, 2)) {
                        serverWorld.blockUpdated(blockPos, serverWorld.getBlockState(blockPos).getBlock());
                        count.incrementAndGet();
                    }
                }
                if (!iterator.hasNext()) {
                    if (count.get() == 0) sendError(ctx, translatableText("commands.fill.failed"));
                    else sendMsg(ctx, translatableText("commands.fill.success", count.get()));
                    return;
                }
                if (System.currentTimeMillis() - start > 15) {
                    queue.put(run.get(), 8);
                    break;
                }
            }
        });
        run.get().run();
        return 1;
    }

    private BoundingBox newBlockBox(BlockPos first, BlockPos second) {
        return new BoundingBox(Math.min(first.getX(), second.getX()), Math.min(first.getY(), second.getY()), Math.min(first.getZ(), second.getZ()), Math.max(first.getX(), second.getX()), Math.max(first.getY(), second.getY()), Math.max(first.getZ(), second.getZ()));
    }

    public enum Mode {
        REPLACE((blockBox, blockPos, blockStateArgument, serverWorld) -> blockStateArgument),
        OUTLINE((blockBox, blockPos, blockStateArgument, serverWorld) -> blockPos.getX() != ((MixinBlockBoxAccessor) blockBox).getMinX_() && blockPos.getX() != ((MixinBlockBoxAccessor) blockBox).getMaxX_() && blockPos.getY() != ((MixinBlockBoxAccessor) blockBox).getMinY_() && blockPos.getY() != ((MixinBlockBoxAccessor) blockBox).getMaxY_() && blockPos.getZ() != ((MixinBlockBoxAccessor) blockBox).getMinZ_() && blockPos.getZ() != ((MixinBlockBoxAccessor) blockBox).getMaxZ_() ? null : blockStateArgument),
        HOLLOW((blockBox, blockPos, blockStateArgument, serverWorld) -> blockPos.getX() != ((MixinBlockBoxAccessor) blockBox).getMinX_() && blockPos.getX() != ((MixinBlockBoxAccessor) blockBox).getMaxX_() && blockPos.getY() != ((MixinBlockBoxAccessor) blockBox).getMinY_() && blockPos.getY() != ((MixinBlockBoxAccessor) blockBox).getMaxY_() && blockPos.getZ() != ((MixinBlockBoxAccessor) blockBox).getMinZ_() && blockPos.getZ() != ((MixinBlockBoxAccessor) blockBox).getMaxZ_() ? AIR_BLOCK_ARGUMENT : blockStateArgument),
        DESTROY((blockBox, blockPos, blockStateArgument, serverWorld) -> {
            serverWorld.destroyBlock(blockPos, true);
            return blockStateArgument;
        });

        public final SetBlockCommand.Filter filter;

        Mode(SetBlockCommand.Filter filter) {
            this.filter = filter;
        }
    }

}
