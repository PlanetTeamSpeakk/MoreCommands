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
                            try {
                                BlockHitResult result = (BlockHitResult) MoreCommands.getRayTraceTarget(ctx.getSource().getEntityOrThrow(), ctx.getSource().getWorld(), 160, true, true);
                                BlockState state = ctx.getSource().getWorld().getBlockState(result.getBlockPos());
                                EntityType<?> type = Registry.ENTITY_TYPE.get(ctx.getArgument("type", Identifier.class));
                                BlockPos pos = new BlockPos(ctx.getSource().getPosition());
                                if (!state.isOf(Blocks.SPAWNER)) {
                                    if (ctx.getSource().getPlayerOrThrow().getMainHandStack().getItem() == Items.SPAWNER) {
                                        MobSpawnerLogic logic = new MobSpawnerLogic() {
                                            @Override
                                            public void sendStatus(World world, BlockPos pos, int i) {
                                            }

                                            public void sendStatus(int status) {
                                            }

                                            public World getWorld() {
                                                return ctx.getSource().getWorld();
                                            }

                                            public BlockPos getPos() {
                                                return pos;
                                            }
                                        }; // Keeping the old methods in there even though they don't override anything in 1.17 for compatibility with 1.16
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
                            } catch (Exception e) {
                                MoreCommands.LOG.catching(e);
                            }
                            return 0;
                        })));
    }
}
