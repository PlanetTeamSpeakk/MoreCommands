package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import com.ptsmods.morecommands.arguments.TimeArgumentType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;

public class PtimeCommand extends ClientCommand {

	private static int time = -1;
	private static boolean fixed = false;
	private static int serverTime = -1;

	public static boolean isEnabled() {
		return time != -1;
	}

	public static void setServerTime(long time) {
		serverTime = (int) time;
	}

	public void preinit() {
		registerCallback(ClientTickEvents.START_WORLD_TICK, world -> {
			if (isEnabled()) {
				world.setTimeOfDay(fixed ? time : (time = (time+1) % 24000));
				serverTime++;
			}
		});
	}

	@Override
	public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) {
		dispatcher.register(cLiteral("ptime").then(cLiteral("off").executes(ctx -> {
			time = -1;
			fixed = false;
			MinecraftClient.getInstance().world.setTimeOfDay(serverTime);
			serverTime = -1;
			sendMsg("Time is now synchronised with the server time.");
			return 1;
		})).then(cArgument("time", TimeArgumentType.time()).executes(ctx -> {
			TimeArgumentType.WorldTime worldTime = TimeArgumentType.getTime(ctx, "time");
			if (time != -1) setServerTime(MinecraftClient.getInstance().world.getTimeOfDay());
			time = worldTime.getTime();
			fixed = worldTime.isFixed();
			sendMsg("Your personal time has been " + (fixed ? "fixed" : "set") + ".");
			return 1;
		})));
	}

}
