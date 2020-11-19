package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.server.command.ServerCommandSource;

public class JumpCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("jump").requires(IS_OP).executes(ctx -> {
            MoreCommands.teleport(ctx.getSource().getEntityOrThrow(), ctx.getSource().getWorld(), MoreCommands.getRayTraceTarget(ctx.getSource().getEntityOrThrow(), ctx.getSource().getWorld(), 160d, true, true).getPos(), ctx.getSource().getEntity().yaw, ctx.getSource().getEntity().pitch);
            return 1;
        }));
    }
}
