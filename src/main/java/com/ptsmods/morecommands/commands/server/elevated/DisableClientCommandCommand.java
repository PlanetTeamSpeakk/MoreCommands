package com.ptsmods.morecommands.commands.server.elevated;

import com.google.common.base.MoreObjects;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.callbacks.PlayerConnectionCallback;
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

public class DisableClientCommandCommand extends Command {
    private final List<String> disabled = new ArrayList<>();

    @Override
    public void init(MinecraftServer server) throws Exception {
        disabled.addAll(MoreObjects.firstNonNull(MoreCommands.readJson(new File(MoreCommands.getRelativePath(server) + "disabledClientCommands.json")), new ArrayList<>()));
        registerCallback(PlayerConnectionCallback.JOIN, player -> {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer()).writeVarInt(disabled.size());
            disabled.forEach(buf::writeString);
            ServerPlayNetworking.send(player, new Identifier("morecommands:disable_client_commands"), isOp(player) ? new PacketByteBuf(Unpooled.buffer()).writeVarInt(0) : buf);
        });
    }

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) throws Exception {
        dispatcher.register(literal("disableclientcommand").then(argument("command", StringArgumentType.word()).executes(ctx -> {
            String command = ctx.getArgument("command", String.class);
            if (disabled.contains(command)) disabled.remove(command);
            else disabled.add(command);
            try {
                MoreCommands.saveJson(new File(MoreCommands.getRelativePath() + "disabledClientCommands.json"), disabled);
            } catch (IOException e) {
                sendMsg(ctx, Formatting.RED + "The data file could not be saved.");
                log.error("Could not save the disabled client commands data file.", e);
                return 0;
            }
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer()).writeVarInt(disabled.size());
            disabled.forEach(buf::writeString);
            ctx.getSource().getMinecraftServer().getPlayerManager().getPlayerList().forEach(player -> ServerPlayNetworking.send(player, new Identifier("morecommands:disable_client_commands"), isOp(player) ? new PacketByteBuf(Unpooled.buffer()).writeVarInt(0) : buf));
            sendMsg(ctx, "The client command " + SF + command + DF + " has been " + formatFromBool(disabled.contains(command), Formatting.RED + "disabled", Formatting.GREEN + "enabled") + DF + " for all regular players.");
            return disabled.contains(command) ? 2 : 1;
        })).then(literal("list").executes(ctx -> {
            sendMsg(ctx, "Currently disabled client options are: " + joinNicely(disabled) + DF + ".");
            return disabled.size();
        })));
    }
}
