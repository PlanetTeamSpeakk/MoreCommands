package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

public class SendCommand extends ClientCommand {
	@Override
	public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) {
		dispatcher.register(cLiteral("send").then(cArgument("msg", StringArgumentType.greedyString()).executes(ctx -> {
			MinecraftClient.getInstance().player.networkHandler.sendPacket(new ChatMessageC2SPacket(ctx.getArgument("msg", String.class)));
			return 1;
		})));
	}
}
