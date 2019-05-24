package com.ptsmods.morecommands.miscellaneous;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class RecipeFurnaceContainer extends Container {

	public RecipeInventory	inputInventory	= new RecipeInventory();
	public RecipeInventory	fuelInventory	= new RecipeInventory();
	public RecipeInventory	outputInventory	= new RecipeInventory();

	public RecipeFurnaceContainer(List<ItemStack> stacks, InventoryPlayer playerInventory) {
		inputInventory.setInventorySlotContents(0, new ItemStack(stacks.get(0).getItem(), stacks.get(0).getCount(), stacks.get(0).getMetadata() == 32767 ? 0 : stacks.get(0).getMetadata()));
		fuelInventory.setInventorySlotContents(0, new ItemStack(Items.COAL, 1));
		outputInventory.setInventorySlotContents(0, new ItemStack(stacks.get(1).getItem(), stacks.get(1).getCount(), stacks.get(1).getMetadata() == 32767 ? 0 : stacks.get(1).getMetadata()));
		addSlotToContainer(new Slot(inputInventory, 0, 56, 17));
		addSlotToContainer(new Slot(fuelInventory, 0, 56, 53));
		addSlotToContainer(new Slot(outputInventory, 0, 116, 35));
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
		return ItemStack.EMPTY; // Don't edit the RecipeFurnace.
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
