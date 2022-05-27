package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public class BreakCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literalReqOp("break")
                .executes(ctx -> {
                    BlockPos block = ((BlockHitResult) MoreCommands.getRayTraceTarget(ctx.getSource().getPlayerOrThrow(), ctx.getSource().getWorld(), 160, true, true)).getBlockPos();
                    PlayerEntity player = ctx.getSource().getPlayerOrThrow();
                    if (block == null || player.getEntityWorld().getBlockState(block).getBlock() == Blocks.AIR) sendMsg(ctx, "You cannot break air.");
                    else {
                        player.getEntityWorld().breakBlock(block, false);
                        sendMsg(ctx, "The block at " + SF + "X: " + block.getX() + DF + ", " + SF + "Y: " + block.getY() + DF + ", " + SF + "Z: " + block.getZ() + DF + " has been broken.");
                        return 1;
                    }
                    return 0;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/break";
    }
}
