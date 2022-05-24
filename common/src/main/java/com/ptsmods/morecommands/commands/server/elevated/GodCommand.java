package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.api.IDataTrackerHelper;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.mixin.common.accessor.MixinPlayerEntityAccessor;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

public class GodCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literalReqOp("god").executes(ctx -> {
            PlayerEntity player = ctx.getSource().getPlayerOrThrow();
            PlayerAbilities abilities = ((MixinPlayerEntityAccessor) player).getAbilities_();
            abilities.invulnerable = !abilities.invulnerable;
            player.sendAbilitiesUpdate();
            player.getDataTracker().set(IDataTrackerHelper.get().invulnerable(), abilities.invulnerable);
            sendMsg(player, "You're now " + Util.formatFromBool(abilities.invulnerable, "in", "") + "vulnerable" + DF + ".");
            return 1;
        }));
    }
}
