package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Objects;

public class ReachCommand extends Command {
	public static final EntityAttribute reachAttribute = new ClampedEntityAttribute("attribute.morecommands.reach", 4.5d, 1d, 160d).setTracked(true);

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literalReqOp("reach").executes(ctx -> {
			double reach = ctx.getSource().getPlayer().getAttributeValue(reachAttribute);
			double base = ctx.getSource().getPlayer().getAttributeBaseValue(reachAttribute);
			sendMsg(ctx, "Your reach is currently " + SF + reach + DF + (base != reach ? " (" + SF + base + " base" + DF + ")" : "") + ".");
			return (int) reach;
		}).then(argument("reach", DoubleArgumentType.doubleArg(1d, 160d)).executes(ctx -> {
			double oldReach = ctx.getSource().getPlayer().getAttributeBaseValue(reachAttribute);
			double reach = ctx.getArgument("reach", Double.class);
			Objects.requireNonNull(ctx.getSource().getPlayer().getAttributeInstance(reachAttribute)).setBaseValue(reach);
			sendMsg(ctx, "Your reach has been set from " + SF + oldReach + DF + " to " + SF + reach + DF + ".");
			return (int) reach;
		})));
	}

	public static double getReach(PlayerEntity player, boolean squared) {
		return Math.pow(player.getAttributeValue(reachAttribute) + (player instanceof ServerPlayerEntity ? 1.5d : 0), squared ? 2 : 1);
	}
}
