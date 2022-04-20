package com.ptsmods.morecommands.api.compat.client;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;

import java.io.IOException;
import java.io.InputStream;

public interface ClientCompat {

    void bufferBuilderBegin(BufferBuilder builder, int drawMode, VertexFormat format);

    int getFrameCount(Sprite sprite);

    void bindTexture(Identifier id);

    ChatVisibility getChatVisibility(GameOptions options);

    double getChatLineSpacing(GameOptions options);

    ActionResult interactBlock(ClientPlayerInteractionManager interactionManager, ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hit);

    InputStream getResourceStream(ResourceManager manager, Identifier id) throws IOException;

    double getGamma(GameOptions options);
}
