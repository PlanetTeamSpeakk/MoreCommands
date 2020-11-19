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

public class GodCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("god").requires(IS_OP).executes(ctx -> {
            PlayerEntity player = ctx.getSource().getPlayer();
            player.abilities.invulnerable = !player.abilities.invulnerable;
            player.sendAbilitiesUpdate();
            player.getDataTracker().set(MoreCommands.INVULNERABLE, player.abilities.invulnerable);
            sendMsg(player, "You're now " + formatFromBool(player.abilities.invulnerable, "in", "") + "vulnerable" + DF + ".");
            return 1;
        }));
    }
}
