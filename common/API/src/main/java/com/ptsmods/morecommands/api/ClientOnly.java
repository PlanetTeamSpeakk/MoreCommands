package com.ptsmods.morecommands.api;

import com.ptsmods.morecommands.api.util.text.TranslatableTextBuilder;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;

// Methods that are used on both the server and the client
// but only need an implementation on the client.
public interface ClientOnly {
    @SuppressWarnings("deprecation")
    static ClientOnly get() {
        return Holder.getClientOnly();
    }

    void translateTranslatableText(TranslatableTextBuilder builder, Style style, boolean includeFormattings, StringBuilder sb);

    float getFrameTime();

    boolean isSingleplayer();

    boolean isRemotePlayer(Player player);

    PTClient getPTClient();
}
