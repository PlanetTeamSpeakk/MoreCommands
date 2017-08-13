package com.ptsmods.morecommands;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.Display;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.IncorrectCommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;
import com.ptsmods.morecommands.miscellaneous.CrashedOnPurpose;

import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid=Reference.MOD_ID, name=Reference.MOD_NAME, version=Reference.VERSION, acceptedMinecraftVersions=Reference.MC_VERSIONS, updateJSON=Reference.UPDATE_URL)
public class MoreCommands {
	
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
		Initialize.registerEvenHandlers();
		Reference.setServerStartingEvent(event);
	}
	
	@EventHandler
	@SideOnly(Side.CLIENT)
	public void postInit(FMLPostInitializationEvent event) {
		Initialize.setupCommandRegistry();
    	if (Reference.shouldRegisterCommands) Initialize.registerClientCommands();
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Reference.setDisplayTitle(Display.getTitle() + " with MinecraftForge");
		if (!new File("mods/javassist.jar").exists()) {	
			System.out.println("Could not find javassist.jar file, download it now...");
			Map<String, String> downloaded = new HashMap<String, String>();
			downloaded.put("fileLocation", "");
			downloaded.put("success", "false");
			try {
				downloaded = Reference.downloadFile("https://raw.githubusercontent.com/PlanetTeamSpeakk/MoreCommands/master/javassist.jar", "mods/javassist.jar");
			} catch (NullPointerException | MalformedURLException e) {
				System.err.println("Javassist.jar could not be downloaded, thus MoreCommands cannot be used.");
			}
			if (!Boolean.parseBoolean(downloaded.get("success"))) {
				System.err.println("Javassist.jar could not be downloaded, thus MoreCommands cannot be used.");
			} else {
				System.out.println("Successfully download javassist.jar.");
			}
			Reference.shouldRegisterCommands = false; // The game has to be restarted for the mod to see the files.
		}
		
		if (!new File("mods/reflections.jar").exists()) {
			System.out.println("Could not find reflections.jar file, download it now...");
			Map<String, String> downloaded = new HashMap<String, String>();
			downloaded.put("fileLocation", "");
			downloaded.put("success", "false");
			try {
				downloaded = Reference.downloadFile("https://raw.githubusercontent.com/PlanetTeamSpeakk/MoreCommands/master/reflections.jar", "mods/reflections.jar");
			} catch (NullPointerException | MalformedURLException e) {
				System.err.println("Reflections.jar could not be downloaded, thus MoreCommands cannot be used.");
			}
			if (!Boolean.parseBoolean(downloaded.get("success"))) {
				System.err.println("Reflections.jar could not be downloaded, thus MoreCommands cannot be used.");
			} else {
				System.out.println("Successfully download reflections.jar.");
			}
			Reference.shouldRegisterCommands = false; // The game has to be restarted for the mod to see the files.
		}
		
	}
	
}
