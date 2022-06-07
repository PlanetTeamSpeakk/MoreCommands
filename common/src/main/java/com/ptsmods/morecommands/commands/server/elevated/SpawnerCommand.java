package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.command.argument.EntitySummonArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;

import java.util.Objects;

public class SpawnerCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literalReqOp("spawner")
                .then(argument("type", EntitySummonArgumentType.entitySummon())
                        .suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                        .executes(ctx -> {
                            BlockHitResult result = (BlockHitResult) MoreCommands.getRayTraceTarget(ctx.getSource().getEntityOrThrow(), ctx.getSource().getWorld(), 160, true, true);
                            BlockState state = ctx.getSource().getWorld().getBlockState(result.getBlockPos());
                            EntityType<?> type = Registry.ENTITY_TYPE.get(ctx.getArgument("type", Identifier.class));
                            BlockPos pos = new BlockPos(ctx.getSource().getPosition());
                            if (!state.isOf(Blocks.SPAWNER)) {
                                if (ctx.getSource().getPlayerOrThrow().getMainHandStack().getItem() == Items.SPAWNER) {
                                    MobSpawnerLogic logic = new LegacyMobSpawnerLogic(ctx.getSource().getWorld(), pos);

                                    ItemStack stack = ctx.getSource().getPlayerOrThrow().getMainHandStack();
                                    NbtCompound tag = stack.getOrCreateNbt();
                                    if (tag != null && (tag = tag.getCompound("BlockEntityTag")).contains("Delay"))
                                        Compat.get().readSpawnerLogicNbt(logic, ctx.getSource().getWorld(), pos, tag);
                                    logic.setEntityId(type);
                                    (tag = new NbtCompound()).put("BlockEntityTag", Compat.get().writeSpawnerLogicNbt(logic, ctx.getSource().getWorld(), pos, new NbtCompound()));
                                    ((NbtCompound) tag.getCompound("BlockEntityTag").getList("SpawnPotentials", 10).get(0)).getCompound("Entity").putString("id", Registry.ENTITY_TYPE.getId(type).toString());
                                    stack.setNbt(tag);
                                    sendMsg(ctx, "Poof!");
                                    return 1;
                                } else sendError(ctx, "You do not appear to be looking at or holding a spawner.");
                            } else {
                                MobSpawnerBlockEntity be = (MobSpawnerBlockEntity) ctx.getSource().getWorld().getBlockEntity(result.getBlockPos());
                                MobSpawnerLogic logic = Objects.requireNonNull(be).getLogic();
                                logic.setEntityId(type);
                                NbtCompound tag = Compat.get().writeSpawnerLogicNbt(logic, ctx.getSource().getWorld(), pos, new NbtCompound());
                                ((NbtCompound) tag.getList("SpawnPotentials", 10).get(0)).getCompound("Entity").putString("id", Registry.ENTITY_TYPE.getId(type).toString());
                                Compat.get().readSpawnerLogicNbt(logic, ctx.getSource().getWorld(), pos, tag);
                                be.markDirty();
                                ctx.getSource().getWorld().updateListeners(result.getBlockPos(), state, state, 3);
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
    private static class LegacyMobSpawnerLogic extends MobSpawnerLogic {
        private final World world;
        private final BlockPos pos;

        public LegacyMobSpawnerLogic(World world, BlockPos pos) {
            this.world = world;
            this.pos = pos;
        }

        @Override
        public void sendStatus(World world, BlockPos pos, int i) {
        }

        // sendStatus(I)V
        public void sendStatus(int status) {}

        public void method_8273(int status) {}

        public void broadcastEvent(int status) {}

        // getWorld()Lnet/minecraft/world/World
        public World getWorld() {
            return world;
        }

        public World method_8271() {
            return getWorld();
        }

        public World getLevel() {
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
