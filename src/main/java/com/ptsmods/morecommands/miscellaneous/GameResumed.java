package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class GameResumed extends Event {

	private GuiScreen currentScreen;

	public GameResumed(GuiScreen currentScreen) {
		this.currentScreen = currentScreen;
	}

	public Gui getGUI() {
		return currentScreen;
	}

}
