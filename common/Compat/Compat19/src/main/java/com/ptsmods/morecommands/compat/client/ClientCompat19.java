package com.ptsmods.morecommands.compat.client;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientChatEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.phys.BlockHitResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Function;

public class ClientCompat19 extends ClientCompat17 {

    @Override
    public ChatVisiblity getChatVisibility(Options options) {
        return options.chatVisibility().get();
    }

    @Override
    public double getChatLineSpacing(Options options) {
        return options.chatLineSpacing().get();
    }

    @Override
    public InteractionResult interactBlock(MultiPlayerGameMode interactionManager, LocalPlayer player, ClientLevel world, InteractionHand hand, BlockHitResult hit) {
        return interactionManager.useItemOn(player, hand, hit);
    }

    @Override
    public InputStream getResourceStream(ResourceManager manager, ResourceLocation id) throws IOException {
        Resource res = manager.getResource(id).orElse(null);
        return res == null ? null : res.open();
    }

    @Override
    public double getGamma(Options options) {
        return options.gamma().get();
    }

    @Override
    public void registerChatProcessListener(Function<String, String> listener) {
        ClientChatEvent.PROCESS.register(processor -> {
            String output = listener.apply(processor.getMessage());

            if (output == null || output.equals(processor.getMessage())) return EventResult.pass();
            processor.setMessage(output);
            return EventResult.interruptTrue();
        });
    }

    @Override
    public void sendMessageOrCommand(String msg) {
        LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);
        if (msg.startsWith("/")) player.command(msg.substring(1));
        else player.chat(msg);
    }
}
