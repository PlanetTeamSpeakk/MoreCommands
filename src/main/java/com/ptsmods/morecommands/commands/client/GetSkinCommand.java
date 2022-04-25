package com.ptsmods.morecommands.commands.client;

import com.google.gson.Gson;
import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.text.ClickEvent;

import java.util.Base64;
import java.util.Map;

public class GetSkinCommand extends ClientCommand {
	@Override
	public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) throws Exception {
		dispatcher.register(cLiteral("getskin")
				.then(cArgument("player", EntityArgumentType.player())
						.executes(ctx -> {
							PlayerListEntry player = getPlayer(ctx, "player");
							if (player.getProfile().getProperties().get("textures").isEmpty()) sendError("That player does not have a skin or cape.");
							else {
								Map<?, ?> textures = (Map<?, ?>) new Gson().fromJson(new String(Base64.getDecoder().decode(player.getProfile().getProperties().get("textures").iterator().next().getValue())), Map.class).get("textures");
								String skinUrl = (String) ((Map<?, ?>) textures.get("SKIN")).get("url");
								String capeUrl = textures.containsKey("CAPE") ? (String) ((Map<?, ?>) textures.get("CAPE")).get("url") : null;
								sendMsg(literalText(SF + player.getProfile().getName() + "'s " + DF + "skin can be found at ")
										.withStyle(DS)
										.append(literalText(skinUrl)
												.withStyle(SS
														.withUnderline(true)
														.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, skinUrl))))
										.append(capeUrl != null ? literalText(" and their cape can be found at ", DS)
												.append(literalText(capeUrl)
														.withStyle(SS.withUnderline(true)
																.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, capeUrl)))) : literalText(""))
										.append(literalText(".")));
								return 1;
							}
							return 0;
						})));
	}
}
