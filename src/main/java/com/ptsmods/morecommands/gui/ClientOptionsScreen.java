package com.ptsmods.morecommands.gui;

import com.google.common.collect.Lists;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.clientoption.ClientOptionCategory;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.mixin.addons.ScreenAddon;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.*;

public class ClientOptionsScreen extends Screen {
	private final Screen parent;
	private final Map<ClickableWidget, ClientOptionCategory> categoryButtons = new HashMap<>();
    private ButtonWidget wicButton;

	public ClientOptionsScreen(Screen parent) {
		super(new LiteralText("")
                .append(new LiteralText("MoreCommands").setStyle(MoreCommands.DS))
                .append(new LiteralText(" client options").setStyle(MoreCommands.SS)));
		this.parent = parent;
	}

	@Override
	protected void init() {
		Objects.requireNonNull(client);
		categoryButtons.clear();
        ((ScreenAddon) this).mc$clear();

		boolean right = false;
		int row = 0;
        ScreenAddon addon = (ScreenAddon) this;

		for (ClientOptionCategory category : ClientOptionCategory.values()) {
            if (category.getHidden().getValue()) continue;

            int x = width / 2 + (right ? 5 : -155);
            int y = height / 6 + 24 * (row + 1) - 6;

            categoryButtons.put(addon.mc$addButton(new ButtonWidget(x, y, 150, 20, new LiteralText(category.getName()),
                    button -> client.setScreen(new ClientOptionsChildScreen(this, category)))), category);

            if (right) row++;
            right = !right;
        }

        wicButton = addon.mc$addButton(new ButtonWidget(width / 2 + (right ? 5 : -155), height / 6 + 24 * (row + 1) - 6, 150, 20, new LiteralText("World Init Commands"),
                button -> client.setScreen(new WorldInitCommandsScreen(this))));

		addon.mc$addButton(new ButtonWidget(width / 2 - 150, height / 6 + 168, 120, 20, new LiteralText("Reset"), btn -> {
			ClientOptions.reset();
			init();
		}));
		addon.mc$addButton(new ButtonWidget(width / 2 + 30, height / 6 + 168, 120, 20, ScreenTexts.DONE, buttonWidget -> client.setScreen(parent)));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);
		drawCenteredText(matrices, textRenderer, getTitle(), width / 2, 10, 0);
		super.render(matrices, mouseX, mouseY, delta);

		categoryButtons.forEach((btn, category) -> {
			if (btn.isMouseOver(mouseX, mouseY) && category.getComments() != null) {
				List<Text> texts = new ArrayList<>();
				for (String s : category.getComments())
					texts.add(new LiteralText(s));
				renderTooltip(matrices, texts, mouseX, mouseY);
			}
		});

        if (wicButton.isMouseOver(mouseX, mouseY))
            renderTooltip(matrices, Lists.newArrayList(new LiteralText("Commands that get ran upon creating a world.")), mouseX, mouseY);
	}

	@Override
	public void close() {
		Objects.requireNonNull(client).setScreen(parent);
	}
}
