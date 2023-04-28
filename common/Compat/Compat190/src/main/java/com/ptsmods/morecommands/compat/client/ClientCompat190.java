package com.ptsmods.morecommands.compat.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import java.util.Objects;

public class ClientCompat190 extends ClientCompat19 {

    @Override
    public void sendChatOrCmd(String msg, boolean forceChat) {
        LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);
        if (!msg.startsWith("/") || forceChat) player.chat(msg);
        else player.command(msg.substring(1));
    }
}
