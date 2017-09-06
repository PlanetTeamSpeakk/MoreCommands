package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class GameResumed extends Event {

	public Gui getGUI() { // null most likely
		return Minecraft.getMinecraft().currentScreen;
	}

}
