package com.ptsmods.morecommands.compat.client;

import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.client.ClientChatEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Function;

public class ClientCompat16 implements ClientCompat {

    @Override
    public void bufferBuilderBegin(BufferBuilder builder, int drawMode, VertexFormat format) {
        builder.begin(drawMode, format);
    }

    @Override
    public int getFrameCount(Sprite sprite) {
        return sprite.getFrameCount();
    }

    @Override
    public void bindTexture(Identifier id) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(id);
    }

    @Override
    public ChatVisibility getChatVisibility(GameOptions options) {
        return options.chatVisibility;
    }

    @Override
    public double getChatLineSpacing(GameOptions options) {
        return options.chatLineSpacing;
    }

    @Override
    public ActionResult interactBlock(ClientPlayerInteractionManager interactionManager, ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hit) {
        return interactionManager.interactBlock(player, world, hand, hit);
    }

    @Override
    public InputStream getResourceStream(ResourceManager manager, Identifier id) throws IOException {
        return manager.getResource(id).getInputStream();
    }

    @Override
    public double getGamma(GameOptions options) {
        return options.gamma;
    }

    @Override
    public Packet<ServerPlayPacketListener> newChatMessagePacket(ClientPlayerEntity player, String message, boolean forceChat) {
        return new ChatMessageC2SPacket(message);
    }

    @Override
    public void registerChatProcessListener(Function<String, String> listener) {
        ClientChatEvent.PROCESS.register(message -> {
            String output = listener.apply(message);

            return output == null || output.equals(message) ? CompoundEventResult.pass() : CompoundEventResult.interruptTrue(output);
        });
    }

    @Override
    public void sendMessageOrCommand(String msg) {
        Objects.requireNonNull(MinecraftClient.getInstance().player).sendChatMessage(msg);
    }
}
