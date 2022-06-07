package com.ptsmods.morecommands.gui;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.addons.ScalableClickableWidget;
import com.ptsmods.morecommands.api.addons.ScreenAddon;
import com.ptsmods.morecommands.api.clientoptions.ClientOption;
import com.ptsmods.morecommands.api.clientoptions.ClientOptionCategory;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.api.util.text.TranslatableTextBuilder;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;

import java.util.*;

public class ClientOptionsChildScreen extends Screen {
    private final ClientOptionCategory category;
    private final Map<ClickableWidget, ClientOption<?>> btnFields = new HashMap<>();
    private final ClientOptionsScreen parent;
    private final List<List<Pair<ClickableWidget, ClientOption<?>>>> pages = new ArrayList<>();
    private ButtonWidget seekLeft = null, seekRight = null;
    private int page = 0;

    ClientOptionsChildScreen(ClientOptionsScreen parent, ClientOptionCategory category) {
        super(LiteralTextBuilder.builder("")
                .append(LiteralTextBuilder.builder("MoreCommands").withStyle(MoreCommands.DS))
                .append(LiteralTextBuilder.builder(" client options").withStyle(MoreCommands.SS))
                .append(LiteralTextBuilder.builder(" " + category.getName()).withStyle(Style.EMPTY.withFormatting(Formatting.WHITE)))
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
        List<Pair<ClickableWidget, ClientOption<?>>> page = new ArrayList<>();

        for (Map.Entry<String, ClientOption<?>> option : ClientOption.getOptions().get(category).entrySet()) {
            if (page.size() == 10) {
                pages.add(page);
                page = new ArrayList<>();
                right = false;
                row = 0;
            }

            int x = width / 2 + (right ? 5 : -155);
            int y = height / 6 + 24*(row+1) - 6;
            ClickableWidget btn = addon.mc$addButton((ClickableWidget) option.getValue().createButton(x, y, option.getKey(), () -> {
                parent.init();
                init();
            }, ClientOptions::write));

            if (btn != null) {
                ((ScalableClickableWidget) btn).setAutoScale(true);
                page.add(new Pair<>(btn, option.getValue()));
                btnFields.put(btn, option.getValue());
            }

            if (right) row++;
            right = !right;
        }

        if (!page.isEmpty()) pages.add(page);
        if (pages.size() > 1) {
            seekLeft = addon.mc$addButton(new ButtonWidget(width / 2 - 150, height / 6 + 145, 120, 20, LiteralTextBuilder.builder("<---").build(), button -> {
                this.page -= 1;
                updatePage();
            }) {
                @Override
                protected MutableText getNarrationMessage() {
                    return TranslatableTextBuilder.builder("gui.narrate.button", LiteralTextBuilder.builder("previous page")).build();
                }
            });
            seekRight = addon.mc$addButton(new ButtonWidget(width / 2 + 30, height / 6 + 145, 120, 20, LiteralTextBuilder.builder("--->").build(), button -> {
                this.page += 1;
                updatePage();
            }) {
                @Override
                protected MutableText getNarrationMessage() {
                    return TranslatableTextBuilder.builder("gui.narrate.button", LiteralTextBuilder.builder("next page")).build();
                }
            });
        }
        updatePage();

        addon.mc$addButton(new ButtonWidget(width / 2 - 150, height / 6 + 168, 120, 20, LiteralTextBuilder.builder("Reset").build(), button -> {
            ClientOptions.reset();
            init();
        }));
        addon.mc$addButton(new ButtonWidget(width / 2 + 30, height / 6 + 168, 120, 20, ScreenTexts.DONE, button -> Objects.requireNonNull(client).setScreen(this.parent)));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        drawCenteredText(matrices, Objects.requireNonNull(client).textRenderer, getTitle(), width / 2, 10, 0);
        super.render(matrices, mouseX, mouseY, delta);
        btnFields.forEach((btn, option) -> {
            List<String> comments = option.getComments();

            if (option.isDisabled()) {
                if (comments == null) comments = new ArrayList<>();
                else (comments = new ArrayList<>(comments)).add("");

                comments.add(Formatting.RED + "This option has been disabled on this server!");
            }

            if (comments != null && btn.isMouseOver(mouseX, mouseY)) {
                List<Text> texts = new ArrayList<>();
                for (String s : comments)
                    texts.add(LiteralTextBuilder.builder(s).build());
                renderTooltip(matrices, texts, mouseX, mouseY);
            }
        });
    }

    @Override
    public void close() {
        Objects.requireNonNull(client).setScreen(parent);
    }

    private void updatePage() {
        if (pages.size() > 1) {
            seekLeft.active = page > 0;
            seekRight.active = page < pages.size() - 1;
            for (ClickableWidget btn : btnFields.keySet())
                btn.visible = false;
            for (Pair<ClickableWidget, ClientOption<?>> pair : pages.get(page))
                pair.getLeft().visible = true;
        }
    }
}
