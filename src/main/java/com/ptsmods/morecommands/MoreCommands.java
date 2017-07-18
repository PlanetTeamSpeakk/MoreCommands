package com.ptsmods.morecommands;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid=Reference.MOD_ID, name=Reference.MOD_NAME, version=Reference.VERSION)
public class MoreCommands {
	
	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		Initialize.registerCommands(event);
		Reference.setServerStartingEvent(event);
	}
	
	@EventHandler
	@SideOnly(Side.CLIENT)
	public void preInit(FMLPreInitializationEvent event) {
        try {
        	Initialize.registerClientCommands();
        } catch (NoClassDefFoundError e) {
        	System.out.println("An error occured while loading the client sided MoreCommands commands, if the mod is installed on a server you can ignore this error, if not please contact PlanetTeamSpeak.");
        	System.out.println(e.getStackTrace());
        }
	}
	
//	@EventHandler
//	public void init(FMLInitializationEvent event) {

//	}
	
//	@EventHandler
//	public void postInit(FMLPostInitializationEvent event) {
		
//	}
}
