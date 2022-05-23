package com.ptsmods.morecommands.api.callbacks;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientCommandSource;

@Environment(EnvType.CLIENT)
public interface ClientCommandRegistrationEvent {
    Event<ClientCommandRegistrationEvent> EVENT = EventFactory.createLoop();

    void register(CommandDispatcher<ClientCommandSource> dispatcher);
}
