package com.ptsmods.morecommands.compat.client;

import com.mojang.brigadier.ParseResults;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.mixin.compat.compat19.plus.MixinClientPlayerEntityAccessor;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientChatEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.network.chat.MessageSigner;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.phys.BlockHitResult;

public class ClientCompat19 extends ClientCompat17 {

    @Override
    public ChatVisiblity getChatVisibility(Options options) {
        return options.chatVisibility().get();
    }

    @Override
    public double getChatLineSpacing(Options options) {
        return options.chatLineSpacing().get();
    }

    @Override
    public InteractionResult interactBlock(MultiPlayerGameMode interactionManager, LocalPlayer player, ClientLevel world, InteractionHand hand, BlockHitResult hit) {
        return interactionManager.useItemOn(player, hand, hit);
    }

    @Override
    public InputStream getResourceStream(ResourceManager manager, ResourceLocation id) throws IOException {
        Resource res = manager.getResource(id).orElse(null);
        return res == null ? null : res.open();
    }

    @Override
    public double getGamma(Options options) {
        return options.gamma().get();
    }

    @Override
    public Packet<ServerGamePacketListener> newChatMessagePacket(LocalPlayer player, String message, boolean forceChat) {
        MessageSigner signer = MessageSigner.create(player.getUUID());
        MixinClientPlayerEntityAccessor accessor = (MixinClientPlayerEntityAccessor) player;

        if (!message.startsWith("/")) return new ServerboundChatPacket(message, accessor.callSignMessage(signer, LiteralTextBuilder.literal(message)), false);
        else {
            message = message.substring(1);
            ParseResults<SharedSuggestionProvider> parseResults = player.connection.getCommands().parse(message, player.connection.getSuggestionsProvider());
            ArgumentSignatures argumentSignatures = accessor.callSignCommandArguments(signer, parseResults, null);
            return new ServerboundChatCommandPacket(message, signer.timeStamp(), argumentSignatures, false);
        }
    }

    @Override
    public void registerChatProcessListener(Function<String, String> listener) {
        ClientChatEvent.PROCESS.register(processor -> {
            String output = listener.apply(processor.getMessage());

            if (output == null || output.equals(processor.getMessage())) return EventResult.pass();
            processor.setMessage(output);
            return EventResult.interruptTrue();
        });
    }

    @Override
    public void sendMessageOrCommand(String msg) {
        LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);
        if (msg.startsWith("/")) player.command(msg.substring(1));
        else player.chat(msg);
    }
}
