package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class RecipeInventory implements IInventory {
	private final ItemStack[] stackResult = new ItemStack[1];

	public RecipeInventory() {}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return stackResult[index];
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if (stackResult[index] != null) {
			stackResult[index].shrink(count);
			return stackResult[index];
		} else return null;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		stackResult[index] = stack;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack) {
		return true;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack element : stackResult)
			if (element != null) return false;
		return true;
	}

	@Override
	public void markDirty() {

	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public void openInventory(EntityPlayer playerIn) {

	}

	@Override
	public void closeInventory(EntityPlayer playerIn) {

	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		for (int i = 0; i < stackResult.length; i++)
			stackResult[i] = null;
	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(getName());
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack stack = getStackInSlot(index);
		setInventorySlotContents(index, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

}
