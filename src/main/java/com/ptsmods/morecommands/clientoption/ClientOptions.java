package com.ptsmods.morecommands.clientoption;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.miscellaneous.FormattingColour;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import static com.ptsmods.morecommands.clientoption.ClientOptionCategory.*;

public class ClientOptions {
	public static class RichPresence {
		public static final BooleanClientOption enableRPC = new BooleanClientOption(RICH_PRESENCE, "Enable RPC", true, RichPresence::updateRPC,
				"Toggle Discord Rich Presence", "May not work on Mac.");
		public static final BooleanClientOption advertiseMC = new BooleanClientOption(RICH_PRESENCE, "Advertise MC", true, RichPresence::updateRPC,
				"Whether I may advertise this mod on the RPC.", "It would be kindly appreciated if you left this enabled. :)");
		public static final BooleanClientOption showDetails = new BooleanClientOption(RICH_PRESENCE, "Show Details", true, RichPresence::updateRPC,
				"Whether to show either the ip of the server you're on", "or the name of the world you're in in the details section of the RPC.");
		public static final BooleanClientOption shareTag = new BooleanClientOption(RICH_PRESENCE, "Share Tag", true, RichPresence::updateTag,
				"Whether your Discord tag should be sent to the server", "for others to see.", "Only works if the mod is installed on the server too.");
		public static final BooleanClientOption askPermission = new BooleanClientOption(RICH_PRESENCE, "Ask Permission", true, RichPresence::updateTag,
				"Whether players need your permission to view your tag.");

		private static void updateRPC(boolean oldValue, boolean newValue) {
			MoreCommandsClient.updatePresence();
		}

		private static void updateTag(boolean oldValue, boolean newValue) {
			MoreCommandsClient.updateTag();
		}

		static void init() {}
	}

	public static class Rendering {
		public static final BooleanClientOption seeTagSneaking = new BooleanClientOption(RENDERING, "See Tag Sneaking", true,
				"See player nametags through blocks", "when they're sneaking.");
		public static final BooleanClientOption renderOwnTag = new BooleanClientOption(RENDERING, "Render Own Tag", true,
				"Render your own tag in third person mode", "and the inventory screen.");
		public static final BooleanClientOption showExactLatency = new BooleanClientOption(RENDERING, "Show Exact Latency", true,
				"Show exact latency (ping) of players when holding tab.", "Kind of buggy, so beware.");
		public static final BooleanClientOption powertoolsGlint = new BooleanClientOption(RENDERING, "Powertools Glint", true,
				"Adds glint (enchanted) effect to MoreCommands powertools.");
		public static final BooleanClientOption renderFog = new BooleanClientOption(RENDERING, "Render Fog", true,
				"Whether fog should be rendered.", "Goes for all types of fog so includes blindness.", "\u00A7cIncompatible with OptiFabric (sorry :/)");
		public static final BooleanClientOption bigTabPager = new BooleanClientOption(RENDERING, "Big Tag Pager", false,
				"Whether you want the pager in the creative menu", "to be larger than normal.", "This does offset the buttons from their usual position.");
		public static final BooleanClientOption fixItemSeams = new BooleanClientOption(RENDERING, "Fix Item Seams", true,
				"Removes transparent lines in item models", "generated from 2D textures.", "\u00A7cRequires restart");
		public static final BooleanClientOption fixAnimItemSeams = new BooleanClientOption(RENDERING, "Fix Anim Item Seams", false,
				"Removes transparent lines in animated item models", "generated from 2D textures.", "\u00A7cDisabled by default for performance reasons", "\u00A7cRequires restart");

		static void init() {}
	}

