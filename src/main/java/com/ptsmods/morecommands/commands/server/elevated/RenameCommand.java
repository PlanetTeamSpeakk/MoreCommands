package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class RenameCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("rename").requires(IS_OP).then(argument("name", StringArgumentType.greedyString()).executes(ctx -> {
			ItemStack stack = ctx.getSource().getPlayer().getMainHandStack();
			if (stack.isEmpty()) sendMsg(ctx, Formatting.RED + "You are not holding an item.");
			else {
				String name = MoreCommands.translateFormattings(ctx.getArgument("name", String.class));
				stack.setCustomName(new LiteralText(name));
				sendMsg(ctx, "The item has been renamed to " + name + Formatting.RESET + ".");
				return 1;
			}
			return 0;
		})));
	}
}
