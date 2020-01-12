package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ScreenChangedEvent extends Event {

	public final GuiScreen from, to;

	public ScreenChangedEvent(GuiScreen from, GuiScreen to) {
		this.from = from;
		this.to = to;
	}

}
