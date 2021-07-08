package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

public class FlyCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literalReqOp("fly").executes(ctx -> {
			PlayerEntity player = ctx.getSource().getPlayer();
			PlayerAbilities abilities = Compat.getCompat().getAbilities(player);
			abilities.allowFlying = !abilities.allowFlying;
			if (!abilities.allowFlying) abilities.flying = false;
			player.sendAbilitiesUpdate();
			player.getDataTracker().set(MoreCommands.MAY_FLY, abilities.allowFlying);
			sendMsg(player, "You can " + formatFromBool(abilities.allowFlying, "now", "no longer") + DF + " fly.");
			return 1;
		}));
	}
}
