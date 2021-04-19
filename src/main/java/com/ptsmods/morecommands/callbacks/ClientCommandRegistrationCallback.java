package com.ptsmods.morecommands.callbacks;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.ClientCommandSource;

// Blatantly copied from CommandRegistrationCallback from the Fabric API.
@Environment(EnvType.CLIENT)
public interface ClientCommandRegistrationCallback {
	Event<ClientCommandRegistrationCallback> EVENT = EventFactory.createArrayBacked(ClientCommandRegistrationCallback.class, callbacks -> dispatcher -> {
		for (int i = 0; i < callbacks.length; i++)
			callbacks[i].register(dispatcher);
	});

	void register(CommandDispatcher<ClientCommandSource> var1);
}
