package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;

public class FixCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.getRoot().addChild(MoreCommands.createAlias("repair", dispatcher.register(literalReqOp("fix").executes(ctx -> {
            ItemStack stack = ctx.getSource().getPlayerOrThrow().getMainHandStack();
            if (stack.isEmpty()) sendError(ctx, "You are not holding an item.");
            else if (!stack.isDamageable() || stack.getDamage() == 0) sendError(ctx, "This item cannot be fixed.");
            else {
                stack.setDamage(0);
                sendMsg(ctx, "As if by " + SF + "magic " + DF + "your item has been fixed! " + SF + ":O");
                return 1;
            }
            return 0;
        }))));
    }
}
