package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

public class FlyCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("fly").requires(IS_OP).executes(ctx -> {
            PlayerEntity player = ctx.getSource().getPlayer();
            player.getAbilities().allowFlying = !player.getAbilities().allowFlying;
            if (!player.getAbilities().allowFlying) player.getAbilities().flying = false;
            player.sendAbilitiesUpdate();
            player.getDataTracker().set(MoreCommands.MAY_FLY, player.getAbilities().allowFlying);
            sendMsg(player, "You can " + formatFromBool(player.getAbilities().allowFlying, "now", "no longer") + DF + " fly.");
            return 1;
        }));
    }
}
