package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsArch;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.api.util.text.TextBuilder;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AliasCommand extends ClientCommand {

	private static final File aliasesFile = MoreCommandsArch.getConfigDirectory().resolve("aliases.json").toFile();
	private final Map<String, String> aliases = new HashMap<>();

	public void preinit() {
		if (aliasesFile.exists()) {
			try {
				aliases.putAll(MoreCommands.readJson(aliasesFile));
			} catch (IOException e) {
				log.catching(e);
			} catch (NullPointerException ignored) {}
			aliases.keySet().forEach(this::register);
		} else saveData();

		ClientCompat.get().registerChatProcessListener(message -> {
			TextBuilder<?> builder = Compat.get().builderFromText(message);
			if (!(builder instanceof LiteralTextBuilder)) return message;

			String replacement = aliases.getOrDefault(((LiteralTextBuilder) message).getLiteral().substring(1).split(" ")[0], null);
			return LiteralTextBuilder.builder(replacement).build();
		});
	}

	@Override
	public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) {
		dispatcher.register(cLiteral("alias").then(cLiteral("create").then(cArgument("name", StringArgumentType.word()).then(cArgument("msg", StringArgumentType.greedyString()).executes(ctx -> {
			String name = ctx.getArgument("name", String.class);
			if (aliases.containsKey(name)) sendMsg(Formatting.RED + "An alias by that name already exists.");
			else {
				aliases.put(name, ctx.getArgument("msg", String.class));
				if (!aliases.get(name).startsWith("/")) sendMsg("The message does not start with a slash and thus is " + SF + "a normal message " + DF + "and " + Formatting.RED + "not " + SF + "a command" + DF + ".");
				register(name);
				saveData();
				sendMsg("An alias by the name of " + SF + name + DF + " has been added.");
				return 1;
			}
			return 0;
		})))).then(cLiteral("remove").then(cArgument("name", StringArgumentType.word()).executes(ctx -> {
			String name = ctx.getArgument("name", String.class);
			if (!aliases.containsKey(name)) sendMsg(Formatting.RED + "An alias by that name does not exist.");
			else {
				aliases.remove(name);
				CommandDispatcher<CommandSource> disp = getPlayer().networkHandler.getCommandDispatcher();
				MoreCommands.removeNode(disp, disp.getRoot().getChild(name));
				saveData();
				sendMsg("Alias " + SF + name + DF + " has been removed.");
				return 1;
			}
			return 0;
		}))).then(cLiteral("list").executes(ctx -> {
			if (aliases.isEmpty()) sendMsg(Formatting.RED + "You do not have any aliases yet, consider creating one with /alias create <name> <msg>.");
			else {
				sendMsg("You currently have the following aliases:");
				aliases.forEach((key, value) -> sendMsg("  " + key + ": " + SF + value));
				return aliases.size();
			}
			return 0;
		})));
	}

	private void saveData() {
		try {
			MoreCommands.saveJson(aliasesFile, aliases);
		} catch (IOException e) {
			log.catching(e);
		}
	}

	private void register(String name) {
		getPlayer().networkHandler.getCommandDispatcher().register(LiteralArgumentBuilder.<CommandSource>literal(name).executes(ctx0 -> 1));
	}

}
