package com.ptsmods.morecommands.api;

import com.ptsmods.morecommands.api.util.text.TranslatableTextBuilder;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;

public class ClientOnlyDummyImpl implements ClientOnly {
    @Override
    public void translateTranslatableText(TranslatableTextBuilder builder, Style style, boolean includeFormattings, StringBuilder sb) {}

    @Override
    public float getFrameTime() {
        return 1;
    }

    @Override
    public boolean isSingleplayer() {
        return false;
    }

    @Override
    public boolean isRemotePlayer(Player player) {
        return false;
    }

    @Override
    public PTClient getPTClient() {
        return null;
    }
}
