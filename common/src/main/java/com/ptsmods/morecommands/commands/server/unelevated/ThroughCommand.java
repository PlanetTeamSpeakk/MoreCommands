package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.mixin.common.accessor.MixinPlayerEntityAccessor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

public class ThroughCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReq("through")
                .executes(ctx -> {
                    Entity entity = ctx.getSource().getEntityOrException();
                    BlockPos pos = entity.blockPosition();
                    Level world = ctx.getSource().getLevel();
                    int x = pos.getX();
                    int y = pos.getY();
                    int z = pos.getZ();
                    boolean yLowered = false;
                    Vec3 rot = entity.getLookAngle();
                    while (true) {
                        for (int x1 = 0; x1 < 64; x1++) { // it will look 64 blocks in front of you at most.
                            switch (Direction.getNearest(rot.x, rot.y, rot.z)) {
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
                                    return ctx.getSource().getServer().getCommands().performCommand(ctx.getSource().withPermission(ctx.getSource().getServer().getOperatorUserPermissionLevel()), "descend");
                                case UP:
                                    return ctx.getSource().getServer().getCommands().performCommand(ctx.getSource().withPermission(ctx.getSource().getServer().getOperatorUserPermissionLevel()), "ascend");
                            }
                            Block block = world.getBlockState(new BlockPos(x, y - 1, z)).getBlock(); // Block under your feet.
                            Block tpblock = world.getBlockState(new BlockPos(x, y, z)).getBlock(); // Block at your feet.
                            Block tpblock2 = world.getBlockState(new BlockPos(x, y + 1, z)).getBlock(); // Block at your head.
                            if ((!MoreCommands.blockBlacklist.contains(block) || entity instanceof Player && ((MixinPlayerEntityAccessor) entity).getAbilities_().flying) && MoreCommands.blockWhitelist.contains(tpblock) && MoreCommands.blockWhitelist.contains(tpblock2)) {
                                entity.teleportToWithTicket(x + entity.getX() - Math.floor(entity.getX()), y, z + entity.getZ() - Math.floor(entity.getZ()));
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

    @Override
    public String getDocsPath() {
        return "/unelevated/through";
    }
}
