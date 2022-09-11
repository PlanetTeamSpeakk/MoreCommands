package com.ptsmods.morecommands.compat;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.function.BiConsumer;

public class Compat192 extends Compat191 {

    @Override
    public void registerCommandRegistrationEventListener(BiConsumer<CommandDispatcher<CommandSourceStack>, Commands.CommandSelection> listener) {
        CommandRegistrationEvent.EVENT.register((dispatcher, registry, commandSelection) -> listener.accept(dispatcher, commandSelection));
    }
}
