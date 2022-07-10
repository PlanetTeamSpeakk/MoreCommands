package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsArch;
import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.api.callbacks.PlayerListEvent;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.commands.arguments.EntityArgument;

public class JoinNotifyCommand extends ClientCommand {
    private static final Map<String, String> players = new HashMap<>();
    private static final File f = MoreCommandsArch.getConfigDirectory().resolve("joinNotifyPlayers.json").toFile();

    public void preinit() {
        try {
            players.putAll(MoreCommands.readJson(f));
        } catch (IOException e) {
            log.catching(e);
        } catch (NullPointerException ignored) {}

        PlayerListEvent.REMOVE.register(entry -> onCall(entry, false));
        PlayerListEvent.ADD.register(entry -> onCall(entry, true));
    }

    @Override
    public void cRegister(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        dispatcher.register(cLiteral("joinnotify")
                .then(cArgument("player", EntityArgument.player())
                        .executes(ctx -> {
                            PlayerInfo player = getPlayer(ctx, "player");
                            String id = player.getProfile().getId().toString();
                            if (players.containsKey(id)) players.remove(id);
                            else players.put(id, player.getProfile().getName());
                            try {
                                MoreCommands.saveJson(f, players);
                            } catch (IOException e) {
                                log.catching(e);
                                sendMsg(ChatFormatting.RED + "Could not save the joinnotify data file.");
                                return 0;
                            }
                            sendMsg("You will " + Util.formatFromBool(players.containsKey(id), ChatFormatting.GREEN + "now ", ChatFormatting.RED + "no longer ") +
                                    DF + "receive a notification when " + SF + player.getProfile().getName() + DF + " joins.");
                            return 1;
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/join-notify";
    }

    private void onCall(PlayerInfo entry, boolean joined) {
        String id = entry == null || entry.getProfile() == null ? null : entry.getProfile().getId().toString();
        Map<String, String> nameMCFriends = MoreCommandsClient.getNameMCFriends();
        if (id != null && (players.containsKey(id) || ClientOptions.Tweaks.joinNotifyNameMC.getValue() && MoreCommandsClient.getNameMCFriends().containsKey(id))) {
            String cachedName = players.getOrDefault(id, nameMCFriends.get(id));
            String s = SF + entry.getProfile().getName() + (entry.getProfile().getName().equals(cachedName) ? "" : DF + " (previously known as " + SF + cachedName + DF + ")") +
                    DF + " has " + Util.formatFromBool(joined, "joined", "left") + DF + " the game.";
            if (!entry.getProfile().getName().equals(cachedName)) {
                if (players.containsKey(id)) {
                    players.put(id, entry.getProfile().getName());
                    try {
                        MoreCommands.saveJson(f, players);
                    } catch (IOException e) {
                        log.error("Could not save the joinnotify data file.", e);
                    }
                }
                if (nameMCFriends.containsKey(id)) MoreCommandsClient.updateNameMCFriend(id, entry.getProfile().getName());
            }
            sendMsg(s);
            sendAbMsg(s);
        }
    }
}
