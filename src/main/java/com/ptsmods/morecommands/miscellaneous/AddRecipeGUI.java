package com.ptsmods.morecommands.miscellaneous;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.ptsmods.morecommands.miscellaneous.Reference.LogType;
import com.ptsmods.morecommands.net.ServerRecipePacket.Type;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AddRecipeGUI extends GuiContainer {

	private Type	type;
	private String	playerInvName;

	public AddRecipeGUI(Type type, InventoryPlayer playerInventory) {
		super(type == Type.WORKBENCH ? new AddRecipeBenchContainer(playerInventory) : type == Type.FURNACE ? new AddRecipeFurnaceContainer(playerInventory) : new AddRecipeBrewingContainer(playerInventory));
		this.type = type;
		playerInvName = playerInventory.getDisplayName().getUnformattedText();
	}

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
			drawTexturedModalRect(k + 56, l + 36, 176, 0, 14, 14);
			drawTexturedModalRect(k + 78, l + 35, 176, 14, 24, 17);
		} else if (type == Type.BREWING) {
			drawTexturedModalRect(k + 98, l + 17, 176, 0, 9, 28);
			drawTexturedModalRect(k + 60, l + 44, 176, 29, 18, 4);
			drawTexturedModalRect(k + 62, l + 14, 185, 0, 12, 29);
		}
	}

	@Override
	public void handleInput() throws IOException {
		if (Mouse.isCreated()) while (Mouse.next()) {
			Reference.print(LogType.INFO, "Mouse event ");
			mouseHandled = false;
			if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent.Pre(this))) continue;
			handleMouseInput();
			if (equals(mc.currentScreen) && !mouseHandled) net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent.Post(this));
		}

		if (Keyboard.isCreated()) while (Keyboard.next()) {
			keyHandled = false;
			if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent.Pre(this))) continue;
			handleKeyboardInput();
			if (equals(mc.currentScreen) && !keyHandled) net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent.Post(this));
		}
	}

}
