package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntitySummonArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Objects;

public class SpawnerCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReqOp("spawner")
                .then(argument("type", EntitySummonArgument.id())
                        .suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                        .executes(ctx -> {
                            BlockHitResult result = (BlockHitResult) MoreCommands.getRayTraceTarget(ctx.getSource().getEntityOrException(), 160, true, true);
                            BlockState state = ctx.getSource().getLevel().getBlockState(result.getBlockPos());
                            EntityType<?> type = Registry.ENTITY_TYPE.get(ctx.getArgument("type", ResourceLocation.class));
                            BlockPos pos = new BlockPos(ctx.getSource().getPosition());
                            if (!state.is(Blocks.SPAWNER)) {
                                if (ctx.getSource().getPlayerOrException().getMainHandItem().getItem() == Items.SPAWNER) {
                                    BaseSpawner logic = new LegacyMobSpawnerLogic(ctx.getSource().getLevel(), pos);

                                    ItemStack stack = ctx.getSource().getPlayerOrException().getMainHandItem();
                                    CompoundTag tag = stack.getOrCreateTag();
                                    if (tag != null && (tag = tag.getCompound("BlockEntityTag")).contains("Delay"))
                                        Compat.get().readSpawnerLogicNbt(logic, ctx.getSource().getLevel(), pos, tag);
                                    logic.setEntityId(type);
                                    (tag = new CompoundTag()).put("BlockEntityTag", Compat.get().writeSpawnerLogicNbt(logic, ctx.getSource().getLevel(), pos, new CompoundTag()));

                                    ListTag spawnPotentials = tag.getCompound("BlockEntityTag").getList("SpawnPotentials", 10);
                                    if (spawnPotentials != null && !spawnPotentials.isEmpty())
                                        ((CompoundTag) spawnPotentials.get(0)).getCompound("Entity").putString("id", Registry.ENTITY_TYPE.getKey(type).toString());

                                    stack.setTag(tag);
                                    sendMsg(ctx, "Poof!");
                                    return 1;
                                } else sendError(ctx, "You do not appear to be looking at or holding a spawner.");
                            } else {
                                SpawnerBlockEntity be = (SpawnerBlockEntity) ctx.getSource().getLevel().getBlockEntity(result.getBlockPos());
                                BaseSpawner logic = Objects.requireNonNull(be).getSpawner();
                                logic.setEntityId(type);
                                CompoundTag tag = Compat.get().writeSpawnerLogicNbt(logic, ctx.getSource().getLevel(), pos, new CompoundTag());

                                ListTag spawnPotentials = tag.getList("SpawnPotentials", 10);
                                if (spawnPotentials != null && !spawnPotentials.isEmpty())
                                    ((CompoundTag) spawnPotentials.get(0)).getCompound("Entity").putString("id", Registry.ENTITY_TYPE.getKey(type).toString());

                                Compat.get().readSpawnerLogicNbt(logic, ctx.getSource().getLevel(), pos, tag);
                                be.setChanged();
                                ctx.getSource().getLevel().sendBlockUpdated(result.getBlockPos(), state, state, 3);
                                sendMsg(ctx, "Poof!");
                                return 1;
                            }
                            return 0;
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/spawner";
    }

    // Keeping the old methods in there even though they don't override anything in 1.17 for compatibility with 1.16
    private static class LegacyMobSpawnerLogic extends BaseSpawner {
        private final Level world;
        private final BlockPos pos;

        public LegacyMobSpawnerLogic(Level world, BlockPos pos) {
            this.world = world;
            this.pos = pos;
        }

        @Override
        public void broadcastEvent(Level world, BlockPos pos, int i) {
        }

        // sendStatus(I)V
        public void sendStatus(int status) {}

        public void method_8273(int status) {}

        public void m_142523_(int status) {}

        public void broadcastEvent(int status) {}

        // getWorld()Lnet/minecraft/world/World
        public Level getWorld() {
            return world;
        }

        public Level method_8271() {
            return getWorld();
        }

        public Level getLevel() {
            return getWorld();
        }

        // getPos()Lnet/minecraft/util/math/BlockPos (moj name is the same)
        public BlockPos getPos() {
            return pos;
        }

        public BlockPos method_8276() {
            return getPos();
        }
    }
}
