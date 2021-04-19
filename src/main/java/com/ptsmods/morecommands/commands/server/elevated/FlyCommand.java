package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

public class FlyCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("fly").requires(IS_OP).executes(ctx -> {
			PlayerEntity player = ctx.getSource().getPlayer();
			player.abilities.allowFlying = !player.abilities.allowFlying;
			if (!player.abilities.allowFlying) player.abilities.flying = false;
			player.sendAbilitiesUpdate();
			player.getDataTracker().set(MoreCommands.MAY_FLY, player.abilities.allowFlying);
			sendMsg(player, "You can " + formatFromBool(player.abilities.allowFlying, "now", "no longer") + DF + " fly.");
			return 1;
		}));
	}
}
