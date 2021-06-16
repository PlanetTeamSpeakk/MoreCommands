package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.option.GameOptions;

public class FullbrightCommand extends ClientCommand {
	@Override
	public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) {
		dispatcher.register(cLiteral("fullbright").executes(ctx -> {
			GameOptions options = MinecraftClient.getInstance().options;
			options.gamma = options.gamma == Short.MAX_VALUE ? 1 : Short.MAX_VALUE;
			options.write();
			sendMsg("You can now see " + (options.gamma == 1 ? "most things" : "everything") + "!");
			return 1;
		}));
	}
}
