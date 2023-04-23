package com.ptsmods.morecommands.commands.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.api.IDataTrackerHelper;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.mixin.common.accessor.MixinPlayerEntityAccessor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;

public class FlyCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReqOp("fly")
                .executes(ctx -> {
                    Player player = ctx.getSource().getPlayerOrException();
                    Abilities abilities = ((MixinPlayerEntityAccessor) player).getAbilities_();
                    abilities.mayfly = !abilities.mayfly;
                    if (!abilities.mayfly) abilities.flying = false;
                    player.onUpdateAbilities();
                    player.getEntityData().set(IDataTrackerHelper.get().mayFly(), abilities.mayfly);
                    sendMsg(player, "You can " + Util.formatFromBool(abilities.mayfly, "now", "no longer") + DF + " fly.");
                    return 1;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/fly";
    }
}
