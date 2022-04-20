package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class ReachCommand extends Command {
	public static final EntityAttribute reachAttribute = new ClampedEntityAttribute("attribute.morecommands.reach", 4.5d, 1d, 160d).setTracked(true);
	private static final UUID morecommandsModifier = UUID.nameUUIDFromBytes("morecommands".getBytes(StandardCharsets.UTF_8));

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		if (IMoreCommands.get().isServerOnly()) dispatcher.register(literalReqOp("reach").executes(ctx -> {
			sendError(ctx, "Reach cannot be used in server only mode.");
			return 0;
		}));
		else dispatcher.register(literalReqOp("reach").executes(ctx -> {
			double reach = ctx.getSource().getPlayer().getAttributeValue(reachAttribute);
			double base = ctx.getSource().getPlayer().getAttributeBaseValue(reachAttribute);
			sendMsg(ctx, "Your reach is currently " + SF + reach + DF + (base != reach ? " (" + SF + base + " base" + DF + ")" : "") + ".");
			return (int) reach;
		}).then(argument("reach", DoubleArgumentType.doubleArg(1d, 160d)).executes(ctx -> {
			double oldReach = ctx.getSource().getPlayer().getAttributeBaseValue(reachAttribute);
			double reach = ctx.getArgument("reach", Double.class);
			ServerPlayerEntity player = ctx.getSource().getPlayer();
			Objects.requireNonNull(player.getAttributeInstance(reachAttribute)).setBaseValue(reach);
			addModifier("pehkui:reach", player, reach);
			addModifier("reach-entity-attributes:reach", player, reach);
			sendMsg(ctx, "Your reach has been set from " + SF + oldReach + DF + " to " + SF + reach + DF + ".");
			return (int) reach;
		})));
	}

	private void addModifier(String id, ServerPlayerEntity player, double reach) {
		Optional.ofNullable(Registry.ATTRIBUTE.get(new Identifier(id)))
				.map(player::getAttributeInstance)
				.ifPresent(atr -> {
					atr.tryRemoveModifier(morecommandsModifier);
					atr.addPersistentModifier(new EntityAttributeModifier(morecommandsModifier, "MoreCommands Modifier", reach / 4.5D, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
				});
	}

	public static double getReach(PlayerEntity player, boolean squared) {
		return Math.pow(player.getAttributeValue(reachAttribute) + (player instanceof ServerPlayerEntity && squared ? 1.5d : 0), squared ? 2 : 1);
	}
}
