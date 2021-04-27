package com.ptsmods.morecommands;

import com.chocohead.mm.api.ClassTinkerers;
import com.google.gson.Gson;
import com.ptsmods.morecommands.miscellaneous.MinecraftVersionData;
import com.ptsmods.morecommands.miscellaneous.MinecraftVersionDataLegacy;
import com.ptsmods.morecommands.miscellaneous.MinecraftVersionDataNew;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import org.apache.logging.log4j.LogManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

public class EarlyRiser implements Runnable {
	public static final MinecraftVersionData version;

	static {
		MinecraftVersionData data;
		try {
			data = new Gson().fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(EarlyRiser.class.getResourceAsStream("/version.json")))), MinecraftVersionDataLegacy.class);
		} catch (Exception e) {
			try {
				data = new Gson().fromJson(new BufferedReader(new InputStreamReader(Objects.requireNonNull(EarlyRiser.class.getResourceAsStream("/version.json")))), MinecraftVersionDataNew.class);
			} catch (Exception e0) {
				throw new RuntimeException("Couldn't load version data.", e);
			}
		}
		version = data;
	}

	@Override
	public void run() {
		// This works on Java 9+ too, whereas the old method of creating a new enum instance, using lots of reflection and reflecting reflection, does not.
		MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();
		ClassTinkerers.enumBuilder(remapper.mapClassName("intermediary", "net.minecraft.class_124"), String.class, char.class, boolean.class).addEnum("RAINBOW", "RAINBOW", 'u', true).build();
		ClassTinkerers.enumBuilder(remapper.mapClassName("intermediary", "net.minecraft.class_2558$class_2559"), String.class, boolean.class).addEnum("SCROLL", "scroll", false).build();
		LogManager.getLogger("MoreCommands-EarlyRiser").info("[MoreCommands] Registered RAINBOW Formatting and SCROLL ClickEvent$Action.");
	}
}
