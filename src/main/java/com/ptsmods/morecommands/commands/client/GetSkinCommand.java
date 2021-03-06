package com.ptsmods.morecommands.commands.client;

import com.google.gson.Gson;
import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.Base64;
import java.util.Map;

public class GetSkinCommand extends ClientCommand {
    @Override
    public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) throws Exception {
        dispatcher.register(cLiteral("getskin").then(cArgument("player", EntityArgumentType.player()).executes(ctx -> {
            PlayerListEntry player = getPlayer(ctx, "player");
            Map<?, ?> textures = (Map<?, ?>) new Gson().fromJson(new String(Base64.getDecoder().decode(player.getProfile().getProperties().get("textures").iterator().next().getValue())), Map.class).get("textures");
            String skinUrl = (String) ((Map<?, ?>) textures.get("SKIN")).get("url");
            String capeUrl = textures.containsKey("CAPE") ? (String) ((Map<?, ?>) textures.get("CAPE")).get("url") : null;
            sendMsg(new LiteralText(SF + player.getProfile().getName() + "'s " + DF + "skin can be found at ").setStyle(DS).append(new LiteralText(skinUrl).setStyle(SS.withUnderline(true).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, skinUrl))))
                    .append(capeUrl != null ? new LiteralText(" and their cape can be found at ").setStyle(DS).append(new LiteralText(capeUrl).setStyle(SS.withUnderline(true).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, capeUrl)))) : new LiteralText(""))
                    .append(new LiteralText(".")));
            return 1;
        })));
    }
}
