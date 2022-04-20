package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.util.DataTrackerHelper;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;

public class JesusCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) throws Exception {
		dispatcher.register(literalReqOp("jesus").executes(ctx -> {
			Entity entity = ctx.getSource().getEntityOrThrow();
			entity.getDataTracker().set(DataTrackerHelper.JESUS, !entity.getDataTracker().get(DataTrackerHelper.JESUS));
			sendMsg(ctx, "You can " + formatFromBool(entity.getDataTracker().get(DataTrackerHelper.JESUS), "now", "no longer") + DF + " walk on water.");
			return entity.getDataTracker().get(DataTrackerHelper.JESUS) ? 2 : 1;
		}));
	}
}
