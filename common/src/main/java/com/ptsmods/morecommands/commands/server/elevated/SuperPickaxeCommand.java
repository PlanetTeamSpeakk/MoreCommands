package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.IDataTrackerHelper;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.player.Player;

public class SuperPickaxeCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.getRoot().addChild(MoreCommands.createAlias("/", dispatcher.register(literalReqOp("superpickaxe")
                .executes(ctx -> {
                    Player p = ctx.getSource().getPlayerOrException();
                    p.getEntityData().set(IDataTrackerHelper.get().superpickaxe(), !p.getEntityData().get(IDataTrackerHelper.get().superpickaxe()));
                    sendMsg(ctx, "Superpickaxe has been " + Util.formatFromBool(p.getEntityData().get(IDataTrackerHelper.get().superpickaxe()), "enabled", "disabled") + DF + ".");
                    return p.getEntityData().get(IDataTrackerHelper.get().superpickaxe()) ? 2 : 1;
                }))));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/super-pickaxe";
    }
}
