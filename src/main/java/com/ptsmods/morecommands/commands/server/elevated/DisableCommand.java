package com.ptsmods.morecommands.commands.server.elevated;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.callbacks.CommandsRegisteredCallback;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

// Instead of permissions, we add the ability to disable commands entirely and, where possible, just use Minecraft's default permission level instead.
public class DisableCommand extends Command {
	private static final SimpleCommandExceptionType DISABLED = new SimpleCommandExceptionType(new LiteralText("This command is currently disabled."));
	private static final Field commandField = ReflectionHelper.getField(CommandNode.class, "command");;
	private final Map<String, com.mojang.brigadier.Command<ServerCommandSource>> disabledCommands = new HashMap<>();
	private final List<String> disabledPaths = new ArrayList<>();
	private static File file = null;

	public void init(MinecraftServer server) {
		if (new File("config/MoreCommands/disabled.json").exists()) MoreCommands.tryMove("config/MoreCommands/disabled.json", MoreCommands.getRelativePath() + "disabled.json");
		file = new File(MoreCommands.getRelativePath() + "disabled.json");
		if (!file.exists()) saveData();
		else {
			try {
				List<String> data = MoreCommands.readJson(file);
				disabledPaths.addAll(data);
			} catch (IOException e) {
				log.catching(e);
			} catch (NullPointerException ignored) {}
		}
		CommandDispatcher<ServerCommandSource> dispatcher = server.getCommandManager().getDispatcher();
		disabledCommands.clear();
		disabledPaths.forEach(path -> {
			String[] parts = path.split("\\.");
			CommandNode<ServerCommandSource> parent = dispatcher.getRoot();
			List<CommandNode<ServerCommandSource>> nodes = Lists.newArrayList();
			for (String part : parts) {
				nodes.add(parent.getChild(part));
				parent = nodes.get(nodes.size() - 1);
			}
			disable(nodes, false);
		});
	}

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("disable").requires(IS_OP).then(argument("cmd", StringArgumentType.greedyString()).executes(ctx -> {
			String cmd = ctx.getArgument("cmd", String.class);
			ParseResults<ServerCommandSource> results = dispatcher.parse(cmd, ctx.getSource().getMinecraftServer().getCommandSource());
			if (results.getContext().getNodes().isEmpty()) sendMsg(ctx, Formatting.RED + "That command could not be found.");
			else {
				List<CommandNode<ServerCommandSource>> nodes = new ArrayList<>();
				results.getContext().getNodes().forEach(node -> nodes.add(node.getNode()));
				sendMsg(ctx, "The command has been " + formatFromBool(!disable(nodes, true), "enabled", "disabled") + ".");
				return 1;
			}
			return 0;
		})));
	}

	@Override
	public boolean forDedicated() {
		return true;
	}

	private boolean disable(List<CommandNode<ServerCommandSource>> nodes, boolean update) {
		CommandNode<ServerCommandSource> node = nodes.get(nodes.size()-1);
		String path = getPath(nodes);
		if (disabledCommands.containsKey(path)) {
			disabledPaths.remove(path);
			setCommand(node, disabledCommands.remove(path));
			disableChildren(path, node, update);
			saveData();
			return false;
		} else {
			if (update) {
				disabledPaths.add(path);
				saveData();
			}
			disabledCommands.put(path, node.getCommand());
			if (node.getCommand() != null) setCommand(node, ctx -> {
				throw DISABLED.create();
			});
			disableChildren(path, node, update);
			if (update) saveData();
			return true;
		}
	}

	private String getPath(List<CommandNode<ServerCommandSource>> nodes) {
		StringBuilder path = new StringBuilder();
		for (CommandNode<ServerCommandSource> node : nodes)
			path.append('.').append(node.getName());
		return path.substring(1);
	}

	private void disableChildren(String parentPath, CommandNode<ServerCommandSource> parent, boolean update) {
		for (CommandNode<ServerCommandSource> child : parent.getChildren()) {
			String path = parentPath + "." + child.getName();
			if (disabledCommands.containsKey(path)) {
				disabledPaths.remove(path);
				setCommand(child, disabledCommands.remove(path));
			}
			else {
				if (update) disabledPaths.add(path);
				disabledCommands.put(path, child.getCommand());
				if (child.getCommand() != null) setCommand(child, createDisabledCommand(child.getCommand()));
			}
			disableChildren(path, child, update);
		}
	}

	private void saveData() {
		try {
			MoreCommands.saveJson(file, disabledPaths);
		} catch (IOException e) {
			log.catching(e);
		}
	}

	private com.mojang.brigadier.Command<ServerCommandSource> createDisabledCommand(com.mojang.brigadier.Command<ServerCommandSource> original) {
		return ctx -> {
			if (isOp(ctx)) return original.run(ctx);
			else throw DISABLED.create();
		};
	}

	private void setCommand(CommandNode<ServerCommandSource> node, com.mojang.brigadier.Command<ServerCommandSource> cmd) {
		ReflectionHelper.setFieldValue(commandField, node, cmd);
	}
}
