package com.ptsmods.morecommands.miscellaneous;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.resources.I18n;

public class GuiIngameMenu extends net.minecraft.client.gui.GuiIngameMenu {

	private Map<Integer, String> buttonDescriptions = new HashMap<>();

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(new GuiButton(3, width/2 - 100, height/4 + 128, I18n.format("menu.quit")));
		buttonList.add(new GuiButton(2, width/2 - 100, height/4 + 152, I18n.format("menu.moreCommands")));
		for (GuiButton button : buttonList)
			button.y -= 20;
		for (int x : Reference.range(13))
			buttonDescriptions.put(x, I18n.format("menu.descs." + x));
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 2) mc.displayGuiScreen(new GuiMoreCommands(this));
		else if (button.id == 3) {
			button.enabled = false;
			super.actionPerformed(button);
			mc.shutdown();
		}
		else super.actionPerformed(button);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		drawDefaultBackground();
		drawCenteredString(fontRenderer, I18n.format("menu.game"), width / 2, 20, 16777215);
		for (GuiButton button : buttonList)
			button.drawButton(mc, mouseX, mouseY, partialTicks);
		for (GuiButton button : buttonList) // creating a second for loop so the hovering text gets drawn correctly
			if (button.isMouseOver())
				drawHoveringText(buttonDescriptions.get(button.id), mouseX, mouseY+8);
		for (GuiLabel label : labelList)
			label.drawLabel(mc, mouseX, mouseY);
	}

}
