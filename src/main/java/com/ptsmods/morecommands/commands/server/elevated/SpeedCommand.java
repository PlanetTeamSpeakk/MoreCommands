package com.ptsmods.morecommands.commands.server.elevated;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.miscellaneous.SpeedType;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

import java.util.Collection;

public class SpeedCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> builder = literal("speed").requires(IS_OP);
        for (SpeedType type : SpeedType.values())
            builder.then(literal(type.name().toLowerCase()).executes(ctx -> getSpeed(ctx, type, null)).then(argument("target", EntityArgumentType.player()).executes(ctx -> getSpeed(ctx, type, EntityArgumentType.getPlayer(ctx, "target")))).then(argument("speed", FloatArgumentType.floatArg(0)).executes(ctx -> setSpeed(ctx, type, ctx.getArgument("speed", Float.class), null)).then(argument("targets", EntityArgumentType.players()).executes(ctx -> setSpeed(ctx, type, ctx.getArgument("speed", Float.class), EntityArgumentType.getPlayers(ctx, "targets"))))));
        dispatcher.register(builder.then(argument("speed", FloatArgumentType.floatArg(0)).executes(ctx -> setSpeed(ctx, determineSpeedType(ctx.getSource().getPlayer()), ctx.getArgument("speed", Float.class), null))));
    }

    private SpeedType determineSpeedType(ServerPlayerEntity player) {
        return player.isSubmergedInWater() ? SpeedType.SWIM : player.abilities.flying ? SpeedType.FLY : SpeedType.WALK;
    }

    private int setSpeed(CommandContext<ServerCommandSource> ctx, SpeedType type, float speed, Collection<ServerPlayerEntity> players) throws CommandSyntaxException {
        if (players == null) players = Lists.newArrayList(ctx.getSource().getPlayer());
        else if (!isOp(ctx)) {
            sendMsg(ctx, Formatting.RED + "You must be op to set other's speed.");
            return 0;
        }
        for (ServerPlayerEntity p : players) {
            float old = type.getSpeed(p);
            type.setSpeed(p, speed);
            sendMsg(p, "Your " + SF + type.name().toLowerCase() + " speed " + DF + "has been set from " + SF + old + DF + " to " + SF + type.getSpeed(p) + DF + ".");
        }
        return players.size();
    }

    private int getSpeed(CommandContext<ServerCommandSource> ctx, SpeedType type, ServerPlayerEntity player) throws CommandSyntaxException {
        boolean notYou = player != null;
        if (!notYou) player = ctx.getSource().getPlayer();
        float speed = type.getSpeed(player);
        String s = MoreCommands.textToString(player.getDisplayName(), SS);
        sendMsg(ctx, (notYou ? s + SF + "'" + (Formatting.strip(s).endsWith("s") ? "" : "s") : "Your") + " " + SF + type.name().toLowerCase() + " speed " + DF + "is currently " + SF + speed + DF + ".");
        return (int) speed;
    }

}
