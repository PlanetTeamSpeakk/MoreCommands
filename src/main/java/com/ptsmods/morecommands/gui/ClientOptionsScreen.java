package com.ptsmods.morecommands.gui;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.clientoption.BooleanClientOption;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.compat.client.ClientCompat;
import com.ptsmods.morecommands.util.ReflectionHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.*;

public class ClientOptionsScreen extends Screen {
	private final Screen parent;
	private final Map<ClickableWidget, Class<?>> btnClasses = new HashMap<>();

	public ClientOptionsScreen(Screen parent) {
		super(new LiteralText("MoreCommands").setStyle(MoreCommands.DS).append(new LiteralText(" client options").setStyle(MoreCommands.SS)));
		this.parent = parent;
	}

	@Override
	protected void init() {
		Objects.requireNonNull(client);
		btnClasses.clear();
		ClientCompat.getCompat().clearScreen(this);
		boolean right = false;
		int row = 0;
		ClientCompat compat = ClientCompat.getCompat();
		List<Class<?>> classes = Lists.newArrayList(ClientOptions.class.getClasses());
		classes.sort(Comparator.comparingInt(clazz -> MoreObjects.firstNonNull(ReflectionHelper.getFieldValue(clazz, "ordinal", null), -1)));
		for (Class<?> c : classes)
			if (!c.isInterface() && !isHidden(c)) {
				int x = width / 2 + (right ? 5 : -155);
				int y = height / 6 + 24 * (row + 1) - 6;
				btnClasses.put(compat.addButton(this, new ButtonWidget(x, y, 150, 20, new LiteralText(ClientOptionsChildScreen.getCleanName(c.getSimpleName()).trim()), button -> client.setScreen(new ClientOptionsChildScreen(this, c)))), c);
				if (right) row++;
				right = !right;
			}
		btnClasses.put(compat.addButton(this, new ButtonWidget(width / 2 + (right ? 5 : -155), height / 6 + 24 * (row + 1) - 6, 150, 20, new LiteralText("World Init Commands"), button -> client.setScreen(new WorldInitCommandsScreen(this)))), WorldInitCommandsScreen.class);
		compat.addButton(this, new ButtonWidget(width / 2 - 150, height / 6 + 168, 120, 20, new LiteralText("Reset"), btn -> {
			ClientOptions.reset();
			init();
		}));
		compat.addButton(this, new ButtonWidget(width / 2 + 30, height / 6 + 168, 120, 20, ScreenTexts.DONE, buttonWidget -> client.setScreen(parent)));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);
		drawCenteredText(matrices, textRenderer, getTitle(), width / 2, 10, 0);
		super.render(matrices, mouseX, mouseY, delta);
		btnClasses.forEach((btn, clazz) -> {
			if (btn.isMouseOver(mouseX, mouseY) && getComment(clazz) != null) {
				List<Text> texts = new ArrayList<>();
				for (String s : Objects.requireNonNull(getComment(clazz)))
					texts.add(new LiteralText(s));
				renderTooltip(matrices, texts, mouseX, mouseY);
			}
		});
	}

	private String[] getComment(Class<?> c) {
		return c.isAnnotationPresent(ClientOptions.Comment.class) ? c.getAnnotation(ClientOptions.Comment.class).value() : null;
	}

	private boolean isHidden(Class<?> c) {
		return c.isAnnotationPresent(ClientOptions.IsHidden.class) && !((BooleanClientOption) ClientOptions.getOption(c.getAnnotation(ClientOptions.IsHidden.class).value())).getValue();
	}

	@Override
	public void onClose() {
		Objects.requireNonNull(client).setScreen(parent);
	}
}
