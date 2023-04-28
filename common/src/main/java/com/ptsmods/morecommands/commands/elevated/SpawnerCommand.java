package com.ptsmods.morecommands.commands.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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
                .then(this.<CommandSourceStack>newResourceArgument("type", "entity_type")
                        .executes(ctx -> {
                            BlockHitResult result = (BlockHitResult) MoreCommands.getRayTraceTarget(ctx.getSource().getEntityOrException(), 160, true, true);
                            BlockState state = ctx.getSource().getLevel().getBlockState(result.getBlockPos());
                            EntityType<?> type = getResource(ctx, "type", "entity_type");
                            BlockPos pos = new BlockPos(ctx.getSource().getPosition());
                            if (!state.is(Blocks.SPAWNER)) {
                                if (ctx.getSource().getPlayerOrException().getMainHandItem().getItem() == Items.SPAWNER) {
                                    BaseSpawner logic = new DummyBaseSpawner();

                                    ItemStack stack = ctx.getSource().getPlayerOrException().getMainHandItem();
                                    CompoundTag tag = stack.getOrCreateTag();
                                    if ((tag = tag.getCompound("BlockEntityTag")).contains("Delay"))
                                        logic.load(ctx.getSource().getLevel(), pos, tag);
                                    Compat.get().setBaseSpawnerEntityId(logic, type, ctx.getSource().getLevel(), pos);
                                    (tag = new CompoundTag()).put("BlockEntityTag", Compat.get().writeBaseSpawnerNbt(logic, ctx.getSource().getLevel(), pos, new CompoundTag()));

                                    ListTag spawnPotentials = tag.getCompound("BlockEntityTag").getList("SpawnPotentials", 10);
                                    if (!spawnPotentials.isEmpty())
                                        ((CompoundTag) spawnPotentials.get(0)).getCompound("Entity").putString("id",
                                                Objects.requireNonNull(Compat.get().<EntityType<?>>getBuiltInRegistry("entity_type").getKey(type)).toString());

                                    stack.setTag(tag);
                                    sendMsg(ctx, "Poof!");
                                    return 1;
                                } else sendError(ctx, "You do not appear to be looking at or holding a spawner.");
                            } else {
                                SpawnerBlockEntity be = (SpawnerBlockEntity) ctx.getSource().getLevel().getBlockEntity(result.getBlockPos());
                                BaseSpawner logic = Objects.requireNonNull(be).getSpawner();
                                Compat.get().setBaseSpawnerEntityId(logic, type, ctx.getSource().getLevel(), pos);
                                CompoundTag tag = Compat.get().writeBaseSpawnerNbt(logic, ctx.getSource().getLevel(), pos, new CompoundTag());

                                ListTag spawnPotentials = tag.getList("SpawnPotentials", 10);
                                if (!spawnPotentials.isEmpty())
                                    ((CompoundTag) spawnPotentials.get(0)).getCompound("Entity").putString("id",
                                            Objects.requireNonNull(Compat.get().<EntityType<?>>getBuiltInRegistry("entity_type").getKey(type)).toString());

                                logic.load(ctx.getSource().getLevel(), pos, tag);
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

    private static class DummyBaseSpawner extends BaseSpawner {
        @Override
        public void broadcastEvent(Level world, BlockPos pos, int i) {}
    }
}
