package com.ptsmods.morecommands.commands.server.elevated;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.MoreCommandsArch;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

// Instead of permissions, we add the ability to disable commands entirely and, where possible, just use Minecraft's default permission level instead.
public class DisableCommand extends Command {
    private static final SimpleCommandExceptionType DISABLED = new SimpleCommandExceptionType(literalText("This command is currently disabled.").build());
    private static final Field commandField = ReflectionHelper.getField(CommandNode.class, "command");
    private static final List<UUID> remindedLP = new ArrayList<>();
    private final Map<String, com.mojang.brigadier.Command<CommandSourceStack>> disabledCommands = new HashMap<>();
    private final List<String> disabledPaths = new ArrayList<>();
    private static File file = null;

    public void init(boolean serverOnly, MinecraftServer server) {
        if (MoreCommandsArch.getConfigDirectory().resolve("disabled.json").toFile().exists()) MoreCommands.tryMove("config/MoreCommands/disabled.json", MoreCommands.getRelativePath().resolve("disabled.json").toString());
        file = MoreCommands.getRelativePath().resolve("disabled.json").toFile();
        if (!file.exists()) saveData();
        else {
            try {
                List<String> data = MoreCommands.readJson(file);
                disabledPaths.addAll(data);
            } catch (IOException e) {
                log.catching(e);
            } catch (NullPointerException ignored) {}
        }
        CommandDispatcher<CommandSourceStack> dispatcher = server.getCommands().getDispatcher();
        disabledCommands.clear();
        disabledPaths.forEach(path -> {
            String[] parts = path.split("\\.");
            CommandNode<CommandSourceStack> parent = dispatcher.getRoot();
            List<CommandNode<CommandSourceStack>> nodes = Lists.newArrayList();
            for (String part : parts) {
                nodes.add(parent.getChild(part));
                parent = nodes.get(nodes.size() - 1);
            }
            disable(nodes, false);
        });
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReqOp("disable")
                .then(argument("cmd", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            try {
                                String cmd = ctx.getArgument("cmd", String.class);
                                ParseResults<CommandSourceStack> results = dispatcher.parse(cmd, ctx.getSource().getServer().createCommandSourceStack());
                                if (results.getContext().getNodes().isEmpty()) sendError(ctx, "That command could not be found.");
                                else {
                                    List<CommandNode<CommandSourceStack>> nodes = new ArrayList<>();
                                    results.getContext().getNodes().forEach(node -> nodes.add(node.getNode()));
                                    sendMsg(ctx, "The command has been " + Util.formatFromBool(!disable(nodes, true), "enabled", "disabled") + ".");
                                    return 1;
                                }
                                return 0;
                            } finally {
                                if (ctx.getSource().getEntity() != null && !remindedLP.contains(ctx.getSource().getEntityOrException().getUUID())) {
                                    sendMsg(ctx, literalText("", Style.EMPTY.applyFormat(ChatFormatting.RED))
                                            .append(literalText("This command is deprecated, you should consider using "))
                                            .append(emptyText(Style.EMPTY
                                                            .applyFormats(ChatFormatting.UNDERLINE, ChatFormatting.BOLD)
                                                            .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://luckperms.net/"))
                                                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, literalText("Click to download").build())))
                                                    .append(literalText("Luck", Style.EMPTY.applyFormat(ChatFormatting.AQUA)))
                                                    .append(literalText("Perms", Style.EMPTY.applyFormat(ChatFormatting.DARK_AQUA))))
                                            .append(literalText(" instead.")));
                                    remindedLP.add(ctx.getSource().getEntityOrException().getUUID());
                                }
                            }
                        })));
    }

    @Override
    public boolean isDedicatedOnly() {
        return true;
    }

    @Override
    public String getDocsPath() {
        return "/elevated/disable";
    }

    private boolean disable(List<CommandNode<CommandSourceStack>> nodes, boolean update) {
        CommandNode<CommandSourceStack> node = nodes.get(nodes.size()-1);
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

    private String getPath(List<CommandNode<CommandSourceStack>> nodes) {
        StringBuilder path = new StringBuilder();
        for (CommandNode<CommandSourceStack> node : nodes)
            path.append('.').append(node.getName());
        return path.substring(1);
    }

    private void disableChildren(String parentPath, CommandNode<CommandSourceStack> parent, boolean update) {
        for (CommandNode<CommandSourceStack> child : parent.getChildren()) {
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

    private com.mojang.brigadier.Command<CommandSourceStack> createDisabledCommand(com.mojang.brigadier.Command<CommandSourceStack> original) {
        return ctx -> {
            if (isOp(ctx)) return original.run(ctx);
            else throw DISABLED.create();
        };
    }

    private void setCommand(CommandNode<CommandSourceStack> node, com.mojang.brigadier.Command<CommandSourceStack> cmd) {
        ReflectionHelper.setFieldValue(commandField, node, cmd);
    }
}
