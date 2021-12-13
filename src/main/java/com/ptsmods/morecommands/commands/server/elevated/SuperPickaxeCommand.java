package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.util.DataTrackerHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

public class SuperPickaxeCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.getRoot().addChild(MoreCommands.createAlias("/", dispatcher.register(literalReqOp("superpickaxe").executes(ctx -> {
			PlayerEntity p = ctx.getSource().getPlayer();
			p.getDataTracker().set(DataTrackerHelper.SUPERPICKAXE, !p.getDataTracker().get(DataTrackerHelper.SUPERPICKAXE));
			sendMsg(ctx, "Superpickaxe has been " + formatFromBool(p.getDataTracker().get(DataTrackerHelper.SUPERPICKAXE), "enabled", "disabled") + DF + ".");
			return p.getDataTracker().get(DataTrackerHelper.SUPERPICKAXE) ? 2 : 1;
		}))));
	}
}
