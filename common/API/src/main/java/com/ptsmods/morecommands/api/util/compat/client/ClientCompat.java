package com.ptsmods.morecommands.api.util.compat.client;

import com.ptsmods.morecommands.api.Holder;
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
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

public interface ClientCompat {

    @SuppressWarnings("deprecation") // Not API
    static ClientCompat get() {
        return Holder.getClientCompat();
    }

    void bufferBuilderBegin(BufferBuilder builder, int drawMode, VertexFormat format);

    int getFrameCount(Sprite sprite);

    void bindTexture(Identifier id);

    ChatVisibility getChatVisibility(GameOptions options);

    double getChatLineSpacing(GameOptions options);

    ActionResult interactBlock(ClientPlayerInteractionManager interactionManager, ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hit);

    InputStream getResourceStream(ResourceManager manager, Identifier id) throws IOException;

    double getGamma(GameOptions options);

    Packet<ServerPlayPacketListener> newChatMessagePacket(ClientPlayerEntity player, String message, boolean forceChat);

    void registerChatProcessListener(Function<String, String> listener);

    void sendMessageOrCommand(String msg);
}
