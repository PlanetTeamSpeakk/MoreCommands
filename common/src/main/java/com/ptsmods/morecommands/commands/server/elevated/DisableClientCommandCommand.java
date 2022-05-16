package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.util.extensions.ObjectExtensions;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.miscellaneous.Command;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import lombok.experimental.ExtensionMethod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ExtensionMethod(ObjectExtensions.class)
public class DisableClientCommandCommand extends Command {
	private final List<String> disabled = new ArrayList<>();

	@Override
	public void init(boolean serverOnly, MinecraftServer server) throws Exception {
		disabled.addAll(MoreCommands.readJson(new File(MoreCommands.getRelativePath(server) + "disabledClientCommands.json")).or(new ArrayList<String>()));
		PlayerEvent.PLAYER_JOIN.register(player -> {
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer()).writeVarInt(disabled.size());
			disabled.forEach(buf::writeString);
			NetworkManager.sendToPlayer(player, new Identifier("morecommands:disable_client_commands"), isOp(player) ? new PacketByteBuf(Unpooled.buffer()).writeVarInt(0) : buf);
		});
	}

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) throws Exception {
		dispatcher.register(literalReqOp("disableclientcommand").then(argument("command", StringArgumentType.word()).executes(ctx -> {
			String command = ctx.getArgument("command", String.class);
			if (disabled.contains(command)) disabled.remove(command);
			else disabled.add(command);
			try {
				MoreCommands.saveJson(new File(MoreCommands.getRelativePath() + "disabledClientCommands.json"), disabled);
			} catch (IOException e) {
				sendError(ctx, "The data file could not be saved.");
				log.error("Could not save the disabled client commands data file.", e);
				return 0;
			}
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer()).writeVarInt(disabled.size());
			disabled.forEach(buf::writeString);
			ctx.getSource().getServer().getPlayerManager().getPlayerList().forEach(player -> NetworkManager.sendToPlayer(player, new Identifier("morecommands:disable_client_commands"),
					isOp(player) ? new PacketByteBuf(Unpooled.buffer()).writeVarInt(0) : buf));
			sendMsg(ctx, "The client command " + SF + command + DF + " has been " + Util.formatFromBool(disabled.contains(command), Formatting.RED + "disabled", Formatting.GREEN + "enabled") + DF + " for all regular players.");
			return disabled.contains(command) ? 2 : 1;
		})).then(literal("list").executes(ctx -> {
			sendMsg(ctx, "Currently disabled client options are: " + joinNicely(disabled) + DF + ".");
			return disabled.size();
		})));
	}
}
