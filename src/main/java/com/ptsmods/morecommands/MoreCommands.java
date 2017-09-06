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

	public MoreCommands() {
		Reference.initialize();
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

}
