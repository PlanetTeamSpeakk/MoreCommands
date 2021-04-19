package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

public class SuperPickaxeCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.getRoot().addChild(MoreCommands.createAlias("/", dispatcher.register(literal("superpickaxe").requires(IS_OP).executes(ctx -> {
			PlayerEntity p = ctx.getSource().getPlayer();
			p.getDataTracker().set(MoreCommands.SUPERPICKAXE, !p.getDataTracker().get(MoreCommands.SUPERPICKAXE));
			sendMsg(ctx, "Superpickaxe has been " + formatFromBool(p.getDataTracker().get(MoreCommands.SUPERPICKAXE), "enabled", "disabled") + DF + ".");
			return p.getDataTracker().get(MoreCommands.SUPERPICKAXE) ? 2 : 1;
		}))));
	}
}
