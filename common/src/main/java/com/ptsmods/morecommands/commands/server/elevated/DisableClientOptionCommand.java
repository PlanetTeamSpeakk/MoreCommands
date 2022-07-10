package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.clientoptions.BooleanClientOption;
import com.ptsmods.morecommands.api.clientoptions.ClientOption;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.api.util.extensions.ObjectExtensions;
import com.ptsmods.morecommands.miscellaneous.Command;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import lombok.experimental.ExtensionMethod;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ExtensionMethod(ObjectExtensions.class)
public class DisableClientOptionCommand extends Command {
    private final List<String> disabled = new ArrayList<>();

    @Override
    public void init(boolean serverOnly, MinecraftServer server) throws Exception {
        disabled.addAll(MoreCommands.readJson(MoreCommands.getRelativePath(server).resolve("disabledClientOptions.json").toFile()).or(new ArrayList<String>()));
        PlayerEvent.PLAYER_JOIN.register(player -> {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer()).writeVarInt(disabled.size());
            disabled.forEach(buf::writeUtf);
            NetworkManager.sendToPlayer(player, new ResourceLocation("morecommands:disable_client_options"), isOp(player) ? new FriendlyByteBuf(Unpooled.buffer()).writeVarInt(0) : buf);
        });
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) throws Exception {
        LiteralArgumentBuilder<CommandSourceStack> disableclientoption = literalReqOp("disableclientoption");

        ClientOption.getUnmappedOptions().values().stream()
                .filter(clientOption -> clientOption instanceof BooleanClientOption)
                .map(ClientOption::getKey)
                .forEach(option -> disableclientoption
                        .then(literal(option)
                                .executes(ctx -> {
                                    if (disabled.contains(option)) disabled.remove(option);
                                    else disabled.add(option);
                                    try {
                                        MoreCommands.saveJson(MoreCommands.getRelativePath().resolve("disabledClientOptions.json"), disabled);
                                    } catch (IOException e) {
                                        sendError(ctx, "The data file could not be saved.");
                                        log.error("Could not save the disabled client options data file.", e);
                                        return 0;
                                    }
                                    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer()).writeVarInt(disabled.size());
                                    disabled.forEach(buf::writeUtf);
                                    ctx.getSource().getServer().getPlayerList().getPlayers().forEach(player -> NetworkManager.sendToPlayer(player, new ResourceLocation("morecommands:disable_client_options"),
                                            isOp(player) ? new FriendlyByteBuf(Unpooled.buffer()).writeVarInt(0) : buf));
                                    sendMsg(ctx, "The clientoption " + SF + option + DF + " has been " + Util.formatFromBool(disabled.contains(option), ChatFormatting.RED + "disabled", ChatFormatting.GREEN + "enabled") +
                                            DF + " for all regular players.");
                                    return disabled.contains(option) ? 2 : 1;
                                })));

        dispatcher.register(disableclientoption
                .then(literal("list")
                        .executes(ctx -> {
                            sendMsg(ctx, "Currently disabled client options are: " + joinNicely(disabled) + DF + ".");
                            return disabled.size();
                        })));
    }

    @Override
    public boolean isDedicatedOnly() {
        return true;
    }

    @Override
    public String getDocsPath() {
        return "/elevated/disable-client-option";
    }
}
