package com.ptsmods.morecommands.commands.server.elevated;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.api.IDataTrackerHelper;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.mixin.common.accessor.MixinPlayerAbilitiesAccessor;
import com.ptsmods.morecommands.mixin.common.accessor.MixinPlayerEntityAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.*;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class SpeedCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = literalReqOp("speed");
        for (SpeedType type : SpeedType.values())
            builder.then(literal(type.name().toLowerCase())
                    .executes(ctx -> getSpeed(ctx, type, null))
                    .then(argument("target", EntityArgument.player())
                            .executes(ctx -> getSpeed(ctx, type, EntityArgument.getPlayer(ctx, "target"))))
                    .then(argument("speed", FloatArgumentType.floatArg(0))
                            .executes(ctx -> setSpeed(ctx, type, ctx.getArgument("speed", Float.class), null))
                            .then(argument("targets", EntityArgument.players())
                                    .requires(hasPermissionOrOp("morecommands.speed.others"))
                                    .executes(ctx -> setSpeed(ctx, type, ctx.getArgument("speed", Float.class), EntityArgument.getPlayers(ctx, "targets"))))));
        dispatcher.register(builder
                .then(argument("speed", FloatArgumentType.floatArg(0))
                        .executes(ctx -> setSpeed(ctx, determineSpeedType(ctx.getSource().getPlayerOrException()), ctx.getArgument("speed", Float.class), null))));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/speed";
    }

    private SpeedType determineSpeedType(ServerPlayer player) {
        return player.isUnderWater() ? SpeedType.SWIM : ((MixinPlayerEntityAccessor) player).getAbilities_().flying ? SpeedType.FLY : SpeedType.WALK;
    }

    private int setSpeed(CommandContext<CommandSourceStack> ctx, SpeedType type, float speed, Collection<ServerPlayer> players) throws CommandSyntaxException {
        if (players == null) players = Lists.newArrayList(ctx.getSource().getPlayerOrException());
        else if (!isOp(ctx)) {
            sendError(ctx, "You must be op to set other's speed.");
            return 0;
        }
        for (ServerPlayer p : players) {
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

    private int getSpeed(CommandContext<CommandSourceStack> ctx, SpeedType type, ServerPlayer player) throws CommandSyntaxException {
        boolean notYou = player != null;
        if (!notYou) player = ctx.getSource().getPlayerOrException();
        double speed = type.getSpeed(player);
        String s = IMoreCommands.get().textToString(player.getDisplayName(), SS, true);
        sendMsg(ctx, (notYou ? s + SF + "'" + (Objects.requireNonNull(ChatFormatting.stripFormatting(s)).endsWith("s") ? "" : "s") : "Your") + " " + SF + type.name().toLowerCase() + " speed " + DF + "is currently " + SF + speed + DF + ".");
        return (int) speed;
    }

    public enum SpeedType {
        WALK((player, speed) -> {
            UUID speedModId = player.getEntityData().get(IDataTrackerHelper.get().speedModifier()).orElseThrow(() -> new AssertionError("This shouldn't happen."));
            AttributeInstance attr = Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED));
            attr.removeModifier(speedModId);
            attr.addPermanentModifier(new AttributeModifier(speedModId, "MoreCommands Speed Modifier", speed - 1, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }, player -> Optional.ofNullable(Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)).getModifier(player.getEntityData()
                .get(IDataTrackerHelper.get().speedModifier()).orElseThrow(() -> new AssertionError("This shouldn't happen."))))
                .map(attr -> attr.getAmount() + 1).orElse(1D)),
        FLY((player, speed) -> {
            ((MixinPlayerAbilitiesAccessor) ((MixinPlayerEntityAccessor) player).getAbilities_()).setFlyingSpeed_((float) (speed / 20));
            player.onUpdateAbilities();
        }, player -> ((MixinPlayerEntityAccessor) player).getAbilities_().getFlyingSpeed() * 20D),
        SWIM((player, speed) -> Objects.requireNonNull(player.getAttribute(Nested.swimSpeedAttribute)).setBaseValue(speed), player -> player.getAttributeValue(Nested.swimSpeedAttribute));

        public static final Attribute swimSpeedAttribute = Nested.swimSpeedAttribute;

        private final BiConsumer<ServerPlayer, Double> consumer;
        private final Function<ServerPlayer, Double> supplier;

        SpeedType(BiConsumer<ServerPlayer, Double> consumer, Function<ServerPlayer, Double> supplier) {
            this.consumer = consumer;
            this.supplier = supplier;
        }

        public void setSpeed(ServerPlayer player, double speed) {
            consumer.accept(player, speed);
        }

        public double getSpeed(ServerPlayer player) {
            return supplier.apply(player);
        }

        private static class Nested { // bla-bla forward references bla-bla ugh
            public static final Attribute swimSpeedAttribute = new RangedAttribute("attribute.morecommands.swim_speed", 1f, 0f, Float.MAX_VALUE).setSyncable(true);
        }
    }
}
