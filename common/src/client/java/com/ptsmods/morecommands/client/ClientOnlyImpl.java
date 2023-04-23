package com.ptsmods.morecommands.client;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.ClientOnly;
import com.ptsmods.morecommands.api.PTClient;
import com.ptsmods.morecommands.api.util.text.TextBuilder;
import com.ptsmods.morecommands.api.util.text.TranslatableTextBuilder;
import com.ptsmods.morecommands.client.util.PTClientImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ClientOnlyImpl implements ClientOnly {
    @Override
    public void translateTranslatableText(TranslatableTextBuilder builder, Style style, boolean includeFormattings, StringBuilder sb) {
        Object[] args = new Object[builder.getArgs().length];

        for (int i = 0; i < args.length; i++) {
            Object arg = builder.getArgs()[i];
            if (arg instanceof Component || arg instanceof TextBuilder)
                args[i] = MoreCommands.INSTANCE.textToString(arg instanceof Component ? (Component) arg : ((TextBuilder<?>) arg).build(), style, true, includeFormattings);
            else args[i] = arg;
        }

        sb.append(I18n.get(builder.getKey(), Arrays.stream(args)
                .map(o -> o instanceof TextBuilder ? ((TextBuilder<?>) o).build() : o)
                .collect(Collectors.toList())));
    }

    @Override
    public float getFrameTime() {
        return Minecraft.getInstance().getFrameTime();
    }

    @Override
    public boolean isSingleplayer() {
        return Minecraft.getInstance().getCurrentServer() == null && Minecraft.getInstance().level != null;
    }

    @Override
    public boolean isRemotePlayer(Player player) {
        return player instanceof RemotePlayer;
    }

    @Override
    public PTClient getPTClient() {
        return PTClientImpl.INSTANCE;
    }
}
