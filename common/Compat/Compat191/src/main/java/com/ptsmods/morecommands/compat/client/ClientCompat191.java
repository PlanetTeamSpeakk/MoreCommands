package com.ptsmods.morecommands.compat.client;

import com.mojang.brigadier.ParseResults;
import com.ptsmods.morecommands.mixin.compat.compat191.plus.MixinLocalPlayerAccessor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.network.chat.ChatMessageContent;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.MessageSigner;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;

public class ClientCompat191 extends ClientCompat190 {

    @Override
    public Packet<ServerGamePacketListener> newChatMessagePacket(LocalPlayer player, String message, boolean forceChat) {
        MixinLocalPlayerAccessor accessor = (MixinLocalPlayerAccessor) player;
        ChatMessageContent content = accessor.callBuildSignedContent(message, null);
        MessageSigner signer = MessageSigner.create(player.getUUID());
        LastSeenMessages.Update update = player.connection.generateMessageAcknowledgements();

        if (!message.startsWith("/")) {
            MessageSignature signature = accessor.callSignMessage(signer, content, update.lastSeen());
            return new ServerboundChatPacket(content.plain(), signer.timeStamp(), signer.salt(), signature, content.isDecorated(), update);
        }

        message = message.substring(1);
        ParseResults<SharedSuggestionProvider> parseResults = player.connection.getCommands().parse(message, player.connection.getSuggestionsProvider());
        ArgumentSignatures argumentSignatures = accessor.callSignCommandArguments(signer, parseResults, null, update.lastSeen());
        return new ServerboundChatCommandPacket(message, signer.timeStamp(), signer.salt(), argumentSignatures, false, update);
    }
}
