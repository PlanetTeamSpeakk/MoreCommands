package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsArch;
import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.api.callbacks.PlayerListEvent;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.clientoption.ClientOptions;
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
				sendMsg(Formatting.RED + "Could not save the joinnotify data file.");
				return 0;
			}
			sendMsg("You will " + Util.formatFromBool(players.containsKey(id), Formatting.GREEN + "now ", Formatting.RED + "no longer ") +
					DF + "receive a notification when " + SF + player.getProfile().getName() + DF + " joins.");
			return 1;
		})));
	}

	private void onCall(PlayerListEntry entry, boolean joined) {
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
