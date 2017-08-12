package com.ptsmods.morecommands.miscellaneous;

import java.util.List;

import net.minecraft.client.Minecraft;

public class GuiOverlayDebug extends net.minecraft.client.gui.GuiOverlayDebug {

	public GuiOverlayDebug(Minecraft mc) {
		super(mc);
	}
	
	@Override
	public List<String> call() { // making the protected function public.
		return super.call();
	}

}
