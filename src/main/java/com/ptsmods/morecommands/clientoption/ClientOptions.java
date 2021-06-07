package com.ptsmods.morecommands.clientoption;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.miscellaneous.FormattingColour;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.*;

public class ClientOptions {
	@Comment({"Discord Rich Presence support.", "To let everyone on Discord know", "that you've been playing Minecraft for three days straight."})
	public static class RichPresence {
		public static final int ordinal = 0;
		public static final BooleanClientOption enableRPC = new BooleanClientOption(true, RichPresence::updateRPC, "Toggle Discord Rich Presence", "May not work on Mac.");
		public static final BooleanClientOption advertiseMC = new BooleanClientOption(true, RichPresence::updateRPC, "Whether I may advertise this mod on the RPC.", "It would be kindly appreciated if you left this enabled. :)");
		public static final BooleanClientOption showDetails = new BooleanClientOption(true, RichPresence::updateRPC, "Whether to show either the ip of the server you're on", "or the name of the world you're in in the details section of the RPC.");
		public static final BooleanClientOption shareTag = new BooleanClientOption(true, RichPresence::updateTag, "Whether your Discord tag should be sent to the server", "for others to see.", "Only works if the mod is installed on the server too.");
		public static final BooleanClientOption askPermission = new BooleanClientOption(true, RichPresence::updateTag, "Whether players need your permission to view your tag.");

		private static void updateRPC(boolean oldValue, boolean newValue) {
			MoreCommandsClient.updatePresence();
		}

		private static void updateTag(boolean oldValue, boolean newValue) {
			MoreCommandsClient.updateTag();
		}
	}

	@Comment("These options change or add things regarding rendering.")
	public static class Rendering {
		public static final int ordinal = 1;
		public static final BooleanClientOption seeTagSneaking = new BooleanClientOption(true, "See player nametags through blocks", "when they're sneaking.");
		public static final BooleanClientOption renderOwnTag = new BooleanClientOption(true, "Render your own tag in third person mode", "and the inventory screen.");
		public static final BooleanClientOption showExactLatency = new BooleanClientOption(true, "Show exact latency (ping) of players when holding tab.", "Kind of buggy, so beware.");
		public static final BooleanClientOption powertoolsGlint = new BooleanClientOption(true, "Adds glint (enchanted) effect to MoreCommands powertools.");
		public static final BooleanClientOption renderFog = new BooleanClientOption(true, "Whether fog should be rendered.", "Goes for all types of fog so includes blindness.", "\u00A7cIncompatible with OptiFabric (sorry :/)");
		public static final BooleanClientOption bigTabPager = new BooleanClientOption(false, "Whether you want the pager in the creative menu", "to be larger than normal.", "This does offset the buttons from their usual position.");
		public static final BooleanClientOption fixItemSeams = new BooleanClientOption(true, "Removes transparent lines in item models", "generated from 2D textures.", "\u00A7cRequires restart");
		public static final BooleanClientOption fixAnimItemSeams = new BooleanClientOption(false, "Removes transparent lines in animated item models", "generated from 2D textures.", "\u00A7cDisabled by default for performance reasons", "\u00A7cRequires restart");
	}

