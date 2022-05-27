package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.api.IDataTrackerHelper;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;

public class JesusCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) throws Exception {
        dispatcher.register(literalReqOp("jesus")
                .executes(ctx -> {
                    Entity entity = ctx.getSource().getEntityOrThrow();
                    entity.getDataTracker().set(IDataTrackerHelper.get().jesus(), !entity.getDataTracker().get(IDataTrackerHelper.get().jesus()));
                    sendMsg(ctx, "You can " + Util.formatFromBool(entity.getDataTracker().get(IDataTrackerHelper.get().jesus()), "now", "no longer") + DF + " walk on water.");
                    return entity.getDataTracker().get(IDataTrackerHelper.get().jesus()) ? 2 : 1;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/jesus";
    }
}
