package com.ptsmods.morecommands.mixin.common;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.FillCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(FillCommand.class)
public class MixinFillCommand {

	/**
	 * @param dispatcher The dispatcher to register this command on.
	 * @author PlanetTeamSpeak
	 * @reason We create an entirely new version of this command in {@link com.ptsmods.morecommands.commands.server.elevated.FillCommand}.
	 */
	@Overwrite
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		// Have a look at my own version of the fill command in com.ptsmods.morecommands.commands.server.elevated.FillCommand
	}
}
