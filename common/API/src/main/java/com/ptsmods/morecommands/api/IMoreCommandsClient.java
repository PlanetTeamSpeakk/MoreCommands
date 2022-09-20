package com.ptsmods.morecommands.api;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

public interface IMoreCommandsClient {
    static IMoreCommandsClient get() {
        return Holder.getMoreCommandsClient();
    }

    static void handleCommand(String message, CallbackInfo cbi) {
        IMoreCommandsClient mcClient = IMoreCommandsClient.get();
        LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);
        StringReader reader = new StringReader(message.startsWith("/") ? message.substring(1) : message);

        if (mcClient.getClientCommandDispatcher().getRoot().getChild(reader.getString().split(" ")[0]) != null) {
            cbi.cancel();
            if (mcClient.isCommandDisabled(reader.getString())) {
                player.displayClientMessage(LiteralTextBuilder
                        .literal("That client command is disabled on this server.", Style.EMPTY.withColor(ChatFormatting.RED)), false);
                return;
            }

            try {
                mcClient.getClientCommandDispatcher().execute(reader, player.connection.getSuggestionsProvider());
            } catch (CommandSyntaxException e) {
                player.displayClientMessage(LiteralTextBuilder.literal(e.getMessage(), Style.EMPTY.withColor(ChatFormatting.RED)), false);
            } catch (Exception e) {
                player.displayClientMessage(LiteralTextBuilder.literal("Unknown or incomplete command, see below for error.", Style.EMPTY.withColor(ChatFormatting.RED)), false);
                IMoreCommands.LOG.catching(e);
            }
        }
    }

    CommandDispatcher<ClientSuggestionProvider> getClientCommandDispatcher();

    boolean isCommandDisabled(String command);

    List<KeyMapping> getKeyMappings();

    void setScheduleWorldInitCommands(boolean scheduleWorldInitCommands);
}