	public static class Tweaks {
		public static final EnumClientOption<FormattingColour> defColour = new EnumClientOption<>(TWEAKS, "Def Colour", FormattingColour.class, FormattingColour.GOLD,
				"The default colour to stylise text with.", "Can be set with the defaultFormatting gamerule on servers.", "\u00A7cRequires restart for full effect");
		public static final EnumClientOption<FormattingColour> secColour = new EnumClientOption<>(TWEAKS, "Sec Colour", FormattingColour.class, FormattingColour.YELLOW,
				"The secondary colour to stylise text with.", "Can be set with the secondaryFormatting gamerule on servers.", "\u00A7cRequires restart for full effect");
		public static final BooleanClientOption enableInfoHUD = new BooleanClientOption(TWEAKS, "Enable Info HUD", false,
				"Draw general information in the top-left corner.", "Can be toggled by pressing the O key on your keyboard.");
		public static final BooleanClientOption openDoubleDoors = new BooleanClientOption(TWEAKS, "Open Double Doors", true,
				"When using (right-clicking) a door that has", "a corresponding door next to it, use both of them.", "Works on servers without the mod too.");
		public static final BooleanClientOption sitOnStairs = new BooleanClientOption(TWEAKS, "Sit On Stairs", true,
				"Right-click on inner-corner or straight stairs to sit on them.", "Only works if the mod is also installed on the server.");
		public static final BooleanClientOption disableElytra = new BooleanClientOption(TWEAKS, "Disable Elytra", false,
				"Prevent your elytra from opening when you press space.", "For if you want to wear it, but not use it.", "(In e.g. pvp areas of survival servers or during parkour)");
		public static final BooleanClientOption noSignLimit = new BooleanClientOption(TWEAKS, "No Sign Limit", true,
				"Allows you to make the lines on signs longer.", "Lines are still cut when rendering (so no giant lines),", "but it's very useful for putting colours on signs.");
		public static final BooleanClientOption lockFlying = new BooleanClientOption(TWEAKS, "Lock Flying", false,
				"Whether flying should be locked.", "When true, you will always be flying.", "It is recommended to bind this option to a key", "if you find yourself often needing it.");
		public static final BooleanClientOption targetFluids = new BooleanClientOption(TWEAKS, "Target Fluids", false,
				"Whether you can target fluids with your cursor.", "Useful for placing blocks on water.");
		public static final BooleanClientOption hiddenOptions = new BooleanClientOption(TWEAKS, "Hidden Options", false,
				"Whether hidden options should be shown.", "You're highly discouraged to enable this.");

		public static final BooleanClientOption doBlockPush = new BooleanClientOption(TWEAKS, "Do Block Push", true,
				"Whether blocks should push you out when you're inside them.");
		public static final BooleanClientOption immediateMoveStop = new BooleanClientOption(TWEAKS, "Immediate Move Stop", false,
				"Whether you should immediately come to a halt", "when you release all movement keys while moving.",
				"\u00A7Not recommended during PvP");
		public static final BooleanClientOption rainbowSplash = new BooleanClientOption(TWEAKS, "Rainbow Splash", false,
				"Whether splash texts should be rainbow coloured.", "\u00A7cRequires restart");
		public static final BooleanClientOption alwaysMinceraft = new BooleanClientOption(TWEAKS, "Always Minceraft", false,
				"Whether to make the title screen always say", "'Minceraft' instead of 'Minecraft'.", "As you may or may not know, by default it has a 0.1% chance.", "\u00A7cRequires restart");
		public static final BooleanClientOption screensInPortal = new BooleanClientOption(TWEAKS, "Screens In Portal", true,
				"Whether you want to be able to open screens like", "chat or inventory while teleporting in a nether portal.", "\u00A7cIncompatible with Tweakeroo");
		public static final BooleanClientOption textColourPicker = new BooleanClientOption(TWEAKS, "Text Colour Picker", true,
				"Whether the 'Colours' button should be displayed", "on most GUIs that support colours in text input.", "(Signs, books, anvils and chat for now)");
		public static final BooleanClientOption colourPickerOpen = new BooleanClientOption(TWEAKS, "Colour Picker Open", false,
				"Whether the 'Colours' button should always be expanded", "when first opening a GUI that has it.");
		public static final BooleanClientOption creativeKeyPager = new BooleanClientOption(TWEAKS, "Creative Key Pager", true,
				"When enabled, you can use the arrow keys or A and D", "to switch pages in the creative menu.");
		public static final BooleanClientOption joinNotifyNameMC = new BooleanClientOption(TWEAKS, "Join Notify NameMC", true,
				"Whether you want to automatically opt your friends", "on NameMC in for join notifications.", "(Get a message when they join/leave)");
		public static final DoubleClientOption brightnessMultiplier = new DoubleClientOption(TWEAKS, "Brightness Multiplier", 1d, 0d, 10d,
				"Multiply the brightness to go above and beyond!", "For best effect, set brightness to max.");

