package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.api.IDataTrackerHelper;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.mixin.common.accessor.MixinPlayerEntityAccessor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;

public class GodCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReqOp("god")
                .executes(ctx -> {
                    Player player = ctx.getSource().getPlayerOrException();
                    Abilities abilities = ((MixinPlayerEntityAccessor) player).getAbilities_();
                    abilities.invulnerable = !abilities.invulnerable;
                    player.onUpdateAbilities();
                    player.getEntityData().set(IDataTrackerHelper.get().invulnerable(), abilities.invulnerable);
                    sendMsg(player, "You're now " + Util.formatFromBool(abilities.invulnerable, "in", "") + "vulnerable" + DF + ".");
                    return 1;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/god";
    }
}
