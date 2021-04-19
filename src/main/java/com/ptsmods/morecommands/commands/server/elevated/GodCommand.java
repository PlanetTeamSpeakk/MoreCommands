package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

public class GodCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("god").requires(IS_OP).executes(ctx -> {
			PlayerEntity player = ctx.getSource().getPlayer();
			player.abilities.invulnerable = !player.abilities.invulnerable;
			player.sendAbilitiesUpdate();
			player.getDataTracker().set(MoreCommands.INVULNERABLE, player.abilities.invulnerable);
			sendMsg(player, "You're now " + formatFromBool(player.abilities.invulnerable, "in", "") + "vulnerable" + DF + ".");
			return 1;
		}));
	}
}
