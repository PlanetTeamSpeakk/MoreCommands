package com.ptsmods.morecommands.commands;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.ClientCommandHandler;

public class macro {

	public macro() {}

	public static class Commandmacro extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		private final Map<String, List<String>> macros = new HashMap();

		public Commandmacro() {
			try {
				loadFile();
			} catch (IOException e) {
				e.printStackTrace();
				macros.clear();
			}
		}

		private void loadFile() throws IOException {
			macros.clear();
			File f = new File("config/MoreCommands/macros.json");
			if (!f.exists()) f.createNewFile();
			macros.putAll(MoreObjects.firstNonNull(new Gson().fromJson(Reference.joinCustomChar("\n", Files.readAllLines(f.toPath()).toArray(new String[0])), Map.class), new HashMap()));
		}

		private void saveFile() throws IOException {
			PrintWriter writer = new PrintWriter(new FileWriter(new File("config/MoreCommands/macros.json")), true);
			new GsonBuilder().setPrettyPrinting().create().toJson(macros, writer);
			writer.flush();
			writer.close();
		}

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			List list = new ArrayList();
			switch (args.length) {
			case 1:
				list.addAll(Lists.newArrayList("create", "add", "insert", "remove", "view", "list"));
				list.addAll(macros.keySet());
				break;
			case 2:
				if (args[0].equals("add") || args[0].equals("view")) list.addAll(macros.keySet());
				break;
			case 3:
				if (args[0].equals("add")) {
					args = Reference.removeArgs(args, 0, 1);
					Map<String, ICommand> commands = new HashMap();
					commands.putAll(ClientCommandHandler.instance.getCommands());
					if (Reference.isSingleplayer()) commands.putAll(server.getCommandManager().getCommands());
					for (Entry<String, ICommand> command : commands.entrySet())
						if (command.getKey().startsWith(args[0])) list.add(command.getKey());
				} else if (args[0].equals("insert") || args[0].equals("remove")) list.addAll(macros.keySet());
				break;
			case 4:
				if (args[0].equals("insert")) {
					args = Reference.removeArgs(args, 0, 1, 2);
					Map<String, ICommand> commands = new HashMap();
					commands.putAll(ClientCommandHandler.instance.getCommands());
					if (Reference.isSingleplayer()) commands.putAll(server.getCommandManager().getCommands());
					for (Entry<String, ICommand> command : commands.entrySet())
						if (command.getKey().startsWith(args[0])) list.add(command.getKey());
					break;
				}
			default:
				if (args[0].equals("add") || args[0].equals("insert")) {
					args = args[0].equals("add") ? Reference.removeArgs(args, 0, 1) : Reference.removeArgs(args, 0, 1, 2);
					if (ClientCommandHandler.instance.getCommands().containsKey(args[0]) || Reference.isSingleplayer() && server.getCommandManager().getCommands().containsKey(args[0])) {
						ICommand command = MoreObjects.firstNonNull(ClientCommandHandler.instance.getCommands().get(args[0]), Reference.isSingleplayer() ? server.getCommandManager().getCommands().get(args[0]) : null);
						if (command == null) break;
						args = Reference.removeArg(args, 0);
						list.addAll(command.getTabCompletions(server, sender, args, pos));
						List newList = new ArrayList();
						for (String s : (List<String>) list)
							if (s.contains(":")) newList.add(new ResourceLocation(s));
							else newList.add(s);
						list = newList;
					}
				}
				break;
			}
			return getListOfStringsMatchingLastWord(args, list);
		}

		@Override
		public String getName() {
			return "macro";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			try {
				loadFile();
			} catch (IOException e) {}
			if (args.length == 0) Reference.sendCommandUsage(sender, usage);
			else if (args[0].equals("create")) {
				args = Reference.removeArg(args, 0);
				if (args.length == 0) Reference.sendCommandUsage(sender, "/macro create <name> Create a macro with the specified name.");
				else if (macros.containsKey(Reference.join(args))) Reference.sendMessage(sender, TextFormatting.RED + "A macro with the specified name already exists, to add or remove commands to/from the command, use /macro add or /macro remove.");
				else {
					macros.put(Reference.join(args), new ArrayList());
					try {
						saveFile();
					} catch (Exception e) {
						Reference.sendMessage(sender, "An unknown error occurred while attempting to save the macros file. Message: " + e.getMessage());
						macros.remove(Reference.join(args));
						return;
					}
					Reference.sendMessage(sender, "The macro '" + Reference.join(args) + "' was successfully created.");
				}
			} else if (args[0].equals("add")) {
				args = Reference.removeArg(args, 0);
				if (args.length < 2) Reference.sendCommandUsage(sender, "/macro add \"<name>\" <command> Insert a command into a macro, put the name of the macro in quotes if it contains whitespace, e.g. /macro insert 3 \"god potion\" potion add minecraft:resistance 127 7200 false false.");
				else {
					String name = args[0].contains("\"") ? Reference.join(args).split("\"")[0] : args[0];
					if (!macros.containsKey(name)) Reference.sendMessage(sender, TextFormatting.RED + "The given macro '" + name + "' appears not to exist.");
					else {
						String command;
						if (args[0].contains("\"")) command = Reference.join(Reference.removeArg(Reference.join(args).split("\""), 0));
						else command = Reference.join(Reference.removeArg(args, 0));
						macros.get(name).add(command);
						try {
							saveFile();
						} catch (IOException e) {
							e.printStackTrace();
							Reference.sendMessage(sender, "An unknown error occurred while attempting to save the macros file. Message: " + e.getMessage());
							macros.get(name).remove(command);
							return;
						}
						Reference.sendMessage(sender, "The command '" + command + "' has been added to the macro '" + name + "'.");
					}
				}
			} else if (args[0].equals("insert")) {
				args = Reference.removeArg(args, 0);
				if (args.length < 2 || !Reference.isInteger(args[0])) Reference.sendCommandUsage(sender, "/macro insert <index> \"<name>\" <command> Add a command to a macro, put the name of the macro in quotes if it contains whitespace, e.g. /macro add \"god potion\" potion add minecraft:resistance 127 7200 false false.");
				else if (Integer.parseInt(args[0]) <= 0) Reference.sendCommandUsage(sender, TextFormatting.RED + "Index cannot be less than or equal to 0.");
				else {
					int index = Integer.parseInt(args[0]);
					args = Reference.removeArg(args, 0);
					String name = args[0].contains("\"") ? Reference.join(args).split("\"")[0] : args[0];
					if (!macros.containsKey(name)) Reference.sendMessage(sender, TextFormatting.RED + "The given macro '" + name + "' appears not to exist.");
					else if (index > macros.get(name).size()) Reference.sendMessage(sender, TextFormatting.RED + "The given index (" + index + ") cannot be greater than the size of the given macro '" + name + "' (" + macros.get(name).size() + ").");
					else {
						String command;
						if (args[0].contains("\"")) command = Reference.join(Reference.removeArg(Reference.join(args).split("\""), 0));
						else command = Reference.join(Reference.removeArg(args, 0));
						macros.get(name).add(index - 1, command);
						try {
							saveFile();
						} catch (IOException e) {
							e.printStackTrace();
							Reference.sendMessage(sender, "An unknown error occurred while attempting to save the macros file. Message: " + e.getMessage());
							macros.get(name).remove(command);
							return;
						}
						Reference.sendMessage(sender, "The command '" + command + "' has been inserted into the macro '" + name + "' at an index of " + index + ".");
					}
				}
			} else if (args[0].equals("remove")) {
				args = Reference.removeArg(args, 0);
				if (args.length < 2 || !Reference.isInteger(args[0])) Reference.sendCommandUsage(sender, "/macro remove <index> <name> Remove a command from the given macro.");
				else {
					int index = Integer.parseInt(args[0]);
					String name = Reference.join(Reference.removeArg(args, 0));
					if (!macros.containsKey(name)) Reference.sendMessage(sender, TextFormatting.RED + "The given macro '" + name + "' appears not to exist.");
					else if (index > macros.get(name).size()) Reference.sendMessage(sender, TextFormatting.RED + "The given index (" + index + ") is greater than the amount of commands in the given macro '" + name + "' (" + macros.get(name).size() + ").");
					else {
						String command = macros.get(name).remove(index - 1);
						try {
							saveFile();
						} catch (IOException e) {
							e.printStackTrace();
							Reference.sendMessage(sender, "An unknown error occurred while attempting to save the macros file. Message: " + e.getMessage());
							macros.get(name).add(index - 1, command);
							return;
						}
						Reference.sendMessage(sender, "The command '" + command + "' at an index of " + index + " was successfully removed from the macro '" + name + "'.");
					}
				}
			} else if (args[0].equals("view")) {
				args = Reference.removeArg(args, 0);
				if (args.length == 0) Reference.sendCommandUsage(sender, "/macro view <name> View all the commands in the given macro.");
				else {
					String name = Reference.join(args);
					if (!macros.containsKey(name)) Reference.sendMessage(sender, TextFormatting.RED + "The given macro '" + name + "' appears not to exist.");
					else {
						String list = "Commands of macro '" + name + "':";
						for (int i = 0; i < macros.get(name).size(); i++)
							list += "\n" + (i + 1) + ". " + macros.get(name).get(i);
						Reference.sendMessage(sender, list);
					}
				}
			} else if (args[0].equals("list")) {
				if (macros.isEmpty()) Reference.sendMessage(sender, TextFormatting.RED + "It appears you have not made any macros yet.");
				else Reference.sendMessage(sender, "You have made the following macros:\n" + joinNiceStringFromCollection(macros.keySet()));
			} else if (args[0].equals("silent") && macros.containsKey(Reference.join(Reference.removeArg(args, 0))) || macros.containsKey(Reference.join(args))) {
				boolean silent = args[0].equals("silent");
				int success = 0;
				int fail = 0;
				for (String command : macros.get(Reference.join(silent ? Reference.removeArg(args, 0) : args)))
					try {
						Reference.sendChatMessage("/" + command);
						success++;
					} catch (Exception e) {
						e.printStackTrace();
						Reference.sendMessage(sender, TextFormatting.RED + "Command '" + command + "' of macro '" + Reference.join(args) + "' could not be run. Error message: " + e.getMessage() + ".");
						fail++;
					}
				if (!silent) Reference.sendMessage(sender, success + " command" + (success == 1 ? "" : "s") + " of macro '" + Reference.join(args) + "' were " + TextFormatting.GREEN + "successfully" + Reference.dtf + " ran" + (fail == 0 ? "." : ", while " + fail + " command" + (fail == 1 ? "" : "s") + " w" + (fail == 1 ? "as" : "ere") + TextFormatting.RED + " not" + Reference.dtf + "."));
			} else Reference.sendMessage(sender, TextFormatting.RED + "The given macro '" + Reference.join(args) + "' could not be found.");
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.CLIENT;
		}

		private String usage = "/macro <create|add|remove|view|list|<name>> Manage or run your macros. To run a macro, type for example /macro god potion.";

	}

}