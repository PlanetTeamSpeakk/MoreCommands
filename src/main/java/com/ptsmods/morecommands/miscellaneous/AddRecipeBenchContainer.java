package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class AddRecipeBenchContainer extends Container {

	public InventoryCrafting	inputInventory	= new InventoryCrafting(this, 3, 3);
	public RecipeInventory		outputInventory;

	public AddRecipeBenchContainer(InventoryPlayer playerInventory) {
		for (int i = 0; i < 9; i++)
			inputInventory.setInventorySlotContents(i, ItemStack.EMPTY);
		outputInventory = new RecipeInventory();
		outputInventory.setInventorySlotContents(0, ItemStack.EMPTY);
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
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		// if (slotId >= 0 && slotId < 10) {
		// inventorySlots.get(slotId).putStack(ItemStack.EMPTY);
		// return ItemStack.EMPTY;
		// } else return super.slotClick(slotId, dragType, clickTypeIn, player);
		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		// ItemStack stack = inventorySlots.get(slotIndex).getStack().copy();
		// stack.setCount(1);
		// return stack;
		return super.transferStackInSlot(player, slotIndex);
	}

	@Override
	public boolean canMergeSlot(ItemStack stack, Slot slot) {
		return true; // Only 1 item in each slot.
	}

	@Override
	public Slot getSlot(int parSlotIndex) {
		// if (parSlotIndex >= inventorySlots.size()) parSlotIndex =
		// inventorySlots.size() - 1;
		return super.getSlot(parSlotIndex);
	}

}