	@Comment({"Some tweaks to change your game.", "These are harmless, but can be very useful."})
	public static class Tweaks {
		public static final int ordinal = 2;
		public static final EnumClientOption<FormattingColour> defColour = new EnumClientOption<>(FormattingColour.class, FormattingColour.GOLD, "The default colour to stylise text with.", "Can be set with the defaultFormatting gamerule on servers.", "\u00A7cRequires restart for full effect");
		public static final EnumClientOption<FormattingColour> secColour = new EnumClientOption<>(FormattingColour.class, FormattingColour.YELLOW, "The secondary colour to stylise text with.", "Can be set with the secondaryFormatting gamerule on servers.", "\u00A7cRequires restart for full effect");
		public static final BooleanClientOption enableInfoHud = new BooleanClientOption(false, "Draw general information in the top-left corner.", "Can be toggled by pressing the O key on your keyboard.");
		public static final BooleanClientOption openDoubleDoors = new BooleanClientOption(true, "When using (right-clicking) a door that has", "a corresponding door next to it, use both of them.", "Works on servers without the mod too.");
		public static final BooleanClientOption sitOnStairs = new BooleanClientOption(true, "Right-click on inner-corner or straight stairs to sit on them.", "Only works if the mod is also installed on the server.");
		public static final BooleanClientOption disableElytra = new BooleanClientOption(false, "Prevent your elytra from opening when you press space.", "For if you want to wear it, but not use it.", "(In e.g. pvp areas of survival servers or during parkour)");
		public static final BooleanClientOption noSignLimit = new BooleanClientOption(true, "Allows you to make the lines on signs longer.", "Lines are still cut when rendering (so no giant lines),", "but it's very useful for putting colours on signs.");
		public static final BooleanClientOption lockFlying = new BooleanClientOption(false, "Whether flying should be locked.", "When true, you will always be flying.", "It is recommended to bind this option to a key", "if you find yourself often needing it.");
		public static final BooleanClientOption targetFluids = new BooleanClientOption(false, "Whether you can target fluids with your cursor.", "Useful for placing blocks on water.");
		@SuppressWarnings("unused") // It is used, just not directly.
		public static final BooleanClientOption hiddenOptions = new BooleanClientOption(false, "Whether hidden options should be shown.", "You're highly discouraged to enable this.");

		public static final BooleanClientOption doBlockPush = new BooleanClientOption(true, "Whether blocks should push you out when you're inside them.");
		public static final BooleanClientOption immediateMoveStop = new BooleanClientOption(false, "Whether you should immediately come to a halt", "when you release all movement keys while moving.");
		public static final BooleanClientOption rainbowSplash = new BooleanClientOption(false, "Whether splash texts should be rainbow coloured.", "\u00A7cRequires restart");
		public static final BooleanClientOption alwaysMinceraft = new BooleanClientOption(false, "Whether to make the title screen always say", "'Minceraft' instead of 'Minecraft'.", "As you may or may not know, by default it has a 0.1% chance.", "\u00A7cRequires restart");
		public static final BooleanClientOption screensInPortal = new BooleanClientOption(true, "Whether you want to be able to open screens like", "chat or inventory while teleporting in a nether portal.", "\u00A7cIncompatible with Tweakeroo");
		public static final BooleanClientOption textColourPicker = new BooleanClientOption(true, "Whether the 'Colours' button should be displayed", "on most GUIs that support colours in text input.", "(Signs, books, anvils and chat for now)");
		public static final BooleanClientOption colourPickerOpen = new BooleanClientOption(false, "Whether the 'Colours' button should always be expanded", "when first opening a GUI that has it.");
		public static final BooleanClientOption creativeKeyPager = new BooleanClientOption(true, "When enabled, you can use the arrow keys or A and D", "to switch pages in the creative menu.");
		public static final BooleanClientOption joinNotifyNameMC = new BooleanClientOption(true, "Whether you want to automatically opt your friends", "on NameMC in for join notifications.", "(Get a message when they join/leave)");
	}

	@Comment({"Some less harmless tweaks.", "All of them are set to mimic the default behaviour of Minecraft,", "meaning that their default values don't change anything.", "", "\u00A7cTo prevent you getting an unfair advantage,", "\u00A7cthese options only affect singleplayer worlds."})
	@IsHidden("hiddenOptions")
	public static class Cheats {
		public static final int ordinal = 3;
		public static final BooleanClientOption sprintAutoJump = new BooleanClientOption(false, "Lets you jump as soon as you hit the ground while sprinting.", "Gives you a slight speed boost when holding space too.");
		public static final BooleanClientOption avoidCactusDmg = new BooleanClientOption(false, "Prevents you from taking damage from cacti by making their", "collision box a little bigger than usual.");
		public static final BooleanClientOption collideAll = new BooleanClientOption(false, "Allows you to collide with (almost) every block in the game.", "Allows you to i.e. walk on cobweb, bushes, grass, etc...", "But not fluids, that's just too much.");
	}

