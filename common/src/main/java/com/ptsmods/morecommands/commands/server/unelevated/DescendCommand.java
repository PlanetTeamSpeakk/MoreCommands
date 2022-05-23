package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DescendCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literalReq("descend").executes(ctx -> {
            Entity entity = ctx.getSource().getEntityOrThrow();
            BlockPos pos = entity.getBlockPos();
            World world = entity.getEntityWorld();
            int x = pos.getX();
            int y = pos.getY() - 2;
            int z = pos.getZ();
            for (; y > 0; y--) {
                Block block = world.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
                Block tpblock = world.getBlockState(new BlockPos(x, y, z)).getBlock();
                Block tpblock2 = world.getBlockState(new BlockPos(x, y + 1, z)).getBlock();
                if (!MoreCommands.blockBlacklist.contains(block) && MoreCommands.blockWhitelist.contains(tpblock) && MoreCommands.blockWhitelist.contains(tpblock2)) {
                    entity.teleport(x + 0.5, y, z + 0.5);
                    sendMsg(ctx, "You have been teleported through the ground.");
                    return 1;
                }
            }
            // Only got here if no free spot was found.
            sendMsg(ctx, "No free spot found below of you.");
            return 0;
        }));
    }
}
