package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.api.IDataTrackerHelper;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.mixin.common.accessor.MixinPlayerEntityAccessor;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

public class FlyCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literalReqOp("fly")
                .executes(ctx -> {
                    PlayerEntity player = ctx.getSource().getPlayerOrThrow();
                    PlayerAbilities abilities = ((MixinPlayerEntityAccessor) player).getAbilities_();
                    abilities.allowFlying = !abilities.allowFlying;
                    if (!abilities.allowFlying) abilities.flying = false;
                    player.sendAbilitiesUpdate();
                    player.getDataTracker().set(IDataTrackerHelper.get().mayFly(), abilities.allowFlying);
                    sendMsg(player, "You can " + Util.formatFromBool(abilities.allowFlying, "now", "no longer") + DF + " fly.");
                    return 1;
                }));
    }
}
