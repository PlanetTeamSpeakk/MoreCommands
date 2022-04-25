package com.ptsmods.morecommands.gui;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.api.addons.ScreenAddon;
import com.ptsmods.morecommands.api.text.LiteralTextBuilder;
import com.ptsmods.morecommands.mixin.client.accessor.MixinCommandSuggestorAccessor;
import com.ptsmods.morecommands.mixin.client.accessor.MixinSuggestionWindowAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class WorldInitCommandsScreen extends Screen {
	private final Screen parent;
	private final List<Pair<TextFieldWidget, CommandSuggestor>> fields = new ArrayList<>();

	public WorldInitCommandsScreen(Screen parent) {
		super(LiteralTextBuilder.builder("MoreCommands ")
				.withStyle(MoreCommands.DS)
				.append(LiteralTextBuilder.builder("client options ")
						.withStyle(MoreCommands.SS))
				.append(LiteralTextBuilder.builder("World Init Commands")
						.withStyle(Style.EMPTY.withColor(Formatting.WHITE)))
				.build());
		this.parent = parent;
	}

	@Override
	protected void init() {
		fields.stream().filter(pair -> pair.getRight() != null).forEach(pair -> Optional.ofNullable(((MixinCommandSuggestorAccessor) pair.getRight()).getWindow()).ifPresent(CommandSuggestor.SuggestionWindow::discard));
		fields.clear();
		((ScreenAddon) this).mc$clear();
		MoreCommandsClient.getWorldInitCommands().forEach(this::addField);
		addField("");
		((ScreenAddon) this).mc$addButton(new ButtonWidget(width / 4 - 30, height / 6 + 168, 120, 20, LiteralTextBuilder.builder("Reset").build(), (buttonWidget) -> {
			MoreCommandsClient.clearWorldInitCommands();
			init();
		}));
		((ScreenAddon) this).mc$addButton(new ButtonWidget(width / 2 + width / 4 - 90, height / 6 + 168, 120, 20, ScreenTexts.DONE, (buttonWidget) -> close()));
		setInitialFocus(fields.get(0).getLeft());
	}

	private void addField(String content) {
		AtomicReference<TextFieldWidget> atomicField = new AtomicReference<>();
		AtomicReference<Pair<TextFieldWidget, CommandSuggestor>> atomicPair = new AtomicReference<>();
		TextFieldWidget field = new TextFieldWidget(textRenderer, 25, fields.size() * 25 + 50, width - 50, 20, LiteralTextBuilder.builder("Insert command here").build()) {
			@Override
			public void write(String string) {
				super.write(string);
				if (!StringUtils.isEmpty(getText()) && fields.size() < 6 && fields.stream().noneMatch(pair0 -> StringUtils.isEmpty(pair0.getLeft().getText()))) addField("");
			}

			@Override
			public void eraseCharacters(int characterOffset) {
				super.eraseCharacters(characterOffset);
				checkEmpty();
			}

			@Override
			public void eraseWords(int wordOffset) {
				super.eraseWords(wordOffset);
				checkEmpty();
			}

			private void checkEmpty() {
				if (StringUtils.isEmpty(getText()) && fields.stream().filter(pair0 -> StringUtils.isEmpty(pair0.getLeft().getText())).count() > 1) {
					int index = fields.indexOf(atomicPair.get());
					update();
					init();
					if (index < fields.size() && index != -1) {
						fields.forEach(pair -> pair.getLeft().setTextFieldFocused(false));
						TextFieldWidget field0 = fields.get(index).getLeft();
						field0.setTextFieldFocused(true);
						field0.setCursorToEnd();
						WorldInitCommandsScreen.this.setFocused(field0);
					}
				}
			}
		};
		atomicField.set(field);
		field.setText(content);
		CommandSuggestor suggestor = client == null || client.player == null ? null : new CommandSuggestor(client, this, field, textRenderer, true, true, 1, 10, false, -805306368) {
			public void render(MatrixStack matrices, int mouseX, int mouseY) {
				CommandSuggestor thiz = this;
				MixinCommandSuggestorAccessor accessor = (MixinCommandSuggestorAccessor) thiz; // Can't cast directly.
				if (accessor.getWindow() != null) accessor.getWindow().render(matrices, mouseX, mouseY);
				else {
					int i = 0;
					for (Iterator<OrderedText> var5 = accessor.getMessages().iterator(); var5.hasNext(); ++i) {
						OrderedText orderedText = var5.next();
						int j = field.y + field.getHeight() + 12 * i + 1;
						DrawableHelper.fill(matrices, accessor.getX() - 1, j, accessor.getX() + accessor.getWidth() + 1, j + 12, -805306368);
						textRenderer.drawWithShadow(matrices, orderedText, accessor.getX(), (float)(j + 2), -1);
					}
				}
			}

			public void showSuggestions(boolean narrateFirstSuggestion) {
				super.showSuggestions(narrateFirstSuggestion);
				CommandSuggestor thiz = this;
				SuggestionWindow window = ((MixinCommandSuggestorAccessor) thiz).getWindow();
				if (window != null) {
					MixinSuggestionWindowAccessor accessor = (MixinSuggestionWindowAccessor) window;
					accessor.setArea(new Rect2i(accessor.getArea().getX(), field.y + field.getHeight() + 1, accessor.getArea().getWidth(), accessor.getArea().getHeight()));
				}
			}
		};
		if (suggestor != null) suggestor.refresh();
		field.setChangedListener(s -> {
			if (suggestor != null) {
				suggestor.setWindowActive(atomicField.get().isFocused());
				suggestor.refresh();
			}
		});
		Pair<TextFieldWidget, CommandSuggestor> pair = Pair.of(field, suggestor);
		atomicPair.set(pair);
		((ScreenAddon) this).mc$addButton(field);
		fields.add(pair);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);
		drawCenteredText(matrices, textRenderer, title, width / 2, 10, 0);
		super.render(matrices, mouseX, mouseY, delta);
		fields.stream().filter(pair -> pair.getLeft().isFocused() && pair.getRight() != null).forEach(pair -> pair.getRight().render(matrices, mouseX, mouseY));
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		super.resize(client, width, height);
		fields.stream().filter(pair -> pair.getRight() != null).forEach(pair -> pair.getRight().refresh());
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_TAB || keyCode == GLFW.GLFW_KEY_ENTER) {
			TextFieldWidget field = fields.get((fields.stream().filter(pair -> pair.getLeft().isFocused()).findFirst().map(fields::indexOf).orElse(-1) + 1) % fields.size()).getLeft();
			fields.forEach(pair -> pair.getLeft().setTextFieldFocused(false));
			field.setTextFieldFocused(true);
			setFocused(field);
			return true;
		} else if (fields.stream().filter(pair -> pair.getLeft().isFocused() && pair.getRight() != null).findFirst().map(pair -> pair.getRight().keyPressed(keyCode, scanCode, modifiers)).orElse(false)) return true;
		else if (fields.stream().filter(pair -> pair.getLeft().isFocused()).findFirst().map(pair -> pair.getLeft().keyPressed(keyCode, scanCode, modifiers)).orElse(false)) return true;
		else return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void close() {
		update();
		Objects.requireNonNull(client).setScreen(parent);
	}

	private void update() {
		MoreCommandsClient.setWorldInitCommands(fields.stream().map(pair -> pair.getLeft().getText()).filter(s -> !StringUtils.isEmpty(s)).collect(Collectors.toList()));
	}

	@Override
	public void tick() {
		fields.forEach(pair -> pair.getLeft().tick());
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		return fields.stream().filter(pair -> pair.getLeft().isFocused() && pair.getRight() != null).anyMatch(pair -> pair.getRight().mouseScrolled(amount));
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		fields.forEach(pair -> pair.getLeft().setTextFieldFocused(false));
		boolean b = fields.stream().filter(pair -> pair.getLeft().isFocused() && pair.getRight() != null).anyMatch(pair -> pair.getRight().mouseClicked(mouseX, mouseY, button)) || super.mouseClicked(mouseX, mouseY, button);
		fields.stream().filter(pair -> pair.getLeft().isFocused()).findFirst().ifPresent(pair -> setFocused(pair.getLeft()));
		return b;
	}
}
