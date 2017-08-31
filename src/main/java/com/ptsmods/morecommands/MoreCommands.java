package com.ptsmods.morecommands;

import java.io.IOException;

import com.ptsmods.morecommands.miscellaneous.ClientEventHandler;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.IncorrectCommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;
import com.ptsmods.morecommands.miscellaneous.ServerEventHandler;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid=Reference.MOD_ID, name=Reference.MOD_NAME, version=Reference.VERSION, acceptedMinecraftVersions=Reference.MC_VERSIONS, updateJSON=Reference.UPDATE_URL)
public class MoreCommands {

	private static boolean initialized = false;

	public MoreCommands() {
		initialize();
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		initialize();
		try {
			Reference.resetCommandRegistry(CommandType.SERVER); // for when you relog and a new command has been added, only needed for development.
			Reference.resetCommandRegistry(CommandType.CLIENT);
			Initialize.setupCommandRegistry();
		} catch (IncorrectCommandType e) {
			e.printStackTrace();
		}
		Initialize.registerCommands(event);
		Initialize.setupBlockLists();
		try {
			Reference.registerEventHandler(CommandType.SERVER, new ServerEventHandler());
		} catch (IncorrectCommandType e) {}
		Reference.setServerStartingEvent(event);
	}

	@EventHandler
	@SideOnly(Side.CLIENT)
	public void postInit(FMLPostInitializationEvent event) {
		Initialize.registerClientCommands();
	}

	@EventHandler
	@SideOnly(Side.CLIENT)
	public void preInit(FMLPreInitializationEvent event) {
		initialize();
		Reference.createFileIfNotExisting("config/MoreCommands/infoOverlay.txt");
		Reference.createFileIfNotExisting("config/MoreCommands/aliases.yaml");
		try {
			Reference.loadInfoOverlayConfig();
			Reference.loadAliases();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Reference.setupKeyBindingRegistry();
		try {
			Reference.registerEventHandler(CommandType.CLIENT, new ClientEventHandler());
		} catch (IncorrectCommandType e) {}
	}

	private static void initialize() {
		if (!initialized) {
			//			try {
			//				Downloader.downloadDependency("http://central.maven.org/maven2/org/javassist/javassist/3.22.0-CR2/javassist-3.22.0-CR2.jar", "javassist.jar");
			//				if (new File("libs/reflections.jar").exists()) new File("libs/reflections.jar").delete(); // getting ready to downgrade to 0.9.10 since 0.9.11 gives an error.
			//				Downloader.downloadDependency("http://central.maven.org/maven2/org/reflections/reflections/0.9.10/reflections-0.9.10.jar", "reflections.jar");
			//				Downloader.downloadDependency("http://central.maven.org/maven2/org/yaml/snakeyaml/1.18/snakeyaml-1.18.jar", "snakeyaml.jar");
			//				Downloader.downloadDependency("http://central.maven.org/maven2/com/google/guava/guava/23.0/guava-23.0.jar", "guava.jar");
			//			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
			//					| InvocationTargetException | IOException e2) {
			//				e2.printStackTrace();
			//			} // using shadowJar instead now.
			//			try {
			//				Downloader.addJarToClasspath("libs/javassist.jar");
			//				Downloader.addJarToClasspath("libs/reflections.jar");
			//				Downloader.addJarToClasspath("libs/snakeyaml.jar");
			//				Downloader.addJarToClasspath("libs/guava.jar");
			//			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
			//					| InvocationTargetException | MalformedURLException e) {
			//				e.printStackTrace();
			//			} just let forge handle it.
			Reference.initialize();
			initialized = true;
		}
	}

}