		static void init() {}
	}

	public static class Cheats {
		public static final BooleanClientOption sprintAutoJump = new BooleanClientOption(CHEATS, "Sprint Auto Jump", false,
				"Lets you jump as soon as you hit the ground while sprinting.", "Gives you a slight speed boost when holding space too.");
		public static final BooleanClientOption avoidCactusDmg = new BooleanClientOption(CHEATS, "Avoid Cactus Dmg", false,
				"Prevents you from taking damage from cacti by making their", "collision box a little bigger than usual.");
		public static final BooleanClientOption collideAll = new BooleanClientOption(CHEATS, "Collide All", false,
				"Allows you to collide with (almost) every block in the game.", "Allows you to i.e. walk on cobweb, bushes, grass, etc...", "But not fluids, that's just too much.");

		static void init() {}
	}

	public static class Chat {
		public static final BooleanClientOption chatMsgCopy = new BooleanClientOption(CHAT, "Chat Msg Copy", true,
				"Copy a chatmessage when you left-click on it.", "Holding control while doing so will also copy formattings.");
		public static final BooleanClientOption chatMsgRemove = new BooleanClientOption(CHAT, "Chat Msg Remove", true,
				"Remove a chatmessage when you right-click on it.");
		public static final BooleanClientOption ignoreEmptyMsgs = new BooleanClientOption(CHAT, "Ignore Empty Msgs", true,
				"Do not show messages that have no content in chat.");
		public static final BooleanClientOption infiniteChat = new BooleanClientOption(CHAT, "Infinite Chat", true,
				"Do not delete chatmessages when there are more than 100.");
		public static final BooleanClientOption showMsgTime = new BooleanClientOption(CHAT, "Show Msg Time", false,
				"Prepend messages in chat with the time they were received.");
		public static final BooleanClientOption use12HourClock = new BooleanClientOption(CHAT, "Use 12 Hour Clock", false,
				"Use a 12 hour clock instead of a 24 hour one.", "See 'Show Msg Time'.");
		public static final BooleanClientOption showSeconds = new BooleanClientOption(CHAT, "Show Seconds", false,
				"Whether to also add seconds or not.", "See 'Show Msg Time'.");

		static void init() {}
	}

	public static class EasterEggs {
		public static final BooleanClientOption rainbows = new BooleanClientOption(EASTER_EGGS, "Rainbows", false,
				"Everything is \u00A7urainbows\u00A7r.", "\u00A7lEverything", "\u00A74Not suitable for people prone to epileptic seizures!");

		static void init() {}
	}

	private static final Properties props = new Properties();
	private static final File f = new File("config/MoreCommands/clientoptions.txt");

	public static boolean write() {
		ClientOption.getUnmappedOptions().values().forEach(option -> props.setProperty(option.getKey(), option.getValueString()));
		if (!f.exists()) {
			try {
				if (!f.createNewFile()) return false;
			} catch (IOException e) {
				MoreCommands.LOG.catching(e);
				return false;
			}
		}

		try (PrintWriter writer = new PrintWriter(f, "UTF-8")) {
			props.store(writer, "MoreCommands' client-only options.\nThese should not be changed manually, but rather be set via the in-game menu. (esc -> options -> MoreCommands)");
			writer.flush();
			return true;
		} catch (Exception e) {
			MoreCommands.LOG.error("An error occurred while saving the clientoptions.", e);
			return false;
		}
	}

	public static void read() {
		if (f.exists())
			try (FileReader reader = new FileReader(f)) {
				props.load(reader);
				ClientOption.getUnmappedOptions().values().stream()
						.filter(option -> props.containsKey(option.getKey()))
						.forEach(option -> option.setValueString(props.getProperty(option.getKey())));
				write();
			} catch (IOException e) {
				MoreCommands.LOG.catching(e);
			}
		else write();
	}

	public static void init() {
		// Initialising all classes, so all options are registered.
		RichPresence.init();
		Rendering.init();
		Tweaks.init();
		Cheats.init();
		Chat.init();
		EasterEggs.init();

		read();
	}

	public static void reset() {
		ClientOption.resetAll();
		write();
	}
}
