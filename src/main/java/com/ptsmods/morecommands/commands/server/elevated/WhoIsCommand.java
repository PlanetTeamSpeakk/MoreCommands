package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.mixin.compat.MixinEntityAccessor;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;

public class WhoIsCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literalReqOp("whois").then(argument("player", EntityArgumentType.player()).executes(ctx -> {
			ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
			sendMsg(ctx, "Info for player " + SF + MoreCommands.textToString(player.getDisplayName(), null, true));
            sendMsg(ctx, "UUID: " + SF + player.getUuidAsString());
			sendMsg(ctx, "World: " + SF + player.getWorld().getRegistryKey().getValue().toString());
			sendMsg(ctx, "Coords: " + SF + player.getBlockPos().getX() + DF + ", " + SF + player.getBlockPos().getY() + DF + ", " + SF + player.getBlockPos().getZ());
			sendMsg(ctx, "Rotation: " + SF + MathHelper.wrapDegrees(((MixinEntityAccessor) player).getYaw_()) + DF + ", " + SF + MathHelper.wrapDegrees(((MixinEntityAccessor) player).getPitch_()));
			sendMsg(ctx, "Health: " + formatFromFloat(player.getHealth(), player.getMaxHealth(), .5f, .8f, false));
			sendMsg(ctx, "Food: " + formatFromFloat(player.getHungerManager().getFoodLevel(), 20f, .5f, .8f, false));
			sendMsg(ctx, "Saturation: " + SF + player.getHungerManager().getSaturationLevel());
			sendMsg(ctx, "IP: " + SF + player.getIp());
			return 1;
		})));
	}
}
