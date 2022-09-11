package com.ptsmods.morecommands.api.util.compat.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.ptsmods.morecommands.api.Holder;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.phys.BlockHitResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

public interface ClientCompat {

    @SuppressWarnings("deprecation") // Not API
    static ClientCompat get() {
        return Holder.getClientCompat();
    }

    void bufferBuilderBegin(BufferBuilder builder, int drawMode, VertexFormat format);

    int getFrameCount(TextureAtlasSprite sprite);

    void bindTexture(ResourceLocation id);

    ChatVisiblity getChatVisibility(Options options);

    double getChatLineSpacing(Options options);

    InteractionResult interactBlock(MultiPlayerGameMode interactionManager, LocalPlayer player, ClientLevel world, InteractionHand hand, BlockHitResult hit);

    InputStream getResourceStream(ResourceManager manager, ResourceLocation id) throws IOException;

    double getGamma(Options options);

    Packet<ServerGamePacketListener> newChatMessagePacket(LocalPlayer player, String message, boolean forceChat);

    void registerChatProcessListener(Function<String, String> listener);

    void sendMessageOrCommand(String msg);

    AbstractTickableSoundInstance newCopySound();

    AbstractTickableSoundInstance newEESound();
}
