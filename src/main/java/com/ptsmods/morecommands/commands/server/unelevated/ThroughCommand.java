package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ThroughCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("through").executes(ctx -> {
            Entity entity = ctx.getSource().getEntityOrThrow();
            BlockPos pos = entity.getBlockPos();
            World world = ctx.getSource().getWorld();
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            boolean yLowered = false;
            Vec3d rot = entity.getRotationVector();
            while (true) {
                for (int x1 = 0; x1 < 64; x1++) { // it will look 64 blocks in front of you at most.
                    switch (Direction.getFacing(rot.x, rot.y, rot.z)) {
                        case NORTH:
                            z--;
                            break;
                        case WEST:
                            x--;
                            break;
                        case SOUTH:
                            z++;
                            break;
                        case EAST:
                            x++;
                            break;
                        case DOWN:
                            return ctx.getSource().getMinecraftServer().getCommandManager().execute(ctx.getSource().withLevel(ctx.getSource().getMinecraftServer().getOpPermissionLevel()), "descend");
                        case UP:
                            return ctx.getSource().getMinecraftServer().getCommandManager().execute(ctx.getSource().withLevel(ctx.getSource().getMinecraftServer().getOpPermissionLevel()), "ascend");
                    }
                    Block block = world.getBlockState(new BlockPos(x, y - 1, z)).getBlock(); // Block under your feet.
                    Block tpblock = world.getBlockState(new BlockPos(x, y, z)).getBlock(); // Block at your feet.
                    Block tpblock2 = world.getBlockState(new BlockPos(x, y + 1, z)).getBlock(); // Block at your head.
                    if ((!MoreCommands.blockBlacklist.contains(block) || entity instanceof PlayerEntity && ((PlayerEntity) entity).abilities.flying) && MoreCommands.blockWhitelist.contains(tpblock) && MoreCommands.blockWhitelist.contains(tpblock2)) {
                        entity.teleport(x + entity.getX() - Math.floor(entity.getX()), y, z + entity.getZ() - Math.floor(entity.getZ()));
                        sendMsg(ctx, "You have been teleported through the wall.");
                        return 1;
                    }
                }
                if (y <= pos.getY() && y != pos.getY() - 8 && !yLowered) {
                    y -= 1;
                    x = pos.getX();
                    z = pos.getZ();
                } else if (y == pos.getY() - 8 && y != pos.getY() + 8) {
                    yLowered = true;
                    y += 1;
                    x = pos.getX();
                    z = pos.getZ();
                } else break;
            }
            // Only got here if no free spot was found.
            sendMsg(ctx, "No free spot found ahead of you.");
            return 0;
        }));
    }
}
