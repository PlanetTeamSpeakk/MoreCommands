package com.ptsmods.morecommands.miscellaneous;

import java.io.IOException;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.ptsmods.morecommands.miscellaneous.Ticker.TickRunnable;
import com.ptsmods.morecommands.net.ServerRecipePacket.Type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RecipeGUI extends GuiContainer {

	private Type	type;
	private String	playerInvName;
	private int		tick	= 0;

	public RecipeGUI(Type type, List<ItemStack> stacks, InventoryPlayer playerInventory) {
		super(type == Type.WORKBENCH ? new RecipeBenchContainer(stacks, playerInventory) : type == Type.FURNACE ? new RecipeFurnaceContainer(stacks, playerInventory) : new RecipeBrewingContainer(stacks, playerInventory));
		this.type = type;
		playerInvName = playerInventory.getDisplayName().getUnformattedText();
		TickRunnable[] run = new TickRunnable[1];
		run[0] = extraArgs -> {
			tick += 1;
			tick %= 40;
			if (Minecraft.getMinecraft().currentScreen == this) Ticker.INSTANCE.addRunnable(TickEvent.Type.CLIENT, run[0]);
		};
		Ticker.INSTANCE.addRunnable(TickEvent.Type.CLIENT, run[0]);
	}

	@Override
	public void actionPerformed(GuiButton button) {}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		Mouse.setGrabbed(false); // Idk why it doesn't do this on its own.
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String s = type == Type.WORKBENCH ? "Crafting" : type == Type.FURNACE ? "Furnace" : "Brewing Stand";
		fontRenderer.drawString(s, type == Type.WORKBENCH ? 28 : xSize / 2 - fontRenderer.getStringWidth(s) / 2, 5, 4210752);
		fontRenderer.drawString(playerInvName, 8, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		if (mc == null || mc.renderEngine == null) return;
		mc.renderEngine.bindTexture(new ResourceLocation((type == Type.BREWING ? "morecommands" : "minecraft") + ":textures/gui/container/" + (type == Type.WORKBENCH ? "crafting_table" : type == Type.FURNACE ? "furnace" : "recipe_brewing_stand") + ".png"));
		int k = width / 2 - xSize / 2;
		int l = height / 2 - ySize / 2;
		drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
		if (type == Type.FURNACE) {
			drawTexturedModalRect(k + 56, l + 36 + 14 - (int) (tick / 40F * 14), 176, 14 - (int) (tick / 40F * 14), 14, (int) (tick / 40F * 14));
			drawTexturedModalRect(k + 78, l + 35, 176, 14, (int) (tick / 40F * 24), 17);
		} else if (type == Type.BREWING) {
			drawTexturedModalRect(k + 97, l + 17, 176, 0, 9, (int) (tick / 40F * 28));
			drawTexturedModalRect(k + 60, l + 44, 176, 29, 18, 4);
			drawTexturedModalRect(k + 62, l + 14 + 29 - (int) (tick % 20 / 20F * 29), 185, 29 - (int) (tick % 20 / 20F * 29), 12, (int) (tick % 20 / 20F * 29));
		}
	}

	@Override
	public void handleInput() throws IOException {
		// Do handle keyboard events, but do not handle mouse events because of dupes.
		// Even though the dupes would be client side only, it would still work on
		// singleplayer.
		if (Keyboard.isCreated()) while (Keyboard.next()) {
			keyHandled = false;
			char c0 = Keyboard.getEventCharacter();
			if (Keyboard.getEventKey() == 0 && c0 >= ' ' || Keyboard.getEventKeyState()) keyTyped(c0, Keyboard.getEventKey());
			mc.dispatchKeypresses();
		}
	}

}
