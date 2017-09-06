package com.ptsmods.morecommands.miscellaneous;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;

public class GuiMoreCommands extends GuiScreen {

	private final GuiScreen lastScreen;
	private final Map<Integer, Map<String, Integer>> urls = new HashMap<>();
	private final Map<Integer, String> urls1 = new HashMap<>();

	public GuiMoreCommands(GuiScreen lastScreen) {
		this.lastScreen = lastScreen;
	}

	@Override
	public void initGui() {
		buttonList.add(new GuiButton(0, width/2 - 100, height-25, "Back"));
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if (button.id == 0) mc.displayGuiScreen(lastScreen);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		String[] strings = new String[] {"I see you've installed MoreCommands.", "Very nice.", "First off, the basics.", "As you may or may not know, this mod has client sided commands.",
				"Which are commands you can use on any server.", "To get a list of them, type /chelp.", "The one you'd probably use the most will be /ptime.", "At least, it's my personal favorite.",
				"Next, you have server sided commands.", "Which only work when the mod is also on the server.", "If you have access to it, the one you'd probably use the most would be /explode.",
				"Cuz who doesn't like to blow things up?", "I sure do, that's why I made it.", "For a list of commands click", "", "You better click that link, ain't easy to add it y'know.",
				"Btw, try /easteregg ;)", ""};
		int row = 0;
		for (String string : strings)
			drawCenteredString(fontRenderer, string, width/2, 2 + row++ * 10, Integer.parseInt("FFAA00", 16));
		drawCenteredUrl(0, "https://minecraft.curseforge.com/projects/morecommands", "here.", width/2-15, row++ * 8 - 3, width/2+10, row * 8); // precise calculations
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		for (Integer id : urls1.keySet()) {
			int x = urls.get(id).get("x");
			int y = urls.get(id).get("y");
			int x1 = urls.get(id).get("x1");
			int y1 = urls.get(id).get("y1");
			if (mouseX >= x && mouseX <= x1 && mouseY >= y && mouseY <= y1)
				try {
					Desktop.getDesktop().browse(new URI(urls1.get(id)));
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
		}

	}

	protected <T extends GuiLabel> T addLabel(T label) {
		labelList.add(label);
		return label;
	}

	protected void drawUrl(int id, String url, String text, int x, int y, int x1, int y1, boolean centered) {
		if (centered) drawCenteredString(fontRenderer, "" + TextFormatting.BLUE + TextFormatting.UNDERLINE + text, x + 15, y, Integer.parseInt("FFFFFF", 16));
		else drawString(fontRenderer, "" + TextFormatting.BLUE + TextFormatting.UNDERLINE + text, x + 15, y, Integer.parseInt("FFFFFF", 16));
		Map<String, Integer> map = new HashMap<>();
		map.put("x", x);
		map.put("y", y);
		map.put("x1", x1);
		map.put("y1", y1);
		urls.put(id, map);
		urls1.put(id, url);
	}

	protected void drawCenteredUrl(int id, String url, String text, int x, int y, int x1, int y1) {
		drawUrl(id, url, text, x, y, x1, y1, true);
	}

	protected void drawUrl(int id, String url, String text, int x, int y, int x1, int y1) {
		drawUrl(id, url, text, x, y, x1, y1, false);
	}
}
