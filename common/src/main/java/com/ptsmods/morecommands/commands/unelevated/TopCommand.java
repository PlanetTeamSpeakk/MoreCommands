package com.ptsmods.morecommands.commands.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;

public class TopCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReq("top")
                .executes(ctx -> {
                    ctx.getSource().getEntityOrException().teleportToWithTicket(ctx.getSource().getPosition().x, MoreCommands.getY(ctx.getSource().getLevel(),
                            (int) ctx.getSource().getPosition().x, (int) ctx.getSource().getPosition().z), ctx.getSource().getPosition().z);
                    sendMsg(ctx, "You have been teleported through the roof.");
                    return 1;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/top";
    }
}
