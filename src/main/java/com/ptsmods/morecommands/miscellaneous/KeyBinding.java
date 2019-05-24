package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.client.resources.I18n;

public abstract class KeyBinding extends net.minecraft.client.settings.KeyBinding {

	private String unlocalizedName;

	public KeyBinding(String unlocalizedName, int keyCode) {
		super(I18n.format("key.morecommands." + unlocalizedName), keyCode, I18n.format("menu.moreCommands"));
		this.unlocalizedName = unlocalizedName;
	}

	public final String getLocalizedName() {
		return I18n.format(getUnlocalizedName());
	}

	public final String getRawUnlocalizedName() {
		return unlocalizedName;
	}

	public final String getUnlocalizedName() {
		return "key.morecommands." + unlocalizedName;
	}

	public abstract void run();

}
