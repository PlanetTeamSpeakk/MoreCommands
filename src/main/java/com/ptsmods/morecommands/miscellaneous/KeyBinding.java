package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

public class KeyBinding extends net.minecraft.client.settings.KeyBinding {

	public KeyBinding(String unlocalizedName, int keyCode) {
		this(I18n.format("keyBinding." + unlocalizedName), keyCode, I18n.format("menu.moreCommands"));
	}

	public KeyBinding(String description, int keyCode, String category) {
		super(description, KeyConflictContext.IN_GAME, KeyModifier.NONE, keyCode, category);
	}

}
