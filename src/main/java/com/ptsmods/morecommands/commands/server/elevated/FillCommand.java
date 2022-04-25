package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.ptsmods.morecommands.api.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.mixin.common.accessor.MixinBlockBoxAccessor;
import com.ptsmods.morecommands.util.CompatHolder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.BlockPredicateArgumentType;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.SetBlockCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

// I have promised myself not to add any world altering commands to this mod, cuz WorldEdit exists, but I just couldn't let this limitation go unnoticed.
// Thanks to a friend of mine for suggesting this idea btw, he said he hated the 32768 block limit of the fill command, so I removed it,
// and to make sure it wouldn't freeze your world or server when used, I made it fill in parts.
public class FillCommand extends Command {
	private static final BlockStateArgument AIR_BLOCK_ARGUMENT = new BlockStateArgument(Blocks.AIR.getDefaultState(), Collections.emptySet(), null);
	private static final Map<Runnable, Integer> queue = new HashMap<>();
	private static final List<Runnable> tasks = new ArrayList<>();

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		try {
			Field childrenField = CommandNode.class.getDeclaredField("children");
			childrenField.setAccessible(true);
			Map<String, CommandNode<ServerCommandSource>> children = (Map<String, CommandNode<ServerCommandSource>>) childrenField.get(dispatcher.getRoot());

			Field literalsField = CommandNode.class.getDeclaredField("literals");
			literalsField.setAccessible(true);
			Map<String, LiteralCommandNode<ServerCommandSource>> literals = (Map<String, LiteralCommandNode<ServerCommandSource>>) literalsField.get(dispatcher.getRoot());

			children.remove("fill");
			literals.remove("fill");
		} catch (Exception e) {
			log.error("Could not unregister fill command.", e);
		}

		ServerTickEvents.START_SERVER_TICK.register(server -> {
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

		Compat compat = CompatHolder.getCompat();
		dispatcher.register(literalReqOp("fill")
				.then(CommandManager.argument("from", BlockPosArgumentType.blockPos())
						.then(CommandManager.argument("to", BlockPosArgumentType.blockPos())
								.then(CommandManager.argument("block", compat.createBlockStateArgumentType())
										.executes((commandContext) -> execute(commandContext, newBlockBox(BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"),
												BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")), BlockStateArgumentType.getBlockState(commandContext, "block"), Mode.REPLACE, null))
										.then(literal("replace")
												.executes((commandContext) -> execute(commandContext, newBlockBox(BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"),
														BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")), BlockStateArgumentType.getBlockState(commandContext, "block"), Mode.REPLACE, null))
												.then(CommandManager.argument("filter", compat.createBlockStateArgumentType())
														.executes((commandContext) -> execute(commandContext, newBlockBox(BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"),
																BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")), BlockStateArgumentType.getBlockState(commandContext, "block"), Mode.REPLACE,
																BlockPredicateArgumentType.getBlockPredicate(commandContext, "filter")))))
										.then(literal("keep")
												.executes((commandContext) -> execute(commandContext, newBlockBox(BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"),
														BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")), BlockStateArgumentType.getBlockState(commandContext, "block"), Mode.REPLACE,
														(cachedBlockPosition) -> cachedBlockPosition.getWorld().isAir(cachedBlockPosition.getBlockPos()))))
										.then(literal("outline")
												.executes((commandContext) -> execute(commandContext, newBlockBox(BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"),
														BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")), BlockStateArgumentType.getBlockState(commandContext, "block"), Mode.OUTLINE, null)))
										.then(literal("hollow")
												.executes((commandContext) -> execute(commandContext, newBlockBox(BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"),
														BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")), BlockStateArgumentType.getBlockState(commandContext, "block"), Mode.HOLLOW, null)))
										.then(literal("destroy")
												.executes((commandContext) -> execute(commandContext, newBlockBox(BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"),
														BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")), BlockStateArgumentType.getBlockState(commandContext, "block"), Mode.DESTROY, null)))))));
	}

	private static int execute(CommandContext<ServerCommandSource> ctx, BlockBox range, BlockStateArgument block, Mode mode, Predicate<CachedBlockPosition> filter) {
		ServerWorld serverWorld = ctx.getSource().getWorld();
		MixinBlockBoxAccessor rangeAccess = (MixinBlockBoxAccessor) range;
		Iterator<BlockPos> iterator = BlockPos.iterate(rangeAccess.getMinX_(), rangeAccess.getMinY_(), rangeAccess.getMinZ_(), rangeAccess.getMaxX_(), rangeAccess.getMaxY_(), rangeAccess.getMaxZ_()).iterator();
		AtomicInteger count = new AtomicInteger(0);
		AtomicReference<Runnable> run = new AtomicReference<>();
		run.set(() -> {
			long start = System.currentTimeMillis();
			while (true) {
				BlockPos blockPos = iterator.next();
				BlockStateArgument blockStateArgument = mode.filter.filter(range, blockPos, block, serverWorld);
				if (blockStateArgument != null) {
					Clearable.clear(serverWorld.getBlockEntity(blockPos));
					if ((filter == null || filter.test(new CachedBlockPosition(serverWorld, blockPos, true))) && blockStateArgument.setBlockState(serverWorld, blockPos, 2)) {
						serverWorld.updateNeighbors(blockPos, serverWorld.getBlockState(blockPos).getBlock());
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

	private BlockBox newBlockBox(BlockPos first, BlockPos second) {
		return new BlockBox(Math.min(first.getX(), second.getX()), Math.min(first.getY(), second.getY()), Math.min(first.getZ(), second.getZ()), Math.max(first.getX(), second.getX()), Math.max(first.getY(), second.getY()), Math.max(first.getZ(), second.getZ()));
	}

	public enum Mode {
		REPLACE((blockBox, blockPos, blockStateArgument, serverWorld) -> blockStateArgument),
		OUTLINE((blockBox, blockPos, blockStateArgument, serverWorld) -> blockPos.getX() != ((MixinBlockBoxAccessor) blockBox).getMinX_() && blockPos.getX() != ((MixinBlockBoxAccessor) blockBox).getMaxX_() && blockPos.getY() != ((MixinBlockBoxAccessor) blockBox).getMinY_() && blockPos.getY() != ((MixinBlockBoxAccessor) blockBox).getMaxY_() && blockPos.getZ() != ((MixinBlockBoxAccessor) blockBox).getMinZ_() && blockPos.getZ() != ((MixinBlockBoxAccessor) blockBox).getMaxZ_() ? null : blockStateArgument),
		HOLLOW((blockBox, blockPos, blockStateArgument, serverWorld) -> blockPos.getX() != ((MixinBlockBoxAccessor) blockBox).getMinX_() && blockPos.getX() != ((MixinBlockBoxAccessor) blockBox).getMaxX_() && blockPos.getY() != ((MixinBlockBoxAccessor) blockBox).getMinY_() && blockPos.getY() != ((MixinBlockBoxAccessor) blockBox).getMaxY_() && blockPos.getZ() != ((MixinBlockBoxAccessor) blockBox).getMinZ_() && blockPos.getZ() != ((MixinBlockBoxAccessor) blockBox).getMaxZ_() ? AIR_BLOCK_ARGUMENT : blockStateArgument),
		DESTROY((blockBox, blockPos, blockStateArgument, serverWorld) -> {
			serverWorld.breakBlock(blockPos, true);
			return blockStateArgument;
		});

		public final SetBlockCommand.Filter filter;

		Mode(SetBlockCommand.Filter filter) {
			this.filter = filter;
		}
	}

}
