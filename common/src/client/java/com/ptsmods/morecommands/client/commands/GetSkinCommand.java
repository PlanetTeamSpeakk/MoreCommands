package com.ptsmods.morecommands.client.commands;

import com.google.gson.Gson;
import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.client.miscellaneous.ClientCommand;
import java.util.Base64;
import java.util.Map;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ClickEvent;

public class GetSkinCommand extends ClientCommand {
    @Override
    public void cRegister(CommandDispatcher<ClientSuggestionProvider> dispatcher) throws Exception {
        dispatcher.register(cLiteral("getskin")
                .then(cArgument("player", EntityArgument.player())
                        .executes(ctx -> {
                            PlayerInfo player = getPlayer(ctx, "player");
                            if (player.getProfile().getProperties().get("textures").isEmpty()) sendError("That player does not have a skin or cape.");
                            else {
                                Map<?, ?> textures = (Map<?, ?>) new Gson().fromJson(new String(Base64.getDecoder().decode(player.getProfile().getProperties().get("textures").iterator().next().getValue())), Map.class).get("textures");
                                String skinUrl = (String) ((Map<?, ?>) textures.get("SKIN")).get("url");
                                String capeUrl = textures.containsKey("CAPE") ? (String) ((Map<?, ?>) textures.get("CAPE")).get("url") : null;
                                sendMsg(literalText(SF + player.getProfile().getName() + "'s " + DF + "skin can be found at ")
                                        .withStyle(DS)
                                        .append(literalText(skinUrl)
                                                .withStyle(SS
                                                        .withUnderlined(true)
                                                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, skinUrl))))
                                        .append(capeUrl != null ? literalText(" and their cape can be found at ", DS)
                                                .append(literalText(capeUrl)
                                                        .withStyle(SS.withUnderlined(true)
                                                                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, capeUrl)))) : literalText(""))
                                        .append(literalText(".")));
                                return 1;
                            }
                            return 0;
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/get-skin";
    }
}
