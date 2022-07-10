package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.api.IDataTrackerHelper;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;

public class JesusCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) throws Exception {
        dispatcher.register(literalReqOp("jesus")
                .executes(ctx -> {
                    Entity entity = ctx.getSource().getEntityOrException();
                    entity.getEntityData().set(IDataTrackerHelper.get().jesus(), !entity.getEntityData().get(IDataTrackerHelper.get().jesus()));
                    sendMsg(ctx, "You can " + Util.formatFromBool(entity.getEntityData().get(IDataTrackerHelper.get().jesus()), "now", "no longer") + DF + " walk on water.");
                    return entity.getEntityData().get(IDataTrackerHelper.get().jesus()) ? 2 : 1;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/jesus";
    }
}
