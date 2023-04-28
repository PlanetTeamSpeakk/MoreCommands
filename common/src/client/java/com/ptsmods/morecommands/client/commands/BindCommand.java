package com.ptsmods.morecommands.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.MoreCommandsArch;
import com.ptsmods.morecommands.client.MoreCommandsClient;
import com.ptsmods.morecommands.api.callbacks.KeyEvent;
import com.ptsmods.morecommands.api.callbacks.MouseEvent;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import com.ptsmods.morecommands.client.arguments.KeyArgumentType;
import com.ptsmods.morecommands.client.miscellaneous.ClientCommand;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BindCommand extends ClientCommand {
    private static final File bindingsFile = MoreCommandsArch.getConfigDirectory().resolve("bindings.json").toFile();

    private static final Map<String, String> bindings = new HashMap<>();
    private static int record = 0;

    public void preinit() {
        if (bindingsFile.exists()) {
            try {
                bindings.putAll(MoreCommands.readJson(bindingsFile));
            } catch (IOException e) {
                log.catching(e);
            } catch (NullPointerException ignored) {}
        }
        else saveData();

        KeyEvent.EVENT.register((keyCode, scancode, action, mods) -> checkBind(keyCode, action));
        MouseEvent.EVENT.register((button, action, mods) -> checkBind(button + GLFW.GLFW_KEY_LAST+1, action));
    }

    private boolean checkBind(int keyCode, int action) {
        if (action == 1 && record > 0) {
            record--;
            sendMsg("You just pressed the " + SF + MoreCommandsClient.getKeyForKeyCode(keyCode) + DF + " key.");
        } else if (action > 0 && Minecraft.getInstance().screen == null) { // Either press or hold, but not release.
            String key = MoreCommandsClient.getKeyForKeyCode(keyCode);
            if (key != null && bindings.containsKey(key)) {
                ClientCompat.get().sendChatOrCmd(bindings.get(key));
                return true;
            }
        }
        return false;
    }

    @Override
    public void cRegister(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        dispatcher.register(cLiteral("bind")
                .then(cLiteral("set")
                        .then(cArgument("key", KeyArgumentType.key())
                                .then(cArgument("msg", StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                            String key = MoreCommandsClient.getKeyForKeyCode(KeyArgumentType.getKey(ctx, "key"));
                                            if (bindings.containsKey(key)) sendMsg(ChatFormatting.RED + "A binding has already been made with this key, if you want this key to send multiple messages, " +
                                                    "consider using macros, if not, consider removing the binding with /bind remove " + key + " first.");
                                            else {
                                                bindings.put(key, ctx.getArgument("msg", String.class));
                                                if (!bindings.get(key).startsWith("/")) sendMsg("The message does not start with a slash and thus is " + SF + "a normal message " + DF +
                                                        "and " + ChatFormatting.RED + "not " + SF + "a command" + DF + ".");
                                                saveData();
                                                sendMsg("A binding for key " + SF + key + DF + " has been saved.");
                                                return 1;
                                            }
                                            return 0;
                                        }))))
                .then(cLiteral("remove")
                        .then(cArgument("key", KeyArgumentType.key())
                                .executes(ctx -> {
                                    String key = MoreCommandsClient.getKeyForKeyCode(KeyArgumentType.getKey(ctx, "key"));
                                    if (!bindings.containsKey(key)) sendMsg(ChatFormatting.RED + "No binding has been set for that key yet.");
                                    else {
                                        bindings.remove(key);
                                        saveData();
                                        sendMsg("The binding for key " + SF + key + DF + " has been removed.");
                                        return 1;
                                    }
                                    return 0;
                                })))
                .then(cLiteral("list")
                        .executes(ctx -> {
                            if (bindings.isEmpty()) sendMsg(ChatFormatting.RED + "You have no bindings set yet, consider setting one with /bind set <key> <msg>.");
                            else {
                                sendMsg("You currently have the following bindings:");
                                bindings.forEach((key, msg) -> sendMsg("  " + key + ": " + SF + msg));
                                return bindings.size();
                            }
                            return 0;
                        }))
                .then(cLiteral("listkeys")
                        .executes(ctx -> {
                            sendMsg("The following keys are at your disposal: " + joinNicely(MoreCommandsClient.getKeys()) + ".");
                            return MoreCommandsClient.getKeys().size();
                        }))
                .then(cLiteral("record")
                        .executes(ctx -> executeRecord(2))
                        .then(cArgument("amount", IntegerArgumentType.integer(1))
                                .executes(ctx -> executeRecord(ctx.getArgument("amount", Integer.class))))));
    }

    @Override
    public String getDocsPath() {
        return "/bind";
    }

    private int executeRecord(int amount) {
        record = amount;
        sendMsg("The next " + SF + amount + DF + " key" + (amount == 1 ? "" : "s") + " pressed will have their name be printed in chat.");
        return amount;
    }

    private void saveData() {
        try {
            MoreCommands.saveJson(bindingsFile, bindings);
        } catch (IOException e) {
            log.catching(e);
        }
    }

}
