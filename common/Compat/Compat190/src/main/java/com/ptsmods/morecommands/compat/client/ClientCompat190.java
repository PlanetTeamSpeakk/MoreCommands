package com.ptsmods.morecommands.compat.client;

import com.mojang.brigadier.ParseResults;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.mixin.compat.compat190.MixinLocalPlayerAccessor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.network.chat.MessageSigner;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;

public class ClientCompat190 extends ClientCompat19 {

    @Override
    public Packet<ServerGamePacketListener> newChatMessagePacket(LocalPlayer player, String message, boolean forceChat) {
        MessageSigner signer = MessageSigner.create(player.getUUID());
        MixinLocalPlayerAccessor accessor = (MixinLocalPlayerAccessor) player;

        if (!message.startsWith("/")) return new ServerboundChatPacket(message, accessor.callSignMessage(signer, LiteralTextBuilder.literal(message)), false);

        message = message.substring(1);
        ParseResults<SharedSuggestionProvider> parseResults = player.connection.getCommands().parse(message, player.connection.getSuggestionsProvider());
        ArgumentSignatures argumentSignatures = accessor.callSignCommandArguments(signer, parseResults, null);
        return new ServerboundChatCommandPacket(message, signer.timeStamp(), argumentSignatures, false);
    }
}