	@Comment({"Chat related tweaks.", "Most of these are enabled by default."})
	public static class Chat {
		public static final int ordinal = 4;
		public static final BooleanClientOption chatMsgCopy = new BooleanClientOption(true, "Copy a chatmessage when you left-click on it.", "Holding control while doing so will also copy formattings.");
		public static final BooleanClientOption chatMsgRemove = new BooleanClientOption(true, "Remove a chatmessage when you right-click on it.");
		public static final BooleanClientOption ignoreEmptyMsgs = new BooleanClientOption(true, "Do not show messages that have no content in chat.");
		public static final BooleanClientOption infiniteChat = new BooleanClientOption(true, "Do not delete chatmessages when there are more than 100.");
		public static final BooleanClientOption showMsgTime = new BooleanClientOption(false, "Prepend messages in chat with the time they were received.");
		public static final BooleanClientOption use12HourClock = new BooleanClientOption(false, "Use a 12 hour clock instead of a 24 hour one.", "See 'Show Msg Time'.");
		public static final BooleanClientOption showSeconds = new BooleanClientOption(false, "Whether to also add seconds or not.", "See 'Show Msg Time'.");
	}

	@Comment({"Don't look in here.", "Stay away.", "", "Keep \u00A7c\u00A7lOUT\u00A7r!! >:c"})
	@IsHidden("hiddenOptions")
	public static class EasterEggs {
		public static final int ordinal = 5;
		public static final BooleanClientOption rainbows = new BooleanClientOption(false, "Everything is \u00A7urainbows\u00A7r.", "\u00A7lEverything", "\u00A74Not suitable for people prone to epileptic seizures!");
	}

	private static final Properties props = new Properties();
	private static final File f = new File("config/MoreCommands/clientoptions.txt");
	@SuppressWarnings("UnstableApiUsage")
	private static final List<Field> fields = Arrays.stream(ClientOptions.class.getClasses()).filter(c -> !c.isInterface()).sorted(Comparator.comparingInt(c -> ReflectionHelper.getFieldValue(c, "ordinal", null))).flatMap(c -> Arrays.stream(c.getFields())).filter(field -> ClientOption.class.isAssignableFrom(field.getType())).collect(ImmutableList.toImmutableList());
	@SuppressWarnings("UnstableApiUsage")
	private static final Map<String, ClientOption<?>> mappedOptions = fields.stream().collect(ImmutableMap.toImmutableMap(Field::getName, field -> ReflectionHelper.getFieldValue(field, null)));
	@SuppressWarnings("UnstableApiUsage")
	private static final Map<ClientOption<?>, String> reversedMappedOptions = mappedOptions.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getValue, Map.Entry::getKey));

	public static void update() {
		for (Class<?> c : ClientOptions.class.getClasses())
			for (Field f : c.getFields())
				if (ClientOption.class.isAssignableFrom(f.getType()))
					props.setProperty(f.getName(), ReflectionHelper.<ClientOption<?>, Object>getFieldValue(f, null).getValueString());
	}

	public static boolean write() {
		update();
		if (!f.exists()) {
			try {
				if (!f.createNewFile()) return false;
			} catch (IOException e) {
				MoreCommands.log.catching(e);
				return false;
			}
		}
		try (PrintWriter writer = new PrintWriter(f, "UTF-8")) {
			props.store(writer, "MoreCommand's client-only options.\nThese should not be changed manually, but rather be set via the in-game menu. (esc -> options -> MoreCommands)");
			writer.flush();
			return true;
		} catch (Exception e) {
			MoreCommands.log.error("An error occurred while saving the clientoptions.", e);
			return false;
		}
	}

	public static void read() {
		if (f.exists())
			try (FileReader reader = new FileReader(f)) {
				props.load(reader);
				getMappedOptions().forEach((key, value) -> {
					if (props.containsKey(key)) value.setValueString(props.getProperty(key));
				});
			} catch (IOException e) {
				MoreCommands.log.catching(e);
			}
		else write();
	}

	public static ClientOption<?> getOption(String option) {
		return getMappedOptions().get(option);
	}

	public static String getName(ClientOption<?> option) {
		return reversedMappedOptions.get(option);
	}

	public static List<ClientOption<?>> getOptions() {
		return ImmutableList.copyOf(getMappedOptions().values());
	}

	public static Map<String, ClientOption<?>> getMappedOptions() {
		return mappedOptions;
	}

	public static void reset() {
		ClientOption.resetAll();
		write();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE})
	public @interface Comment {
		String[] value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD, ElementType.TYPE})
	public @interface IsHidden {
		/**
		 * @return The name of the boolean option used to determine whether this option or class should be shown.
		 */
		String value();
	}
}
