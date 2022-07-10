package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class DescendCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReq("descend")
                .executes(ctx -> {
                    Entity entity = ctx.getSource().getEntityOrException();
                    BlockPos pos = entity.blockPosition();
                    Level world = entity.getCommandSenderWorld();
                    int x = pos.getX();
                    int y = pos.getY() - 2;
                    int z = pos.getZ();
                    for (; y > 0; y--) {
                        Block block = world.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
                        Block tpblock = world.getBlockState(new BlockPos(x, y, z)).getBlock();
                        Block tpblock2 = world.getBlockState(new BlockPos(x, y + 1, z)).getBlock();
                        if (!MoreCommands.blockBlacklist.contains(block) && MoreCommands.blockWhitelist.contains(tpblock) && MoreCommands.blockWhitelist.contains(tpblock2)) {
                            entity.teleportToWithTicket(x + 0.5, y, z + 0.5);
                            sendMsg(ctx, "You have been teleported through the ground.");
                            return 1;
                        }
                    }
                    // Only got here if no free spot was found.
                    sendMsg(ctx, "No free spot found below of you.");
                    return 0;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/descend";
    }
}
