package com.ptsmods.morecommands;

import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid=Reference.MOD_ID, name=Reference.MOD_NAME, version=Reference.VERSION, acceptedMinecraftVersions=Reference.MC_VERSIONS, updateJSON=Reference.UPDATE_URL)
public class MoreCommands {
	
	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		Initialize.registerCommands(event);
		Reference.setServerStartingEvent(event);
	}
	
	@EventHandler
	@SideOnly(Side.CLIENT)
	public void postInit(FMLPostInitializationEvent event) {
        try {
        	Initialize.registerClientCommands();
        } catch (NoClassDefFoundError e) {
        	System.out.println("An error occured while loading the client sided MoreCommands commands, if the mod is installed on a server you can ignore this error, if not please contact PlanetTeamSpeak.");
        	System.out.println(e.getStackTrace());
        }
	}

}
