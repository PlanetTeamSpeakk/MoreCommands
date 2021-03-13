package com.ptsmods.morecommands;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import org.apache.logging.log4j.LogManager;

public class EarlyRiser implements Runnable {
    @Override
    public void run() {
        // This works on Java 9+ too, whereas the old method of creating a new enum instance, using lots of reflection and reflecting reflection, does not.
        MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();
        ClassTinkerers.enumBuilder(remapper.mapClassName("intermediary", "net.minecraft.class_124"), String.class, char.class, boolean.class).addEnum("RAINBOW", "RAINBOW", 'u', true).build();
        ClassTinkerers.enumBuilder(remapper.mapClassName("intermediary", "net.minecraft.class_2558$class_2559"), String.class, boolean.class).addEnum("SCROLL", "scroll", false).build();
        LogManager.getLogger("MoreCommands-EarlyRiser").info("[MoreCommands] Registered RAINBOW formatting and SCROLL ClickEvent$Action.");
    }
}