package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;

public class FixCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.getRoot().addChild(MoreCommands.createAlias("repair", dispatcher.register(literal("fix").requires(IS_OP).executes(ctx -> {
			ItemStack stack = ctx.getSource().getPlayer().getMainHandStack();
			if (stack.isEmpty()) sendMsg(ctx, Formatting.RED + "You are not holding an item.");
			else if (!stack.isDamageable() || stack.getDamage() == 0) sendMsg(ctx, Formatting.RED + "This item cannot be fixed.");
			else {
				stack.setDamage(0);
				sendMsg(ctx, "As if by " + SF + "magic " + DF + "your item has been fixed! " + SF + ":O");
				return 1;
			}
			return 0;
		}))));
	}
}
