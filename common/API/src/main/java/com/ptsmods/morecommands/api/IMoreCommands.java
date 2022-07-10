package com.ptsmods.morecommands.api;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
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

    String textToString(Component text, Style parentStyle, boolean includeFormattings);

    void setCreatingWorld(boolean creatingWorld);

    Path getJar();
}
