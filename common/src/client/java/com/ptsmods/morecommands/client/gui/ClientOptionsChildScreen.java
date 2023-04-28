package com.ptsmods.morecommands.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.addons.ScalableClickableWidget;
import com.ptsmods.morecommands.api.addons.ScreenAddon;
import com.ptsmods.morecommands.api.clientoptions.ClientOption;
import com.ptsmods.morecommands.api.clientoptions.ClientOptionCategory;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.api.util.text.TranslatableTextBuilder;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Tuple;

import java.util.*;

public class ClientOptionsChildScreen extends Screen {
    private final ClientOptionCategory category;
    private final Map<AbstractWidget, ClientOption<?>> btnFields = new HashMap<>();
    private final ClientOptionsScreen parent;
    private final List<List<Tuple<AbstractWidget, ClientOption<?>>>> pages = new ArrayList<>();
    private Button seekLeft = null, seekRight = null;
    private int page = 0;

    ClientOptionsChildScreen(ClientOptionsScreen parent, ClientOptionCategory category) {
        super(LiteralTextBuilder.builder("")
                .append(LiteralTextBuilder.builder("MoreCommands").withStyle(MoreCommands.DS))
                .append(LiteralTextBuilder.builder(" client options").withStyle(MoreCommands.SS))
                .append(LiteralTextBuilder.builder(" " + category.getName()).withStyle(Style.EMPTY.applyFormat(ChatFormatting.WHITE)))
                .build());
        this.category = category;
        this.parent = parent;
    }

    protected void init() {
        btnFields.clear();
        ((ScreenAddon) this).mc$clear();
        pages.clear();
        boolean right = false;
        int row = 0;
        ScreenAddon addon = (ScreenAddon) this;
        List<Tuple<AbstractWidget, ClientOption<?>>> page = new ArrayList<>();

        for (Map.Entry<String, ClientOption<?>> option : ClientOption.getOptions().get(category).entrySet()) {
            if (page.size() == 10) {
                pages.add(page);
                page = new ArrayList<>();
                right = false;
                row = 0;
            }

            int x = width / 2 + (right ? 5 : -155);
            int y = height / 6 + 24*(row+1) - 6;
            AbstractWidget btn = addon.mc$addButton((AbstractWidget) option.getValue().createButton(this, x, y, option.getKey(), () -> {
                parent.init();
                init();
            }, ClientOptions::write));

            if (btn != null) {
                ((ScalableClickableWidget) btn).setAutoScale(true);
                page.add(new Tuple<>(btn, option.getValue()));
                btnFields.put(btn, option.getValue());
            }

            if (right) row++;
            right = !right;
        }

        if (!page.isEmpty()) pages.add(page);
        if (pages.size() > 1) {
            seekLeft = addon.mc$addButton(ClientCompat.get().newButton(this, width / 2 - 150, height / 6 + 145, 120, 20,
                    LiteralTextBuilder.literal("<---"), button -> {
                        this.page -= 1;
                        updatePage();
                    }, null, TranslatableTextBuilder.builder("gui.narrate.button", LiteralTextBuilder.builder("previous page")).build()));

            seekRight = addon.mc$addButton(ClientCompat.get().newButton(this, width / 2 + 30, height / 6 + 145, 120, 20,
                    LiteralTextBuilder.literal("--->"), button -> {
                        this.page += 1;
                        updatePage();
                    }, null, TranslatableTextBuilder.builder("gui.narrate.button", LiteralTextBuilder.builder("next page")).build()));
        }
        updatePage();

        addon.mc$addButton(ClientCompat.get().newButton(this, width / 2 - 150, height / 6 + 168, 120, 20,
                LiteralTextBuilder.literal("Reset"), button -> {
                    ClientOptions.reset();
                    init();
                }, null));
        addon.mc$addButton(ClientCompat.get().newButton(this, width / 2 + 30, height / 6 + 168, 120, 20,
                CommonComponents.GUI_DONE, button -> Objects.requireNonNull(minecraft).setScreen(this.parent), null));
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        drawCenteredString(matrices, Objects.requireNonNull(minecraft).font, getTitle(), width / 2, 10, 0);
        super.render(matrices, mouseX, mouseY, delta);
        btnFields.forEach((btn, option) -> {
            List<String> comments = option.getComments();

            if (option.isDisabled()) {
                if (comments == null) comments = new ArrayList<>();
                else (comments = new ArrayList<>(comments)).add("");

                comments.add(ChatFormatting.RED + "This option has been disabled on this server!");
            }

            if (comments != null && btn.isMouseOver(mouseX, mouseY)) {
                List<Component> texts = new ArrayList<>();
                for (String s : comments)
                    texts.add(LiteralTextBuilder.literal(s));
                renderComponentTooltip(matrices, texts, mouseX, mouseY);
            }
        });
    }

    @Override
    public void onClose() {
        Objects.requireNonNull(minecraft).setScreen(parent);
    }

    private void updatePage() {
        if (pages.size() > 1) {
            seekLeft.active = page > 0;
            seekRight.active = page < pages.size() - 1;
            for (AbstractWidget btn : btnFields.keySet())
                btn.visible = false;
            for (Tuple<AbstractWidget, ClientOption<?>> pair : pages.get(page))
                pair.getA().visible = true;
        }
    }
}
