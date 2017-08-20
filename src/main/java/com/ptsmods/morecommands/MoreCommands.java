package com.ptsmods.morecommands;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.Display;

import com.ptsmods.morecommands.miscellaneous.ClientEventHandler;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.IncorrectCommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;
import com.ptsmods.morecommands.miscellaneous.ServerEventHandler;

import net.minecraftforge.common.MinecraftForge;
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
		Reference.setupBiomeList();
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
		try {
			Reference.registerEventHandler(CommandType.CLIENT, new ClientEventHandler());
		} catch (IncorrectCommandType e) {}
	}
	
}
