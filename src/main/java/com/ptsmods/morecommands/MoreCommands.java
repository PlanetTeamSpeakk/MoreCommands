package com.ptsmods.morecommands;

import java.io.File;
import java.io.IOException;

import org.lwjgl.opengl.Display;

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
	
	public MoreCommands() {
		try {
			Reference.setDisplayTitle(Display.getTitle() + " with MinecraftForge");
		} catch (NoClassDefFoundError e) {}
		Reference.downloadDependency("http://central.maven.org/maven2/org/javassist/javassist/3.22.0-CR2/javassist-3.22.0-CR2.jar", "javassist.jar");
		Reference.downloadDependency("http://central.maven.org/maven2/org/reflections/reflections/0.9.11/reflections-0.9.11.jar", "reflections.jar");
		Reference.downloadDependency("http://central.maven.org/maven2/org/yaml/snakeyaml/1.18/snakeyaml-1.18.jar", "snakeyaml.jar"); 
		Reference.setupBiomeList();
		if (!new File("config/MoreCommands/").isDirectory()) new File("config/MoreCommands/").mkdirs();
		if (!new File("config/MoreCommands/homes.yaml").exists())
			try {
				new File("config/MoreCommands/homes.yaml").createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		if (!new File("config/MoreCommands/warps.yaml").exists())
			try {
				new File("config/MoreCommands/warps.yaml").createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		try {
			Reference.loadWarpsFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		try {
			Reference.resetCommandRegistry(CommandType.SERVER); // for when you relog and a new command has been added, only needed for development.
			Reference.resetCommandRegistry(CommandType.CLIENT);
			Initialize.setupCommandRegistry();
		} catch (IncorrectCommandType e) {
			e.printStackTrace();
		}
		if (Reference.shouldRegisterCommands) Initialize.registerCommands(event);
		Initialize.setupBlockLists();
		try {
			Reference.registerEventHandler(CommandType.SERVER, new ServerEventHandler());
		} catch (IncorrectCommandType e) {}
		Reference.setServerStartingEvent(event);
	}
	
	@EventHandler
	@SideOnly(Side.CLIENT)
	public void postInit(FMLPostInitializationEvent event) {
		Initialize.setupCommandRegistry();
    	if (Reference.shouldRegisterCommands) Initialize.registerClientCommands();
	}
	
	@EventHandler
	@SideOnly(Side.CLIENT)
	public void preInit(FMLPreInitializationEvent event) {
		if (!new File("config/MoreCommands/infoOverlay.txt").exists())
			try {
				new File("config/MoreCommands/infoOverlay.txt").createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		try {
			Reference.loadInfoOverlayConfig();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Reference.setupKeyBindingRegistry();
		try {
			Reference.registerEventHandler(CommandType.CLIENT, new ClientEventHandler());
		} catch (IncorrectCommandType e) {}
	}
	
}
