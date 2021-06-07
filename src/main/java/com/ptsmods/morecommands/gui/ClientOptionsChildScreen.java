package com.ptsmods.morecommands.gui;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.clientoption.ClientOption;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.compat.Compat;
import com.ptsmods.morecommands.compat.client.ClientCompat;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;

import java.lang.reflect.Field;
import java.util.*;

import static com.ptsmods.morecommands.MoreCommands.log;

public class ClientOptionsChildScreen extends Screen {
	private final Class<?> c;
	private final Map<ClickableWidget, Field> btnFields = new HashMap<>();
	private final ClientOptionsScreen parent;
	private final List<List<Pair<ClickableWidget, Field>>> pages = new ArrayList<>();
	private ButtonWidget seekLeft = null, seekRight = null;
	private int page = 0;

	ClientOptionsChildScreen(ClientOptionsScreen parent, Class<?> c) {
		super(new LiteralText("MoreCommands").setStyle(MoreCommands.DS).append(new LiteralText(" client options").setStyle(MoreCommands.SS)).append(new LiteralText(" " + getCleanName(c.getSimpleName()).trim()).setStyle(Style.EMPTY.withFormatting(Formatting.WHITE))));
		this.c = c;
		this.parent = parent;
	}

	protected void init() {
		btnFields.clear();
		ClientCompat.getCompat().clearScreen(this);
		pages.clear();
		boolean right = false;
		int row = 0;
		ClientCompat compat = ClientCompat.getCompat();
		List<Pair<ClickableWidget, Field>> page = new ArrayList<>();
		for (Field f : c.getFields()) {
			ClientOption<?> option = getOption(f);
			if (option == null) continue; // Most likely the ordinal field
			if (page.size() == 10) {
				pages.add(page);
				page = new ArrayList<>();
				right = false;
				row = 0;
			}
			int x = width / 2 + (right ? 5 : -155);
			int y = height / 6 + 24*(row+1) - 6;
			ClickableWidget btn = compat.addButton(this, (ClickableWidget) option.createButton(x, y, getCleanName(f), () -> {
				parent.init();
				init();
			}));
			if (btn != null) {
				page.add(new Pair<>(btn, f));
				btnFields.put(btn, f);
			}
			if (right) row++;
			right = !right;
		}
		if (!page.isEmpty()) pages.add(page);
		if (pages.size() > 1) {
			seekLeft = compat.addButton(this, new ButtonWidget(width / 2 - 150, height / 6 + 145, 120, 20, new LiteralText("<---"), button -> {
				this.page -= 1;
				updatePage();
			}) {
				@Override
				protected MutableText getNarrationMessage() {
					return new TranslatableText("gui.narrate.button", new LiteralText("previous page"));
				}
			});
			seekRight = compat.addButton(this, new ButtonWidget(width / 2 + 30, height / 6 + 145, 120, 20, new LiteralText("--->"), button -> {
				this.page += 1;
				updatePage();
			}) {
				@Override
				protected MutableText getNarrationMessage() {
					return new TranslatableText("gui.narrate.button", new LiteralText("next page"));
				}
			});
		}
		updatePage();
		compat.addButton(this, new ButtonWidget(width / 2 - 150, height / 6 + 168, 120, 20, new LiteralText("Reset"), button -> {
			ClientOptions.reset();
			init();
		}));
		compat.addButton(this, new ButtonWidget(width / 2 + 30, height / 6 + 168, 120, 20, ScreenTexts.DONE, button -> Objects.requireNonNull(client).openScreen(this.parent)));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);
		drawCenteredText(matrices, Objects.requireNonNull(client).textRenderer, getTitle(), width / 2, 10, 0);
		super.render(matrices, mouseX, mouseY, delta);
		btnFields.forEach((btn, field) -> {
			List<String> comments;
			if (btn.isMouseOver(mouseX, mouseY) && (comments = getComments(field)) != null) {
				List<Text> texts = new ArrayList<>();
				for (String s : comments)
					texts.add(new LiteralText(s));
				renderTooltip(matrices, texts, mouseX, mouseY);
			}
		});
	}

	@Override
	public void onClose() {
		Objects.requireNonNull(client).openScreen(parent);
	}

	private void updatePage() {
		if (pages.size() > 1) {
			seekLeft.active = page > 0;
			seekRight.active = page < pages.size() - 1;
			for (ClickableWidget btn : btnFields.keySet())
				btn.visible = false;
			for (Pair<ClickableWidget, Field> pair : pages.get(page))
				pair.getLeft().visible = true;
		}
	}

	private String getCleanName(Field f) {
		return getCleanName(f.getName());
	}

	static String getCleanName(String name) {
		StringBuilder s = new StringBuilder();
		boolean digit = false;
		int lastCh = -1;
		for (int ch : name.chars().toArray()) {
			if (Character.isUpperCase(ch) && !Character.isUpperCase(lastCh)) s.append(' ');
			if (Character.isDigit(ch) && !digit) {
				s.append(' ');
				digit = true;
			} else if (!Character.isDigit(ch) && digit) digit = false;
			s.append((char) ch);
			lastCh = ch;
		}
		s.insert(0, Character.toUpperCase(s.charAt(0)));
		s.deleteCharAt(1);
		return s.toString();
	}

	private List<String> getComments(Field f) {
		return Optional.ofNullable(getOption(f)).map(ClientOption::getComments).orElse(null);
	}

	private ClientOption<?> getOption(Field f) {
		try {
			return ClientOption.class.isAssignableFrom(f.getType()) ? (ClientOption<?>) f.get(null) : null;
		} catch (IllegalAccessException e) {
			log.error("An unknown error occurred while getting type of field " + f + ".", e);
			return null;
		}
	}
}
