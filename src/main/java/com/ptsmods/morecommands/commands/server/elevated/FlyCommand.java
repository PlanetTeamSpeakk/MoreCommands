package com.ptsmods.morecommands.commands.server.elevated;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

import java.util.Collection;

public class FlyCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("fly").requires(IS_OP).executes(ctx -> {
            PlayerEntity player = ctx.getSource().getPlayer();
            player.abilities.allowFlying = !player.abilities.allowFlying;
            if (!player.abilities.allowFlying) player.abilities.flying = false;
            player.sendAbilitiesUpdate();
            player.getDataTracker().set(MoreCommands.MAY_FLY, player.abilities.allowFlying);
            sendMsg(player, "You can " + formatFromBool(player.abilities.allowFlying, "now", "no longer") + DF + " fly.");
            return 1;
        }));
    }
}
