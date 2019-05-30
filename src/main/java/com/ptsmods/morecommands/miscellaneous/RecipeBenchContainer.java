package com.ptsmods.morecommands.miscellaneous;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class RecipeBenchContainer extends Container {

	public InventoryCrafting	inputInventory	= new InventoryCrafting(this, 3, 3);
	public RecipeInventory		outputInventory;

	public RecipeBenchContainer(List<ItemStack> stacks, InventoryPlayer playerInventory) {
		for (int i = 0; i < 9; i++)
			inputInventory.setInventorySlotContents(i, stacks.get(i));
		outputInventory = new RecipeInventory();
		outputInventory.setInventorySlotContents(0, stacks.get(9));
		for (int inputSlotIndexX = 0; inputSlotIndexX < 3; ++inputSlotIndexX)
			for (int inputSlotIndexY = 0; inputSlotIndexY < 3; ++inputSlotIndexY)
				addSlotToContainer(new Slot(inputInventory, inputSlotIndexY + inputSlotIndexX * 3, 30 + inputSlotIndexY * 18, 17 + inputSlotIndexX * 18));
		addSlotToContainer(new Slot(outputInventory, 0, 124, 35));
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
		return ItemStack.EMPTY; // Don't edit the RecipeBench.
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
	public Slot getSlot(int parSlotIndex) {
		if (parSlotIndex >= inventorySlots.size()) parSlotIndex = inventorySlots.size() - 1;
		return super.getSlot(parSlotIndex);
	}

}
