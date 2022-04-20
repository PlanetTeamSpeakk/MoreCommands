package com.ptsmods.morecommands.clientoption;

import com.google.common.collect.ImmutableList;

import java.util.List;

public enum ClientOptionCategory {
	RICH_PRESENCE("Rich Presence", "Discord Rich Presence support.", "To let everyone on Discord know", "that you've been playing Minecraft for three days straight."),
	RENDERING("Rendering", "These options change or add things regarding rendering."),
	TWEAKS("Tweaks", "Some tweaks to change your game.", "These are harmless, but can be very useful."),
	CHEATS("Cheats", ClientOptions.Tweaks.hiddenOptions, "Some less harmless tweaks.", "All of them are set to mimic the default behaviour of Minecraft,", "meaning that their default values don't change anything.", "",
			"\u00A7cTo prevent you getting an unfair advantage,", "\u00A7cthese options only affect singleplayer worlds."),
	CHAT("Chat", "Chat related tweaks.", "Most of these are enabled by default."),
	EASTER_EGGS("Easter Eggs", ClientOptions.Tweaks.hiddenOptions, "Don't look in here.", "Stay away.", "", "Keep \u00A7c\u00A7lOUT\u00A7r!! >:c");

	private final String name;
	private final BooleanClientOption hidden;
	private final List<String> comments;

	ClientOptionCategory(String name, String... comments) {
		this(name, null, comments);
	}

	ClientOptionCategory(String name, BooleanClientOption hidden, String... comments) {
		this.name = name;
		this.hidden = hidden;
		this.comments = comments == null ? null : ImmutableList.copyOf(comments);
	}

	public String getName() {
		return name;
	}

	public BooleanClientOption getHidden() {
		return hidden;
	}

	public List<String> getComments() {
		return comments;
	}
}
