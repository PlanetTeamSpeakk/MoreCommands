package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.item.ItemStack;

public class FixCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.getRoot().addChild(MoreCommands.createAlias("repair", dispatcher.register(literalReqOp("fix")
                .executes(ctx -> {
                    ItemStack stack = ctx.getSource().getPlayerOrException().getMainHandItem();
                    if (stack.isEmpty()) sendError(ctx, "You are not holding an item.");
                    else if (!stack.isDamageableItem() || stack.getDamageValue() == 0) sendError(ctx, "This item cannot be fixed.");
                    else {
                        stack.setDamageValue(0);
                        sendMsg(ctx, "As if by " + SF + "magic " + DF + "your item has been fixed! " + SF + ":O");
                        return 1;
                    }
                    return 0;
                }))));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/fix";
    }
}
