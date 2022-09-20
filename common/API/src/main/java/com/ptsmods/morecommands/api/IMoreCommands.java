package com.ptsmods.morecommands.api;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public interface IMoreCommands {
    Logger LOG = LogManager.getLogger("MoreCommands");

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

    RegistrySupplier<SoundEvent> getCopySound();
    RegistrySupplier<SoundEvent> getEESound();

    RegistrySupplier<Attribute> getReachAttribute();

    RegistrySupplier<Attribute> getSwimSpeedAttribute();

    void registerAttributes(boolean addToSupplier);
}
