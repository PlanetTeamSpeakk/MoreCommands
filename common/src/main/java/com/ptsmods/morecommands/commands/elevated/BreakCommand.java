package com.ptsmods.morecommands.commands.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;

public class BreakCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReqOp("break")
                .executes(ctx -> {
                    BlockPos block = ((BlockHitResult) MoreCommands.getRayTraceTarget(ctx.getSource().getPlayerOrException(), 160, true, true)).getBlockPos();
                    Player player = ctx.getSource().getPlayerOrException();
                    if (block == null || player.getCommandSenderWorld().getBlockState(block).getBlock() == Blocks.AIR) sendMsg(ctx, "You cannot break air.");
                    else {
                        player.getCommandSenderWorld().destroyBlock(block, false);
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
