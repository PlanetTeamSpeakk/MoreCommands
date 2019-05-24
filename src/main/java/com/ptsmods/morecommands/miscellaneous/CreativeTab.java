package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CreativeTab extends CreativeTabs {

	private final Item		iconItem;
	private final String	label;

	public CreativeTab(String label, Item iconItem) {
		super(label);
		this.iconItem = iconItem;
		this.label = label;
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(iconItem);
	}

	@Override
	public String getTranslationKey() {
		return "itemGroup." + Reference.joinCustomChar("", Reference.capitalizeFirstChars(label.split(" "))); // this should convert "Unobtainable items" to "itemGroup.UnobtainableItems"
	}

}
