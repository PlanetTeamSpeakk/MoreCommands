package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.IDataTrackerHelper;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

public class SuperPickaxeCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.getRoot().addChild(MoreCommands.createAlias("/", dispatcher.register(literalReqOp("superpickaxe").executes(ctx -> {
            PlayerEntity p = ctx.getSource().getPlayerOrThrow();
            p.getDataTracker().set(IDataTrackerHelper.get().superpickaxe(), !p.getDataTracker().get(IDataTrackerHelper.get().superpickaxe()));
            sendMsg(ctx, "Superpickaxe has been " + Util.formatFromBool(p.getDataTracker().get(IDataTrackerHelper.get().superpickaxe()), "enabled", "disabled") + DF + ".");
            return p.getDataTracker().get(IDataTrackerHelper.get().superpickaxe()) ? 2 : 1;
        }))));
    }
}
