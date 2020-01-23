package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class AddRecipeBrewingContainer extends Container {

	private RecipeInventory		reagentInventory	= new RecipeInventory();
	private RecipeInventory		fuelInventory		= new RecipeInventory();
	private RecipeInventory[]	inputInventories	= new RecipeInventory[] {new RecipeInventory(), new RecipeInventory(), new RecipeInventory()};
	private RecipeInventory		outputInventory		= new RecipeInventory();

	public AddRecipeBrewingContainer(InventoryPlayer playerInventory) {
		reagentInventory.setInventorySlotContents(0, ItemStack.EMPTY);
		fuelInventory.setInventorySlotContents(0, new ItemStack(Items.BLAZE_POWDER, 1));
		inputInventories[0].setInventorySlotContents(0, ItemStack.EMPTY);
		inputInventories[1].setInventorySlotContents(0, ItemStack.EMPTY);
		inputInventories[2].setInventorySlotContents(0, ItemStack.EMPTY);
		outputInventory.setInventorySlotContents(0, ItemStack.EMPTY);
		addSlotToContainer(new Slot(reagentInventory, 0, 79, 17));
		addSlotToContainer(new Slot(fuelInventory, 0, 17, 17));
		addSlotToContainer(new Slot(inputInventories[0], 0, 56, 51));
		addSlotToContainer(new Slot(inputInventories[1], 0, 79, 58));
		addSlotToContainer(new Slot(inputInventories[2], 0, 102, 51));
		addSlotToContainer(new Slot(outputInventory, 0, 141, 17));
		for (int playerSlotIndexY = 0; playerSlotIndexY < 3; ++playerSlotIndexY)
			for (int playerSlotIndexX = 0; playerSlotIndexX < 9; ++playerSlotIndexX)
				addSlotToContainer(new Slot(playerInventory, playerSlotIndexX + playerSlotIndexY * 9 + 9, 8 + playerSlotIndexX * 18, 84 + playerSlotIndexY * 18));
		for (int hotbarSlotIndex = 0; hotbarSlotIndex < 9; ++hotbarSlotIndex)
			addSlotToContainer(new Slot(playerInventory, hotbarSlotIndex, 8 + hotbarSlotIndex * 18, 142));
	}

	@Override
	public void onCraftMatrixChanged(IInventory parInventory) {}

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		return ItemStack.EMPTY; // Don't edit the RecipeBrewingStand.
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {

	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return false; // d o n o t
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		// don't
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canMergeSlot(ItemStack stack, Slot slot) {
		return false; // n o
	}

	@Override
	public Slot getSlot(int slotIndex) {
		if (slotIndex >= inventorySlots.size()) slotIndex = inventorySlots.size() - 1;
		return super.getSlot(slotIndex);
	}

}
