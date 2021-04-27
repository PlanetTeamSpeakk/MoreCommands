package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

public class GodCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("god").requires(IS_OP).executes(ctx -> {
			PlayerEntity player = ctx.getSource().getPlayer();
			PlayerAbilities abilities = Compat.getCompat().getAbilities(player);
			abilities.invulnerable = !abilities.invulnerable;
			player.sendAbilitiesUpdate();
			player.getDataTracker().set(MoreCommands.INVULNERABLE, abilities.invulnerable);
			sendMsg(player, "You're now " + formatFromBool(abilities.invulnerable, "in", "") + "vulnerable" + DF + ".");
			return 1;
		}));
	}
}
