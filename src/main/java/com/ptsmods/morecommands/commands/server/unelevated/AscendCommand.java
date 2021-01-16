package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.MoreCommands;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AscendCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("ascend").executes(ctx -> {
            Entity entity = ctx.getSource().getEntityOrThrow();
            World world = entity.getEntityWorld();
            Vec3d pos = entity.getPos();
            double x = pos.x;
            double y = pos.y + 2;
            double z = pos.z;
            for (; y < world.getTopHeightLimit(); y++) {
                Block block = world.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
                Block tpblock = world.getBlockState(new BlockPos(x, y, z)).getBlock();
                Block tpblock2 = world.getBlockState(new BlockPos(x, y + 1, z)).getBlock();
                if (!MoreCommands.blockBlacklist.contains(block) && MoreCommands.blockWhitelist.contains(tpblock) && MoreCommands.blockWhitelist.contains(tpblock2)) {
                    entity.teleport(x + 0.5, y, z + 0.5);
                    sendMsg(ctx, "You have been teleported through the roof.");
                    return 1;
                }
            }
            sendMsg(ctx, "No free spot found above you.");
            return 0;
        }));
    }
}
