package com.ptsmods.morecommands.commands.server.elevated;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.mixin.common.accessor.MixinPlayerAbilitiesAccessor;
import com.ptsmods.morecommands.util.DataTrackerHelper;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.attribute.*;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class SpeedCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> builder = literalReqOp("speed");
		for (SpeedType type : SpeedType.values())
			builder.then(literal(type.name().toLowerCase()).executes(ctx -> getSpeed(ctx, type, null)).then(argument("target", EntityArgumentType.player()).executes(ctx -> getSpeed(ctx, type, EntityArgumentType.getPlayer(ctx, "target")))).then(argument("speed", FloatArgumentType.floatArg(0)).executes(ctx -> setSpeed(ctx, type, ctx.getArgument("speed", Float.class), null)).then(argument("targets", EntityArgumentType.players()).executes(ctx -> setSpeed(ctx, type, ctx.getArgument("speed", Float.class), EntityArgumentType.getPlayers(ctx, "targets"))))));
		dispatcher.register(builder.then(argument("speed", FloatArgumentType.floatArg(0)).executes(ctx -> setSpeed(ctx, determineSpeedType(ctx.getSource().getPlayer()), ctx.getArgument("speed", Float.class), null))));
	}

	private SpeedType determineSpeedType(ServerPlayerEntity player) {
		return player.isSubmergedInWater() ? SpeedType.SWIM : Compat.getCompat().getAbilities(player).flying ? SpeedType.FLY : SpeedType.WALK;
	}

	private int setSpeed(CommandContext<ServerCommandSource> ctx, SpeedType type, float speed, Collection<ServerPlayerEntity> players) throws CommandSyntaxException {
		if (players == null) players = Lists.newArrayList(ctx.getSource().getPlayer());
		else if (!isOp(ctx)) {
			sendError(ctx, "You must be op to set other's speed.");
			return 0;
		}
		for (ServerPlayerEntity p : players) {
			double old = type.getSpeed(p);
			try {
				type.setSpeed(p, speed);
			} catch (Exception e) {
				log.catching(e);
			}
			sendMsg(p, "Your " + SF + type.name().toLowerCase() + " speed " + DF + "has been set from " + SF + old + DF + " to " + SF + type.getSpeed(p) + DF + ".");
		}
		return players.size();
	}

	private int getSpeed(CommandContext<ServerCommandSource> ctx, SpeedType type, ServerPlayerEntity player) throws CommandSyntaxException {
		boolean notYou = player != null;
		if (!notYou) player = ctx.getSource().getPlayer();
		double speed = type.getSpeed(player);
		String s = MoreCommands.textToString(player.getDisplayName(), SS, true);
		sendMsg(ctx, (notYou ? s + SF + "'" + (Objects.requireNonNull(Formatting.strip(s)).endsWith("s") ? "" : "s") : "Your") + " " + SF + type.name().toLowerCase() + " speed " + DF + "is currently " + SF + speed + DF + ".");
		return (int) speed;
	}

	public enum SpeedType {
		WALK((player, speed) -> {
			UUID speedModId = player.getDataTracker().get(DataTrackerHelper.SPEED_MODIFIER).orElseThrow(() -> new AssertionError("This shouldn't happen."));
			EntityAttributeInstance attr = Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED));
			attr.removeModifier(speedModId);
			attr.addPersistentModifier(new EntityAttributeModifier(speedModId, "MoreCommands Speed Modifier", speed - 1, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
		}, player -> Optional.ofNullable(Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).getModifier(player.getDataTracker().get(DataTrackerHelper.SPEED_MODIFIER).orElseThrow(() -> new AssertionError("This shouldn't happen.")))).map(attr -> attr.getValue() + 1).orElse(1D)),
		FLY((player, speed) -> {
			((MixinPlayerAbilitiesAccessor) Compat.getCompat().getAbilities(player)).setFlySpeed_((float) (speed / 20));
			player.sendAbilitiesUpdate();
		}, player -> Compat.getCompat().getAbilities(player).getFlySpeed() * 20D),
		SWIM((player, speed) -> Objects.requireNonNull(player.getAttributeInstance(Nested.swimSpeedAttribute)).setBaseValue(speed), player -> player.getAttributeValue(Nested.swimSpeedAttribute));

		public static final EntityAttribute swimSpeedAttribute = Nested.swimSpeedAttribute;

		private final BiConsumer<ServerPlayerEntity, Double> consumer;
		private final Function<ServerPlayerEntity, Double> supplier;

		SpeedType(BiConsumer<ServerPlayerEntity, Double> consumer, Function<ServerPlayerEntity, Double> supplier) {
			this.consumer = consumer;
			this.supplier = supplier;
		}

		public void setSpeed(ServerPlayerEntity player, double speed) {
			consumer.accept(player, speed);
		}

		public double getSpeed(ServerPlayerEntity player) {
			return supplier.apply(player);
		}

		private static class Nested { // bla-bla forward references bla-bla ugh
			public static final EntityAttribute swimSpeedAttribute = new ClampedEntityAttribute("attribute.morecommands.swim_speed", 1f, 0f, Float.MAX_VALUE).setTracked(true);
		}
	}
}
