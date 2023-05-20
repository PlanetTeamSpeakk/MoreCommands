package com.ptsmods.morecommands.compat.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ptsmods.morecommands.api.util.compat.Compat;
import dev.architectury.registry.CreativeTabRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Objects;
import java.util.function.Consumer;

public class ClientCompat193 extends ClientCompat192 {
    private static CreativeTabRegistry.TabSupplier unobtainableItemsTab;

    @Override
    public Button newButton(Screen screen, int x, int y, int width, int height, Component text, Consumer<Button> onPress, Component tooltip) {
        return newButton(screen, x, y, width, height, text, onPress, tooltip, null);
    }

    @Override
    public Button newButton(Screen screen, int x, int y, int width, int height, Component text, Consumer<Button> onPress, Component tooltip, Component narration) {
        MutableComponent narrationComp = narration == null ? text.copy() : narration.copy();
        return new Button.Builder(text, onPress::accept)
                .pos(x, y)
                .size(width, height)
                .tooltip(tooltip == null ? null : Tooltip.create(tooltip))
                .createNarration(comp -> narrationComp)
                .build();
    }

    @Override
    public void registerUnobtainableItemsTab() {
        if (unobtainableItemsTab != null) return;

        unobtainableItemsTab = CreativeTabRegistry.create(new ResourceLocation("morecommands:unobtainable_items"), builder -> builder
                .icon(() -> new ItemStack(Compat.get().<Item>getBuiltInRegistry("item")
                        .get(new ResourceLocation("morecommands:locked_chest"))))
                .displayItems(((flags, output, isOp) -> {
                    for (Item item : Compat.get().<Item>getBuiltInRegistry("item"))
                        if (item != Items.AIR && CreativeModeTabs.allTabs().stream().noneMatch(tab -> tab.contains(new ItemStack(item)))) output.accept(item);
                })));
    }

    @Override
    public void fillUnobtainableItemsTab() {} // Already handled in #registerUnobtainableItemsTab().

    @Override
    public void sendChatOrCmd(String msg, boolean forceChat) {
        LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);
        if (!msg.startsWith("/") || forceChat) player.connection.sendChat(msg);
        else player.connection.sendCommand(msg.substring(1));
    }

    @Override
    public VertexConsumer vertex(VertexConsumer vertex, PoseStack.Pose pose, float x, float y, float z) {
        // They now use JOML for their math instead of com.mojang.math.
        return vertex.vertex(pose.pose(), x, y, z);
    }

    @Override
    public VertexConsumer normal(VertexConsumer vertex, PoseStack.Pose pose, float nx, float ny, float nz) {
        return vertex.normal(pose.normal(), nx, ny, nz);
    }
}
