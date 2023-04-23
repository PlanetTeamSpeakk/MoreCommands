package com.ptsmods.morecommands.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.MoreCommandsArch;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import com.ptsmods.morecommands.api.util.extensions.ObjectExtensions;
import com.ptsmods.morecommands.client.miscellaneous.ClientCommand;
import lombok.experimental.ExtensionMethod;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.SharedSuggestionProvider;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ExtensionMethod(ObjectExtensions.class)
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
            String replacement = message.startsWith("/") ? aliases.getOrDefault(message.substring(1).split(" ")[0], null) : null;
            return replacement.or(message);
        });
    }

    @Override
    public void cRegister(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        dispatcher.register(cLiteral("alias")
                .then(cLiteral("create")
                        .then(cArgument("name", StringArgumentType.word())
                                .then(cArgument("msg", StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                            String name = ctx.getArgument("name", String.class);
                                            if (aliases.containsKey(name)) sendMsg(ChatFormatting.RED + "An alias by that name already exists.");
                                            else {
                                                aliases.put(name, ctx.getArgument("msg", String.class));
                                                if (!aliases.get(name).startsWith("/")) sendMsg("The message does not start with a slash and thus is " + SF + "a normal message " + DF + "and " +
                                                        ChatFormatting.RED + "not " + SF + "a command" + DF + ".");
                                                register(name);
                                                saveData();
                                                sendMsg("An alias by the name of " + SF + name + DF + " has been added.");
                                                return 1;
                                            }
                                            return 0;
                                        }))))
                .then(cLiteral("remove")
                        .then(cArgument("name", StringArgumentType.word())
                                .executes(ctx -> {
                                    String name = ctx.getArgument("name", String.class);
                                    if (!aliases.containsKey(name)) sendMsg(ChatFormatting.RED + "An alias by that name does not exist.");
                                    else {
                                        aliases.remove(name);
                                        CommandDispatcher<SharedSuggestionProvider> disp = getPlayer().connection.getCommands();
                                        MoreCommands.removeNode(disp, disp.getRoot().getChild(name));
                                        saveData();
                                        sendMsg("Alias " + SF + name + DF + " has been removed.");
                                        return 1;
                                    }
                                    return 0;
                                })))
                .then(cLiteral("list")
                        .executes(ctx -> {
                            if (aliases.isEmpty()) sendMsg(ChatFormatting.RED + "You do not have any aliases yet, consider creating one with /alias create <name> <msg>.");
                            else {
                                sendMsg("You currently have the following aliases:");
                                aliases.forEach((key, value) -> sendMsg("  " + key + ": " + SF + value));
                                return aliases.size();
                            }
                            return 0;
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/alias";
    }

    private void saveData() {
        try {
            MoreCommands.saveJson(aliasesFile, aliases);
        } catch (IOException e) {
            log.catching(e);
        }
    }

    private void register(String name) {
        getPlayer().connection.getCommands().register(LiteralArgumentBuilder.<SharedSuggestionProvider>literal(name).executes(ctx0 -> 1));
    }

}
