package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.server.command.ServerCommandSource;

public class TopCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literalReq("top").executes(ctx -> {
            ctx.getSource().getEntityOrThrow().teleport(ctx.getSource().getPosition().x, MoreCommands.getY(ctx.getSource().getWorld(), (int) ctx.getSource().getPosition().x, (int) ctx.getSource().getPosition().z), ctx.getSource().getPosition().z);
            sendMsg(ctx, "You have been teleported through the roof.");
            return 1;
        }));
    }
}
