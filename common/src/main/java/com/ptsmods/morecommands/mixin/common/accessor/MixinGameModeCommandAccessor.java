package com.ptsmods.morecommands.mixin.common.accessor;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.GameModeCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Collection;

@Mixin(GameModeCommand.class)
public interface MixinGameModeCommandAccessor {

    @Invoker
    static int callSetMode(CommandContext<CommandSourceStack> commandContext, Collection<ServerPlayer> collection, GameType gameType) {
        throw new AssertionError("This shouldn't happen.");
    }
}
