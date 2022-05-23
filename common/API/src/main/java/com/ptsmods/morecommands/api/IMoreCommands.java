package com.ptsmods.morecommands.api;

import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public interface IMoreCommands {
    Logger LOG = LogManager.getLogger("MoreCommands");

    static IMoreCommands get() {
        return Holder.getMoreCommands();
    }

    Formatting getDefaultFormatting();

    Formatting getSecondaryFormatting();

    boolean isServerOnly();

    MinecraftServer getServer();

    Path getConfigDirectory();

    String textToString(Text text, Style parentStyle, boolean includeFormattings);

    void setCreatingWorld(boolean creatingWorld);

    Path getJar();
}
