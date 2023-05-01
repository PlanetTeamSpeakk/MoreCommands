package com.ptsmods.morecommands.api.util.compat.client;

import com.ptsmods.morecommands.api.Holder;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.phys.BlockHitResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ClientCompat {

    @SuppressWarnings("deprecation") // Not API
    static ClientCompat get() {
        return Holder.getClientCompat();
    }

    ChatVisiblity getChatVisibility(Options options);

    double getChatLineSpacing(Options options);

    InteractionResult interactBlock(MultiPlayerGameMode interactionManager, LocalPlayer player, ClientLevel world, InteractionHand hand, BlockHitResult hit);

    InputStream getResourceStream(ResourceManager manager, ResourceLocation id) throws IOException;

    double getGamma(Options options);

    void registerChatProcessListener(Function<String, String> listener);

    default void sendChatOrCmd(String msg) {
        sendChatOrCmd(msg, false);
    }

    void sendChatOrCmd(String msg, boolean forceChat);

    AbstractTickableSoundInstance newCopySound();

    AbstractTickableSoundInstance newEESound();

    Button newButton(Screen screen, int x, int y, int width, int height, Component text, Consumer<Button> onPress, Component tooltip);

    Button newButton(Screen screen, int x, int y, int width, int height, Component text, Consumer<Button> onPress, Component tooltip, Component narration);

    void registerUnobtainableItemsTab();

    void fillUnobtainableItemsTab();

    void setFocused(EditBox editBox, boolean focused);
}
