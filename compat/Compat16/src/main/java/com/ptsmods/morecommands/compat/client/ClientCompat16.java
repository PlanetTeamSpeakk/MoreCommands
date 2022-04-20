package com.ptsmods.morecommands.compat.client;

import com.ptsmods.morecommands.api.compat.client.ClientCompat;
import net.minecraft.client.MinecraftClient;
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
}
