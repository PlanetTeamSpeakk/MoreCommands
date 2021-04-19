package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;

public class MoreCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("more").requires(IS_OP).executes(ctx -> {
			ItemStack stack = ctx.getSource().getEntityOrThrow().getItemsHand().iterator().next();
			if (stack != null && stack.getItem() != Items.AIR) {
				int old = stack.getCount();
				stack.setCount(stack.getMaxCount());
				sendMsg(ctx, "Set stack count from " + SF + old + DF + " to " + SF + stack.getCount() + DF + ".");
				return stack.getMaxCount();
			}
			sendMsg(ctx, Formatting.RED + "You are not holding an item.");
			return 0;
		}));
	}
}
