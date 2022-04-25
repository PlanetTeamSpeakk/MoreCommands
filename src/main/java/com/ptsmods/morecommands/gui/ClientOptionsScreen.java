package com.ptsmods.morecommands.gui;

import com.google.common.collect.Lists;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.addons.ScreenAddon;
import com.ptsmods.morecommands.api.text.LiteralTextBuilder;
import com.ptsmods.morecommands.clientoption.ClientOptionCategory;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.*;

public class ClientOptionsScreen extends Screen {
	private final Screen parent;
	private final Map<ClickableWidget, ClientOptionCategory> categoryButtons = new HashMap<>();
	private ButtonWidget wicButton;

	public ClientOptionsScreen(Screen parent) {
		super(LiteralTextBuilder.builder("")
				.append(LiteralTextBuilder.builder("MoreCommands").withStyle(MoreCommands.DS))
				.append(LiteralTextBuilder.builder(" client options").withStyle(MoreCommands.SS))
				.build());
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
			if (category.getHidden() != null && category.getHidden().getValue()) continue;

			int x = width / 2 + (right ? 5 : -155);
			int y = height / 6 + 24 * (row + 1) - 6;

			categoryButtons.put(addon.mc$addButton(new ButtonWidget(x, y, 150, 20, LiteralTextBuilder.builder(category.getName()).build(),
					button -> client.setScreen(new ClientOptionsChildScreen(this, category)))), category);

			if (right) row++;
			right = !right;
		}

		wicButton = addon.mc$addButton(new ButtonWidget(width / 2 + (right ? 5 : -155), height / 6 + 24 * (row + 1) - 6, 150, 20, LiteralTextBuilder.builder("World Init Commands").build(),
				button -> client.setScreen(new WorldInitCommandsScreen(this))));

		addon.mc$addButton(new ButtonWidget(width / 2 - 150, height / 6 + 168, 120, 20, LiteralTextBuilder.builder("Reset").build(), btn -> {
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
					texts.add(LiteralTextBuilder.builder(s).build());
				renderTooltip(matrices, texts, mouseX, mouseY);
			}
		});

		if (wicButton.isMouseOver(mouseX, mouseY))
			renderTooltip(matrices, Lists.newArrayList(LiteralTextBuilder.builder("Commands that get ran upon creating a world.").build()), mouseX, mouseY);
	}

	@Override
	public void close() {
		Objects.requireNonNull(client).setScreen(parent);
	}
}
