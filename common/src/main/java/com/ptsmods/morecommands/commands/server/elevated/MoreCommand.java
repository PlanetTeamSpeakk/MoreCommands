package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MoreCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReqOp("more")
                .executes(ctx -> {
                    ItemStack stack = ctx.getSource().getEntityOrException().getHandSlots().iterator().next();
                    if (stack != null && stack.getItem() != Items.AIR) {
                        int old = stack.getCount();
                        stack.setCount(stack.getMaxStackSize());
                        sendMsg(ctx, "Set stack count from " + SF + old + DF + " to " + SF + stack.getCount() + DF + ".");
                        return stack.getMaxStackSize();
                    }
                    sendError(ctx, "You are not holding an item.");
                    return 0;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/more";
    }
}
