package com.ptsmods.morecommands.miscellaneous;

import com.ptsmods.morecommands.MoreCommands;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.server.network.ServerPlayerEntity;

import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.Function;

public enum SpeedType {
    WALK((player, speed) -> {
        if (Nested.walkSpeedField == null) {
            Nested.walkSpeedField = MoreCommands.getYarnField(PlayerAbilities.class, "walkSpeed", "field_7482");
            Nested.walkSpeedField.setAccessible(true);
        }
        try {
            Nested.walkSpeedField.set(player.abilities, speed / 10);
        } catch (IllegalAccessException e) {
            MoreCommands.log.catching(e);
        }
        player.sendAbilitiesUpdate();
    }, player -> player.abilities.getWalkSpeed() * 10),
    FLY((player, speed) -> {
        if (Nested.flySpeedField == null) {
            Nested.flySpeedField = MoreCommands.getYarnField(PlayerAbilities.class, "flySpeed", "field_7481");
            Nested.flySpeedField.setAccessible(true);
        }
        try {
            Nested.flySpeedField.set(player.abilities, speed / 20);
        } catch (IllegalAccessException e) {
            MoreCommands.log.catching(e);
        }
        player.sendAbilitiesUpdate();
    }, player -> player.abilities.getFlySpeed() * 20),
    SWIM((player, speed) -> {
        player.getAttributeInstance(Nested.swimSpeedAttribute).setBaseValue(speed);
    }, player -> (float) player.getAttributeValue(Nested.swimSpeedAttribute));

    public static final EntityAttribute swimSpeedAttribute = Nested.swimSpeedAttribute;

    private final BiConsumer<ServerPlayerEntity, Float> consumer;
    private final Function<ServerPlayerEntity, Float> supplier;

    SpeedType(BiConsumer<ServerPlayerEntity, Float> consumer, Function<ServerPlayerEntity, Float> supplier) {
        this.consumer = consumer;
        this.supplier = supplier;
    }

    public void setSpeed(ServerPlayerEntity player, float speed) {
        consumer.accept(player, speed);
    }

    public float getSpeed(ServerPlayerEntity player) {
        return supplier.apply(player);
    }

    private static class Nested { // bla-bla forward references bla-bla ugh
        private static Field walkSpeedField, flySpeedField;
        public static final EntityAttribute swimSpeedAttribute = new ClampedEntityAttribute("attribute.morecommands.swim_speed", 1f, 0f, Float.MAX_VALUE).setTracked(true);
    }
}
