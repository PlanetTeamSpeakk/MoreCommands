package com.ptsmods.morecommands.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.addons.ScreenAddon;
import com.ptsmods.morecommands.api.clientoptions.ClientOptionCategory;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.*;

public class ClientOptionsScreen extends Screen {
    private final Screen parent;
    private final Map<AbstractWidget, ClientOptionCategory> categoryButtons = new HashMap<>();
    private Button wicButton;

    public ClientOptionsScreen(Screen parent) {
        super(LiteralTextBuilder.builder("")
                .append(LiteralTextBuilder.builder("MoreCommands").withStyle(MoreCommands.DS))
                .append(LiteralTextBuilder.builder(" client options").withStyle(MoreCommands.SS))
                .build());
        this.parent = parent;
    }

    @Override
    protected void init() {
        Objects.requireNonNull(minecraft);
        categoryButtons.clear();
        ((ScreenAddon) this).mc$clear();

        boolean right = false;
        int row = 0;
        ScreenAddon addon = (ScreenAddon) this;

        for (ClientOptionCategory category : ClientOptionCategory.values()) {
            if (category.getHidden() != null && !category.getHidden().getValue()) continue;

            int x = width / 2 + (right ? 5 : -155);
            int y = height / 6 + 24 * (row + 1) - 6;

            categoryButtons.put(addon.mc$addButton(ClientCompat.get().newButton(this, x, y, 150, 20,
                    LiteralTextBuilder.literal(category.getName()),
                    button -> minecraft.setScreen(new ClientOptionsChildScreen(this, category)), null)), category);

            if (right) row++;
            right = !right;
        }

        wicButton = addon.mc$addButton(ClientCompat.get().newButton(this, width / 2 + (right ? 5 : -155),
                height / 6 + 24 * (row + 1) - 6, 150, 20, LiteralTextBuilder.literal("World Init Commands"),
                button -> minecraft.setScreen(new WorldInitCommandsScreen(this)), null));

        addon.mc$addButton(ClientCompat.get().newButton(this, width / 2 - 150, height / 6 + 168, 120, 20,
                LiteralTextBuilder.literal("Reset"), btn -> {
                    ClientOptions.reset();
                    init();
                }, null));
        addon.mc$addButton(ClientCompat.get().newButton(this, width / 2 + 30, height / 6 + 168, 120, 20,
                CommonComponents.GUI_DONE, buttonWidget -> minecraft.setScreen(parent), null));
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        drawCenteredString(matrices, font, getTitle(), width / 2, 10, 0);
        super.render(matrices, mouseX, mouseY, delta);

        categoryButtons.forEach((btn, category) -> {
            if (btn.isMouseOver(mouseX, mouseY) && category.getComments() != null) {
                List<Component> texts = new ArrayList<>();
                for (String s : category.getComments())
                    texts.add(LiteralTextBuilder.literal(s));
                renderComponentTooltip(matrices, texts, mouseX, mouseY);
            }
        });

        if (wicButton.isMouseOver(mouseX, mouseY))
            renderComponentTooltip(matrices, Lists.newArrayList(LiteralTextBuilder.literal("Commands that get ran upon creating a world.")), mouseX, mouseY);
    }

    @Override
    public void onClose() {
        Objects.requireNonNull(minecraft).setScreen(parent);
    }
}
