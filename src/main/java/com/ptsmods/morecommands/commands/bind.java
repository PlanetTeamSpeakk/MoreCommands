package com.ptsmods.morecommands.commands;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class bind {

	public bind() {}

	public static class Commandbind extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		private final List<Map<String, Object>> binds;

		public Commandbind() throws IOException {
			File f = new File("config/MoreCommands/binds.json");
			if (!f.exists()) {
				new File("config/MoreCommands").mkdirs();
				f.createNewFile();
			}
			binds = new Gson().fromJson(Reference.joinCustomChar("\n", Files.readAllLines(f.toPath()).toArray(new String[0])), List.class);
			for (Map<String, Object> bind : binds)
				bind.put("key", ((Double) bind.get("key")).intValue()); // Gson loads all numbers as doubles.
		}

		@Override
		public List getAliases() {
			return Lists.newArrayList("keybind", "key", "binds");
		}

		@Override
		public List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			switch (args.length) {
			case 1:
				return getListOfStringsMatchingLastWord(args, Lists.newArrayList("set", "remove", "list", "listkeys"));
			case 2:
				switch (args[0]) {
				case "set":
					args[1] = args[1].toUpperCase();
					return getListOfStringsMatchingLastWord(args, getKeyNames());
				case "remove":
					List<String> keys = new ArrayList();
					for (Map<String, Object> bind : binds)
						keys.add(Keyboard.getKeyName((int) bind.get("key")));
					return getListOfStringsMatchingLastWord(args, keys);
				default:
					return new ArrayList();
				}
			default:
				return new ArrayList();
			}
		}

		@Override
		public String getName() {
			return "bind";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length == 0 || !"set".equals(args[0]) && !"remove".equals(args[0]) && !"list".equals(args[0]) && !"listkeys".equals(args[0])) Reference.sendCommandUsage(sender, usage);
			else switch (args[0]) {
			case "set":
				if (args.length < 3) Reference.sendCommandUsage(sender, "/bind set <key> <command or message> Add a new keybinding, for a list of valid keys run /bind listkeys, " + TextFormatting.DARK_RED + "make sure to put a forward slash (/) in front of commands" + TextFormatting.RED + ". Only 1 keybind can exist for each key, to run multiple commands try combining with /macro.");
				else if (Keyboard.getKeyIndex(args[1].toUpperCase()) == Keyboard.KEY_NONE) Reference.sendCommandUsage(sender, "The given key was unknown, for a list of keys run /bind listkeys.");
				else {
					for (Map<String, Object> bind : new ArrayList<>(binds))
						if ((int) bind.get("key") == Keyboard.getKeyIndex(args[1].toUpperCase())) binds.remove(bind);
					Map<String, Object> bind = new HashMap();
					bind.put("key", Keyboard.getKeyIndex(args[1].toUpperCase()));
					bind.put("cmd", Reference.joinCustomChar(" ", args).split(" ", 3)[2]);
					binds.add(bind);
					try {
						saveBinds();
					} catch (IOException e) {
						e.printStackTrace();
						Reference.sendMessage(sender, TextFormatting.RED + "Could not save the keybinds to disk.");
					}
					Reference.sendMessage(sender, "Keybind for key " + bind.get("key") + " (" + Keyboard.getKeyName(Keyboard.getKeyIndex(args[1].toUpperCase())) + ") has been set to " + (((String) bind.get("cmd")).startsWith("/") ? "run" : "say") + " " + TextFormatting.YELLOW + bind.get("cmd") + Reference.dtf + ".");
				}
				break;
			case "remove":
				if (args.length < 2) Reference.sendCommandUsage(sender, "/bind remove <key> Removes a command keybind from the list of keybinds.");
				else if (Keyboard.getKeyIndex(args[1].toUpperCase()) == Keyboard.KEY_NONE) Reference.sendMessage(sender, TextFormatting.RED + "The given key was unknown, for a list of set bindings run /bind list.");
				else {
					boolean found = false;
					int key = Keyboard.getKeyIndex(args[1].toUpperCase());
					for (Map<String, Object> bind : new ArrayList<>(binds))
						if ((int) bind.get("key") == key) {
							binds.remove(bind);
							try {
								saveBinds();
							} catch (IOException e) {
								e.printStackTrace();
								Reference.sendMessage(sender, TextFormatting.RED + "Could not save the keybinds to disk.");
							}
							Reference.sendMessage(sender, "Keybind for key " + key + " (" + Keyboard.getKeyName(key) + ") has been removed.");
							found = true;
						}
					if (!found) Reference.sendMessage(sender, TextFormatting.RED + "Could not find a keybind for key " + key + " (" + Keyboard.getKeyName(key) + ").");
				}
				break;
			case "list":
				if (binds.size() == 0) Reference.sendMessage(sender, "You do not have any keybindings set yet, try setting one for /screenshot 12x 12x for instance.");
				else {
					String msg = "You have the following keybinds set:";
					for (Map<String, Object> bind : binds)
						msg += "\n  " + Keyboard.getKeyName((int) bind.get("key")) + ": " + TextFormatting.YELLOW + bind.get("cmd");
					Reference.sendMessage(sender, msg);
				}
				break;
			case "listkeys":
				Reference.sendMessage(sender, "A full list of all keys: " + joinNiceStringFromCollection(getKeyNames()) + ".");
			}
		}

		public static List<String> getKeyNames() {
			List<String> keys = new ArrayList();
			for (Field f : Reference.getFields(Keyboard.class))
				if (f.getName().startsWith("KEY_")) try {
					String key = Keyboard.getKeyName(f.getInt(null));
					if (!key.equals("NONE")) keys.add(key);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			return keys;
		}

		private void saveBinds() throws IOException {
			PrintWriter writer = new PrintWriter(new FileWriter(new File("config/MoreCommands/binds.json")));
			new GsonBuilder().setPrettyPrinting().create().toJson(binds, writer);
			writer.flush();
			writer.close();
		}

		private final List<Integer> pressedKeys = new ArrayList();

		@SubscribeEvent
		public void onKeyInput(KeyInputEvent event) {
			if (Keyboard.getEventKeyState()) {
				if (!pressedKeys.contains(Keyboard.getEventKey())) {
					for (Map<String, Object> bind : binds)
						if (Keyboard.getEventKey() == (int) bind.get("key")) Reference.sendChatMessage((String) bind.get("cmd"));
					pressedKeys.add(Keyboard.getEventKey());
				}
			} else pressedKeys.remove((Integer) Keyboard.getEventKey());
		}

		@SubscribeEvent
		public void onClientTick(ClientTickEvent event) {
			if (Minecraft.getMinecraft().currentScreen != null && !Minecraft.getMinecraft().currentScreen.allowUserInput) pressedKeys.clear();
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.CLIENT;
		}

		private String usage = "/bind <set|remove|list|listkeys> Manage keybinds, allows you to bind a command or chat message to a certain key.";

	}

}