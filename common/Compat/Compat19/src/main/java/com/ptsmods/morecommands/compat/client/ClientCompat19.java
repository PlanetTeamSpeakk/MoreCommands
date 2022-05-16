package com.ptsmods.morecommands.compat.client;

import com.mojang.brigadier.ParseResults;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.mixin.compat.compat19.plus.MixinClientPlayerEntityAccessor;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.client.ClientChatEvent;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandSource;
import net.minecraft.network.Packet;
import net.minecraft.network.encryption.ArgumentSignatures;
import net.minecraft.network.encryption.ChatMessageSigner;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

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

	@Override
	public Packet<ServerPlayPacketListener> newChatMessagePacket(ClientPlayerEntity player, String message, boolean forceChat) {
		ChatMessageSigner signer = ChatMessageSigner.create(player.getUuid());
		MixinClientPlayerEntityAccessor accessor = (MixinClientPlayerEntityAccessor) player;

		if (!message.startsWith("/")) return new ChatMessageC2SPacket(message, accessor.callSignChatMessage(signer, LiteralTextBuilder.builder(message).build()), false);
		else {
			message = message.substring(1);
			ParseResults<CommandSource> parseResults = player.networkHandler.getCommandDispatcher().parse(message, player.networkHandler.getCommandSource());
			ArgumentSignatures argumentSignatures = accessor.callSignArguments(signer, parseResults);
			return new CommandExecutionC2SPacket(message, signer.timeStamp(), argumentSignatures);
		}
	}

	@Override
	public void registerChatProcessListener(Function<Text, Text> listener) {
		ClientChatEvent.PROCESS.register((chatType, message, sender) -> {
			Text output = listener.apply(message);

			return output == null || output.equals(message) ? CompoundEventResult.pass() : CompoundEventResult.interruptTrue(output);
		});
	}
}