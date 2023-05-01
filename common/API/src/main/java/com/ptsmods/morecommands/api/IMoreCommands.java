package com.ptsmods.morecommands.api;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public interface IMoreCommands {
    Logger LOG = LogManager.getLogger("MoreCommands");
    Map<String, DamageSource> DAMAGE_SOURCES = new HashMap<>();

    static IMoreCommands get() {
        return Holder.getMoreCommands();
    }

    ChatFormatting getDefaultFormatting();

    ChatFormatting getSecondaryFormatting();

    boolean isServerOnly();

    MinecraftServer getServer();

    Path getConfigDirectory();

    default String textToString(Component text) {
        return textToString(text, null, true);
    }

    String textToString(Component text, Style parentStyle, boolean includeFormattings);

    boolean isCreatingWorld();

    void setCreatingWorld(boolean creatingWorld);

    Path getJar();

    Supplier<SoundEvent> getCopySound();
    Supplier<SoundEvent> getEESound();

    Supplier<Attribute> getReachAttribute();

    Supplier<Attribute> getSwimSpeedAttribute();

    void registerAttributes(boolean addToSupplier);

    void registerPermission(String permission, boolean defaultValue);

    double getReach(Player player, boolean squared);
}
