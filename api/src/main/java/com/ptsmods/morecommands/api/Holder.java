package com.ptsmods.morecommands.api;

import com.ptsmods.morecommands.api.compat.Compat;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;

public class Holder {
	private static IMoreCommands moreCommands;
	private static Compat compat;
	// Trying not to load the Command class, which in turn loads the MoreCommands class, when registering this attribute
	// in MixinPlayerEntity which is done too early.
	public static final EntityAttribute REACH_ATTRIBUTE = new ClampedEntityAttribute("attribute.morecommands.reach", 4.5d, 1d, 160d).setTracked(true);

	public static IMoreCommands getMoreCommands() {
		return moreCommands;
	}

	public static void setMoreCommands(IMoreCommands moreCommands) {
		if (Holder.moreCommands != null) throw new IllegalStateException("MoreCommands instance already set.");
		Holder.moreCommands = moreCommands;
	}

	public static Compat getCompat() {
		return compat;
	}

	public static Compat setCompat(Compat compat) {
		if (Holder.compat != null) throw new IllegalStateException("Compat instance already set.");
		return Holder.compat = compat;
	}
}
