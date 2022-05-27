package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsArch;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.util.Formatting;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MacroCommand extends ClientCommand {

    private static final File file = MoreCommandsArch.getConfigDirectory().resolve("macros.json").toFile();
    private final Map<String, List<String>> macros = new HashMap<>();

    public void preinit() {
        if (!file.exists()) saveData();
        else {
            try {
                macros.putAll(MoreCommands.readJson(file));
            } catch (IOException e) {
                log.catching(e);
            } catch (NullPointerException ignored) {}
        }
    }

    @Override
    public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) {
        dispatcher.register(cLiteral("macro")
                .then(cLiteral("create")
                        .then(cArgument("name", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    String name = ctx.getArgument("name", String.class);
                                    if (macros.containsKey(name)) sendMsg("A macro by that name already exists, to add commands to it, use " + SF + "/macro add <macro> <command>" + DF + ".");
                                    else {
                                        macros.put(name, new ArrayList<>());
                                        saveData();
                                        sendMsg("The macro has been created, you can add commands to it with " + SF + "/macro add " + (name.contains(" ") ? "\"" + name + "\"" : name) + " <command>" + DF + ".");
                                        return 1;
                                    }
                                    return 0;
                                })))
                .then(cLiteral("add")
                        .then(cArgument("macro", StringArgumentType.string())
                                .then(cArgument("index", IntegerArgumentType.integer())
                                        .then(cArgument("msg", StringArgumentType.greedyString())
                                                .executes(ctx -> executeAdd(ctx, ctx.getArgument("index", Integer.class)))))
                                .then(cArgument("msg", StringArgumentType.greedyString())
                                        .executes(ctx -> executeAdd(ctx, -1)))))
                .then(cLiteral("remove")
                        .then(cArgument("macro", StringArgumentType.string())
                                .then(cArgument("index", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {
                                            String macro = ctx.getArgument("macro", String.class);
                                            int index = ctx.getArgument("index", Integer.class)-1;
                                            if (!macros.containsKey(macro)) sendMsg(Formatting.RED + "A macro by the given name could not be found.");
                                            else if (index >= macros.get(macro).size()) sendMsg(Formatting.RED + "The given index was greater than the amount of commands in this macro (" + SF + macros.get(macro).size() + DF + ").");
                                            else {
                                                String cmd = macros.get(macro).remove(index);
                                                saveData();
                                                sendMsg("The command " + SF + cmd + DF + " with an index of " + SF + index + DF + " has been removed.");
                                                return macros.get(macro).size() + 1;
                                            }
                                            return 0;
                                        })))
                        .then(cArgument("macro", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    String macro = ctx.getArgument("macro", String.class);
                                    if (!macros.containsKey(macro)) sendMsg(Formatting.RED + "A macro by the given name could not be found.");
                                    else {
                                        macros.remove(macro);
                                        saveData();
                                        sendMsg("The macro has been removed.");
                                        return 1;
                                    }
                                    return 0;
                                })))
                .then(cLiteral("view")
                        .then(cArgument("macro", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    String macro = ctx.getArgument("macro", String.class);
                                    if (!macros.containsKey(macro)) sendMsg(Formatting.RED + "A macro by the given name could not be found.");
                                    else if (macros.get(macro).isEmpty()) sendMsg(Formatting.RED + "The given macro does not yet have any commands added, consider adding some with " +
                                            SF + "/macro add " + (macro.contains(" ") ? "\"" + macro + "\"" : macro) + " <command>" + DF + ".");
                                    else {
                                        StringBuilder msg = new StringBuilder("Commands of macro " + SF + macro + DF + ":");
                                        for (int i = 0; i < macros.get(macro).size(); i++)
                                            msg.append("\n  ").append(i + 1).append(". ").append(SF).append(macros.get(macro).get(i));
                                        sendMsg(msg.toString());
                                        return 1;
                                    }
                                    return 0;
                                })))
                .then(cLiteral("list")
                        .executes(ctx -> {
                            if (macros.isEmpty()) sendMsg(Formatting.RED + "You have not made any macros yet, consider making some with /macro create <name>.");
                            else sendMsg("You have the following macros: " + joinNicely(macros.keySet()) + ".");
                            return macros.size();
                        }))
                .then(cArgument("macro", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            String macro = ctx.getArgument("macro", String.class);
                            if (!macros.containsKey(macro)) sendMsg(Formatting.RED + "A macro by the given name could not be found.");
                            else {
                                for (String msg : macros.get(macro)) ClientCompat.get().sendMessageOrCommand(msg);
                                return macros.get(macro).size();
                            }
                            return 0;
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/client/macro";
    }

    private int executeAdd(CommandContext<ClientCommandSource> ctx, int index) {
        String macro = ctx.getArgument("macro", String.class);
        String msg = ctx.getArgument("msg", String.class);
        if (!macros.containsKey(macro)) sendMsg(Formatting.RED + "A macro by the given name could not be found.");
        else {
            macros.get(macro).add(index == -1 ? macros.get(macro).size() : index, msg);
            saveData();
            if (!msg.startsWith("/")) sendMsg("The message does not start with a slash and thus is " + SF + "a normal message " + DF + "and " + Formatting.RED + "not " + SF + "a command" + DF + ".");
            sendMsg("The message has been added to macro " + SF + macro + DF + " with an index of " + SF + (macros.get(macro).size()-1) + DF + ".");
            return macros.get(macro).size();
        }
        return 0;
    }

    private void saveData() {
        try {
            MoreCommands.saveJson(file, macros);
        } catch (IOException e) {
            log.catching(e);
        }
    }
}
