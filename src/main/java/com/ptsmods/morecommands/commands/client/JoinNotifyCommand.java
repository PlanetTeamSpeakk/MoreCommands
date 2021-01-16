package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.callbacks.PlayerListCallback;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.util.Formatting;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JoinNotifyCommand extends ClientCommand {

    private static final Map<String, String> players = new HashMap<>();
    private static final File f = new File("config/MoreCommands/joinNotifyPlayers.json");

    public void preinit() {
        try {
            players.putAll(MoreCommands.readJson(f));
        } catch (IOException e) {
            log.catching(e);
        } catch (NullPointerException ignored) {}
        registerCallback(PlayerListCallback.REMOVE, entry -> {
            if (entry != null && entry.getProfile() != null && players.containsKey(entry.getProfile().getId().toString())) {
                String s = SF + entry.getProfile().getName() + DF + " has " + Formatting.RED + "left " + DF + "the game.";
                sendMsg(s);
                sendAbMsg(s);
            }
        });
        registerCallback(PlayerListCallback.ADD, entry -> {
            if (entry != null && entry.getProfile() != null && players.containsKey(entry.getProfile().getId().toString())) {
                String s = SF + entry.getProfile().getName() + DF + " has " + Formatting.GREEN + "joined " + DF + "the game.";
                sendMsg(s);
                sendAbMsg(s);
            }
        });
    }

    @Override
    public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) {
        dispatcher.register(cLiteral("joinnotify").then(cArgument("player", EntityArgumentType.player()).executes(ctx -> {
            PlayerListEntry player = getPlayer(ctx, "player");
            String id = player.getProfile().getId().toString();
            if (players.containsKey(id)) players.remove(id);
            else players.put(id, player.getProfile().getName());
            try {
                MoreCommands.saveJson(f, players);
            } catch (IOException e) {
                log.catching(e);
                sendMsg(Formatting.RED + "Could not save the data file.");
                return 0;
            }
            sendMsg("You will " + formatFromBool(players.containsKey(id), Formatting.GREEN + "now ", Formatting.RED + "no longer ") + DF + "receive a notification when " + SF + player.getProfile().getName() + DF + " joins.");
            return 1;
        })));
    }
}
