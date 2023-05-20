package com.ptsmods.morecommands.compat.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ptsmods.morecommands.api.addons.ItemTabAddon;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import com.ptsmods.morecommands.miscellaneous.CopySoundOld;
import com.ptsmods.morecommands.miscellaneous.EESoundOld;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.client.ClientChatEvent;
import dev.architectury.registry.CreativeTabRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class ClientCompat17 implements ClientCompat {
    private static CreativeModeTab unobtainableItemsTab;

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
    public void registerChatProcessListener(Function<String, String> listener) {
        ClientChatEvent.PROCESS.register(message -> {
            String output = listener.apply(message);

            return output == null || output.equals(message) ? CompoundEventResult.pass() : CompoundEventResult.interruptTrue(output);
        });
    }

    @Override
    public void sendChatOrCmd(String msg, boolean forceChat) {
        Objects.requireNonNull(Minecraft.getInstance().player).connection.send(new ServerboundChatPacket(msg));
    }

    @Override
    public AbstractTickableSoundInstance newCopySound() {
        return new CopySoundOld();
    }

    @Override
    public AbstractTickableSoundInstance newEESound() {
        return new EESoundOld();
    }

    @Override
    public Button newButton(Screen screen, int x, int y, int width, int height, Component text, Consumer<Button> onPress, Component tooltip) {
        return new Button(x, y, width, height, text, onPress::accept,
                tooltip == null ? Button.NO_TOOLTIP : ((button, poseStack, tx, ty) -> screen.renderTooltip(poseStack, tooltip, tx, ty)));
    }

    @Override
    public Button newButton(Screen screen, int x, int y, int width, int height, Component text, Consumer<Button> onPress, Component tooltip, Component narration) {
        MutableComponent narrationComp = narration.copy();
        return new Button(x, y, width, height, text, onPress::accept,
                tooltip == null ? Button.NO_TOOLTIP : ((button, poseStack, tx, ty) -> screen.renderTooltip(poseStack, tooltip, tx, ty))) {
            @Override
            protected @NotNull MutableComponent createNarrationMessage() {
                return narrationComp;
            }
        };
    }

    @Override
    public void registerUnobtainableItemsTab() {
        if (unobtainableItemsTab != null) return;

        unobtainableItemsTab = CreativeTabRegistry.create(new ResourceLocation("morecommands:unobtainable_items"), () -> new ItemStack(
                Compat.get().<Item>getBuiltInRegistry("item").get(new ResourceLocation("morecommands:locked_chest"))));
    }

    @Override
    public void fillUnobtainableItemsTab() {
        if (unobtainableItemsTab == null) throw new IllegalStateException("Unobtainable Items tab not yet registered.");

        for (Item item : Compat.get().<Item>getBuiltInRegistry("item"))
            if (item.getItemCategory() == null) ((ItemTabAddon) item).setTab(unobtainableItemsTab);
    }

    @Override
    public void setFocused(EditBox editBox, boolean focused) {
        editBox.setFocus(focused);
    }

    @Override
    public VertexConsumer vertex(VertexConsumer vertex, PoseStack.Pose pose, float x, float y, float z) {
        return vertex.vertex(pose.pose(), x, y, z);
    }

    @Override
    public VertexConsumer normal(VertexConsumer vertex, PoseStack.Pose pose, float nx, float ny, float nz) {
        return vertex.normal(pose.normal(), nx, ny, nz);
    }
}
