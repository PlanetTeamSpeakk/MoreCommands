package com.ptsmods.morecommands.api;

public class Holder {
	private static IMoreCommands moreCommands;

	public static IMoreCommands getMoreCommands() {
		return moreCommands;
	}

	public static void setMoreCommands(IMoreCommands moreCommands) {
		if (Holder.moreCommands != null) throw new IllegalStateException("MoreCommands instance already set.");
		Holder.moreCommands = moreCommands;
	}
}
