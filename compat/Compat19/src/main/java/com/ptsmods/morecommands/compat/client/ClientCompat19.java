package com.ptsmods.morecommands.compat.client;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;

import java.io.IOException;
import java.io.InputStream;

public class ClientCompat19 extends ClientCompat17 {

	@Override
	public ChatVisibility getChatVisibility(GameOptions options) {
		return options.getChatVisibility().getValue();
	}

	@Override
	public double getChatLineSpacing(GameOptions options) {
		return options.getChatLineSpacing().getValue();
	}

	@Override
	public ActionResult interactBlock(ClientPlayerInteractionManager interactionManager, ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hit) {
		return interactionManager.interactBlock(player, hand, hit);
	}

	@Override
	public InputStream getResourceStream(ResourceManager manager, Identifier id) throws IOException {
		Resource res = manager.getResource(id).orElse(null);
		return res == null ? null : res.getInputStream();
	}

	@Override
	public double getGamma(GameOptions options) {
		return options.getGamma().getValue();
	}
}
