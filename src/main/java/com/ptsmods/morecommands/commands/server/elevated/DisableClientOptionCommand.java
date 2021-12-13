package com.ptsmods.morecommands.commands.server.elevated;

import com.google.common.base.MoreObjects;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.callbacks.PlayerConnectionCallback;
import com.ptsmods.morecommands.clientoption.BooleanClientOption;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.miscellaneous.Command;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DisableClientOptionCommand extends Command {
	private final List<String> disabled = new ArrayList<>();

	@Override
	public void init(boolean serverOnly, MinecraftServer server) throws Exception {
		disabled.addAll(MoreObjects.firstNonNull(MoreCommands.readJson(new File(MoreCommands.getRelativePath(server) + "disabledClientOptions.json")), new ArrayList<>()));
		registerCallback(PlayerConnectionCallback.JOIN, player -> {
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer()).writeVarInt(disabled.size());
			disabled.forEach(buf::writeString);
			ServerPlayNetworking.send(player, new Identifier("morecommands:disable_client_options"), isOp(player) ? new PacketByteBuf(Unpooled.buffer()).writeVarInt(0) : buf);
		});
	}

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) throws Exception {
		LiteralArgumentBuilder<ServerCommandSource> disableclientoption = literalReqOp("disableclientoption");

		ClientOptions.getMappedOptions().entrySet().stream()
				.filter(entry -> entry.getValue() instanceof BooleanClientOption)
				.map(Map.Entry::getKey)
				.forEach(option -> disableclientoption.then(literal(option).executes(ctx -> {
					if (disabled.contains(option)) disabled.remove(option);
					else disabled.add(option);
					try {
						MoreCommands.saveJson(new File(MoreCommands.getRelativePath() + "disabledClientOptions.json"), disabled);
					} catch (IOException e) {
						sendError(ctx, "The data file could not be saved.");
						log.error("Could not save the disabled client options data file.", e);
						return 0;
					}
					PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer()).writeVarInt(disabled.size());
					disabled.forEach(buf::writeString);
					ctx.getSource().getServer().getPlayerManager().getPlayerList().forEach(player -> ServerPlayNetworking.send(player, new Identifier("morecommands:disable_client_options"), isOp(player) ? new PacketByteBuf(Unpooled.buffer()).writeVarInt(0) : buf));
					sendMsg(ctx, "The clientoption " + SF + option + DF + " has been " + formatFromBool(disabled.contains(option), Formatting.RED + "disabled", Formatting.GREEN + "enabled") + DF + " for all regular players.");
					return disabled.contains(option) ? 2 : 1;
				})));

		dispatcher.register(disableclientoption.then(literal("list").executes(ctx -> {
			sendMsg(ctx, "Currently disabled client options are: " + joinNicely(disabled) + DF + ".");
			return disabled.size();
		})));
	}

	@Override
	public boolean forDedicated() {
		return true;
	}
}
