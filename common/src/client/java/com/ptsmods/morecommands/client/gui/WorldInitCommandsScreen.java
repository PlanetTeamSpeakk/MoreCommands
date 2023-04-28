package com.ptsmods.morecommands.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.addons.ScreenAddon;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.client.MoreCommandsClient;
import com.ptsmods.morecommands.client.mixin.accessor.MixinAbstractWidgetAccessor;
import com.ptsmods.morecommands.client.mixin.accessor.MixinCommandSuggestorAccessor;
import com.ptsmods.morecommands.client.mixin.accessor.MixinSuggestionWindowAccessor;
import lombok.NonNull;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class WorldInitCommandsScreen extends Screen {
    private final Screen parent;
    private final List<Pair<EditBox, CommandSuggestions>> fields = new ArrayList<>();

    public WorldInitCommandsScreen(Screen parent) {
        super(LiteralTextBuilder.builder("MoreCommands ")
                .withStyle(MoreCommands.DS)
                .append(LiteralTextBuilder.builder("client options ")
                        .withStyle(MoreCommands.SS))
                .append(LiteralTextBuilder.builder("World Init Commands")
                        .withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)))
                .build());
        this.parent = parent;
    }

    @Override
    protected void init() {
        fields.stream()
                .filter(pair -> pair.getRight() != null)
                .forEach(pair -> ((MixinCommandSuggestorAccessor) pair.getRight()).setSuggestions(null));
        fields.clear();
        ((ScreenAddon) this).mc$clear();
        MoreCommandsClient.getWorldInitCommands().forEach(this::addField);
        addField("");
        ((ScreenAddon) this).mc$addButton(ClientCompat.get().newButton(this, width / 4 - 30, height / 6 + 168,
                120, 20, LiteralTextBuilder.literal("Reset"), btn -> {
                    MoreCommandsClient.clearWorldInitCommands();
                    init();
                }, null));
        ((ScreenAddon) this).mc$addButton(ClientCompat.get().newButton(this, width / 2 + width / 4 - 90, height / 6 + 168,
                120, 20, CommonComponents.GUI_DONE, btn -> onClose(), null));
        setInitialFocus(fields.get(0).getLeft());
    }

    private void addField(String content) {
        AtomicReference<EditBox> atomicField = new AtomicReference<>();
        AtomicReference<Pair<EditBox, CommandSuggestions>> atomicPair = new AtomicReference<>();
        EditBox field = new EditBox(font, 25, fields.size() * 25 + 50, width - 50, 20, LiteralTextBuilder.literal("Insert command here")) {
            @Override
            public void insertText(@NonNull String string) {
                super.insertText(string);
                if (!StringUtils.isEmpty(getValue()) && fields.size() < 6 && fields.stream()
                        .noneMatch(pair0 -> StringUtils.isEmpty(pair0.getLeft().getValue()))) addField("");
            }

            @Override
            public void deleteChars(int characterOffset) {
                super.deleteChars(characterOffset);
                checkEmpty();
            }

            @Override
            public void deleteWords(int wordOffset) {
                super.deleteWords(wordOffset);
                checkEmpty();
            }

            private void checkEmpty() {
                if (StringUtils.isEmpty(getValue()) && fields.stream().filter(pair0 -> StringUtils.isEmpty(pair0.getLeft().getValue())).count() > 1) {
                    int index = fields.indexOf(atomicPair.get());
                    update();
                    init();
                    if (index < fields.size() && index != -1) {
                        fields.forEach(pair -> pair.getLeft().setFocus(false));
                        EditBox field0 = fields.get(index).getLeft();
                        field0.setFocus(true);
                        field0.moveCursorToEnd();
                        WorldInitCommandsScreen.this.setFocused(field0);
                    }
                }
            }
        };

        atomicField.set(field);
        field.setValue(content);
        CommandSuggestions suggestor = minecraft == null || minecraft.player == null ? null : new CommandSuggestions(minecraft, this, field, font, true, true, 1, 10, false, -805306368) {
            public void render(@NonNull PoseStack matrices, int mouseX, int mouseY) {
                CommandSuggestions thiz = this;
                MixinCommandSuggestorAccessor accessor = (MixinCommandSuggestorAccessor) thiz; // Can't cast directly.
                if (accessor.getSuggestions() != null) accessor.getSuggestions().render(matrices, mouseX, mouseY);
                else {
                    int i = 0;
                    for (Iterator<FormattedCharSequence> var5 = accessor.getCommandUsage().iterator(); var5.hasNext(); ++i) {
                        FormattedCharSequence orderedText = var5.next();

                        int j = ((MixinAbstractWidgetAccessor) field).getY_() + field.getHeight() + 12 * i + 1;
                        fill(matrices, accessor.getCommandUsagePosition() - 1, j, accessor.getCommandUsagePosition() + accessor.getCommandUsageWidth() + 1, j + 12, -805306368);
                        font.drawShadow(matrices, orderedText, accessor.getCommandUsagePosition(), (float)(j + 2), -1);
                    }
                }
            }

            public void showSuggestions(boolean narrateFirstSuggestion) {
                super.showSuggestions(narrateFirstSuggestion);
                CommandSuggestions thiz = this;
                SuggestionsList window = ((MixinCommandSuggestorAccessor) thiz).getSuggestions();
                if (window != null) {
                    MixinSuggestionWindowAccessor accessor = (MixinSuggestionWindowAccessor) window;
                    accessor.setRect(new Rect2i(accessor.getRect().getX(), ((MixinAbstractWidgetAccessor) field).getY_() + field.getHeight() + 1,
                            accessor.getRect().getWidth(), accessor.getRect().getHeight()));
                }
            }
        };

        if (suggestor != null) suggestor.updateCommandInfo();
        field.setResponder(s -> {
            if (suggestor != null) {
                suggestor.setAllowSuggestions(atomicField.get().isFocused());
                suggestor.updateCommandInfo();
            }
        });

        Pair<EditBox, CommandSuggestions> pair = Pair.of(field, suggestor);
        atomicPair.set(pair);
        ((ScreenAddon) this).mc$addButton(field);
        fields.add(pair);
    }

    @Override
    public void render(@NonNull PoseStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        drawCenteredString(matrices, font, title, width / 2, 10, 0);
        super.render(matrices, mouseX, mouseY, delta);
        fields.stream().filter(pair -> pair.getLeft().isFocused() && pair.getRight() != null).forEach(pair -> pair.getRight().render(matrices, mouseX, mouseY));
    }

    @Override
    public void resize(@NonNull Minecraft client, int width, int height) {
        super.resize(client, width, height);
        fields.stream().filter(pair -> pair.getRight() != null).forEach(pair -> pair.getRight().updateCommandInfo());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_TAB || keyCode == GLFW.GLFW_KEY_ENTER) {
            EditBox field = fields.get((fields.stream().filter(pair -> pair.getLeft().isFocused()).findFirst().map(fields::indexOf).orElse(-1) + 1) % fields.size()).getLeft();
            fields.forEach(pair -> pair.getLeft().setFocus(false));
            field.setFocus(true);
            setFocused(field);
            return true;
        } else if (fields.stream().filter(pair -> pair.getLeft().isFocused() && pair.getRight() != null).findFirst().map(pair -> pair.getRight().keyPressed(keyCode, scanCode, modifiers)).orElse(false)) return true;
        else if (fields.stream().filter(pair -> pair.getLeft().isFocused()).findFirst().map(pair -> pair.getLeft().keyPressed(keyCode, scanCode, modifiers)).orElse(false)) return true;
        else return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        update();
        Objects.requireNonNull(minecraft).setScreen(parent);
    }

    private void update() {
        MoreCommandsClient.setWorldInitCommands(fields.stream().map(pair -> pair.getLeft().getValue()).filter(s -> !StringUtils.isEmpty(s)).collect(Collectors.toList()));
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
        fields.forEach(pair -> pair.getLeft().setFocus(false));
        boolean b = fields.stream()
                .filter(pair -> pair.getLeft().isFocused() && pair.getRight() != null)
                .anyMatch(pair -> pair.getRight().mouseClicked(mouseX, mouseY, button)) || super.mouseClicked(mouseX, mouseY, button);
        fields.stream()
                .filter(pair -> pair.getLeft().isFocused())
                .findFirst()
                .ifPresent(pair -> setFocused(pair.getLeft()));
        return b;
    }
}
