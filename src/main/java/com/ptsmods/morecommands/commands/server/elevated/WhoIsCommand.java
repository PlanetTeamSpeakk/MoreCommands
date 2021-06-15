package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;

public class WhoIsCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("whois").requires(IS_OP).then(argument("player", EntityArgumentType.player()).executes(ctx -> {
			ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
			sendMsg(ctx, "Info for player " + SF + MoreCommands.textToString(player.getDisplayName(), null, true));
			sendMsg(ctx, "World: " + SF + player.getServerWorld().getRegistryKey().getValue().toString());
			sendMsg(ctx, "Coords: " + SF + player.getBlockPos().getX() + DF + ", " + SF + player.getBlockPos().getY() + DF + ", " + SF + player.getBlockPos().getZ());
			sendMsg(ctx, "Rotation: " + SF + MathHelper.wrapDegrees(Compat.getCompat().getEntityYaw(player)) + DF + ", " + SF + MathHelper.wrapDegrees(Compat.getCompat().getEntityPitch(player)));
			sendMsg(ctx, "Health: " + formatFromFloat(player.getHealth(), player.getMaxHealth(), .5f, .8f, true));
			sendMsg(ctx, "Food: " + formatFromFloat(player.getHungerManager().getFoodLevel(), 20f, .5f, .8f, true));
			sendMsg(ctx, "Saturation: " + SF + player.getHungerManager().getSaturationLevel());
			sendMsg(ctx, "IP: " + SF + player.getIp());
			return 1;
		})));
	}
}
