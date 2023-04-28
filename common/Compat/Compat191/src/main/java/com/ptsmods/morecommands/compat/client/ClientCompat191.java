package com.ptsmods.morecommands.compat.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import java.util.Objects;

public class ClientCompat191 extends ClientCompat190 {

    @Override
    public void sendChatOrCmd(String msg, boolean forceChat) {
        LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);
        if (!msg.startsWith("/") || forceChat) player.chatSigned(msg, null);
        else player.commandSigned(msg.substring(1), null);
    }
}
