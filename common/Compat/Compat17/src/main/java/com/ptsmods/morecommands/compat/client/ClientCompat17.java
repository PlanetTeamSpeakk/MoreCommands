package com.ptsmods.morecommands.compat.client;

import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import com.ptsmods.morecommands.miscellaneous.CopySoundOld;
import com.ptsmods.morecommands.miscellaneous.EESoundOld;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.client.ClientChatEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.phys.BlockHitResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Function;

public class ClientCompat17 implements ClientCompat {
    @Override
    public ChatVisiblity getChatVisibility(Options options) {
        return options.chatVisibility;
    }

    @Override
    public double getChatLineSpacing(Options options) {
        return options.chatLineSpacing;
    }

    @Override
    public InteractionResult interactBlock(MultiPlayerGameMode interactionManager, LocalPlayer player, ClientLevel world, InteractionHand hand, BlockHitResult hit) {
        return interactionManager.useItemOn(player, world, hand, hit);
    }

    @Override
    public InputStream getResourceStream(ResourceManager manager, ResourceLocation id) throws IOException {
        return manager.getResource(id).getInputStream();
    }

    @Override
    public double getGamma(Options options) {
        return options.gamma;
    }

    @Override
    public Packet<ServerGamePacketListener> newChatMessagePacket(LocalPlayer player, String message, boolean forceChat) {
        return new ServerboundChatPacket(message);
    }

    @Override
    public void registerChatProcessListener(Function<String, String> listener) {
        ClientChatEvent.PROCESS.register(message -> {
            String output = listener.apply(message);

            return output == null || output.equals(message) ? CompoundEventResult.pass() : CompoundEventResult.interruptTrue(output);
        });
    }

    @Override
    public void sendMessageOrCommand(String msg) {
        Objects.requireNonNull(Minecraft.getInstance().player).chat(msg);
    }

    @Override
    public AbstractTickableSoundInstance newCopySound() {
        return new CopySoundOld();
    }

    @Override
    public AbstractTickableSoundInstance newEESound() {
        return new EESoundOld();
    }
}
