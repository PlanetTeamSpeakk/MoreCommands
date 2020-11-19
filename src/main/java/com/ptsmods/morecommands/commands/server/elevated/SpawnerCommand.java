package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.arguments.RegistryArgumentType;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;

public class SpawnerCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("spawner").requires(IS_OP).then(argument("type", new RegistryArgumentType<>(Registry.ENTITY_TYPE)).executes(ctx -> {
            BlockHitResult result = (BlockHitResult) MoreCommands.getRayTraceTarget(ctx.getSource().getEntityOrThrow(), ctx.getSource().getWorld(), 160, true, true);
            BlockState state = ctx.getSource().getWorld().getBlockState(result.getBlockPos());
            EntityType<?> type = ctx.getArgument("type", EntityType.class);
            if (!state.isOf(Blocks.SPAWNER)) {
                if (ctx.getSource().getPlayer().getMainHandStack().getItem() == Items.SPAWNER) {
                    MobSpawnerLogic logic = new MobSpawnerLogic() {
                        @Override
                        public void sendStatus(int status) {}

                        @Override
                        public World getWorld() {
                            return ctx.getSource().getWorld();
                        }

                        @Override
                        public BlockPos getPos() {
                            return new BlockPos(ctx.getSource().getPosition());
                        }
                    };
                    ItemStack stack = ctx.getSource().getPlayer().getMainHandStack();
                    CompoundTag tag = stack.getOrCreateTag();
                    if (tag != null && (tag = tag.getCompound("BlockEntityTag")).contains("Delay"))
                        logic.fromTag(tag);
                    logic.setEntityId(type);
                    (tag = new CompoundTag()).put("BlockEntityTag", logic.toTag(new CompoundTag()));
                    ((CompoundTag) tag.getCompound("BlockEntityTag").getList("SpawnPotentials", 10).get(0)).getCompound("Entity").putString("id", Registry.ENTITY_TYPE.getId(type).toString());
                    stack.setTag(tag);
                    sendMsg(ctx, "Poof!");
                    return 1;
                } else sendMsg(ctx, Formatting.RED + "You do not appear to be looking at or holding a spawner.");
            } else {
                MobSpawnerBlockEntity be = (MobSpawnerBlockEntity) ctx.getSource().getWorld().getBlockEntity(result.getBlockPos());
                MobSpawnerLogic logic = be.getLogic();
                logic.setEntityId(type);
                CompoundTag tag = logic.toTag(new CompoundTag());
                ((CompoundTag) tag.getList("SpawnPotentials", 10).get(0)).getCompound("Entity").putString("id", Registry.ENTITY_TYPE.getId(type).toString());
                logic.fromTag(tag);
                be.markDirty();
                ctx.getSource().getWorld().updateListeners(result.getBlockPos(), state, state, 3);
                sendMsg(ctx, "Poof!");
                return 1;
            }
            return 0;
        })));
    }
}
