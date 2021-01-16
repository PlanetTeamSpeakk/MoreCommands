package com.ptsmods.morecommands.gui;

import com.google.common.base.MoreObjects;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.ClientOptions;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ptsmods.morecommands.MoreCommands.log;

public class ClientOptionsChildScreen extends Screen {

    private final Class<?> c;
    private final Map<AbstractButtonWidget, Field> btnFields = new HashMap<>();
    private final ClientOptionsScreen parent;
    private final List<List<Pair<AbstractButtonWidget, Field>>> pages = new ArrayList<>();
    private ButtonWidget seekLeft = null, seekRight = null;
    private int page = 0;

    ClientOptionsChildScreen(ClientOptionsScreen parent, Class<?> c) {
        super(new LiteralText("MoreCommands").setStyle(MoreCommands.DS).append(new LiteralText(" client options").setStyle(MoreCommands.SS)).append(new LiteralText(" " + getCleanName(c.getSimpleName()).trim()).setStyle(Style.EMPTY.withFormatting(Formatting.WHITE))));
        this.c = c;
        this.parent = parent;
    }

    protected void init() {
        btnFields.clear();
        buttons.clear();
        pages.clear();
        boolean right = false;
        int row = 0;
        List<Pair<AbstractButtonWidget, Field>> page = new ArrayList<>();
        for (Field f : c.getFields()) {
            if (Modifier.isFinal(f.getModifiers()) || isHidden(f)) continue;
            if (page.size() == 10) {
                pages.add(page);
                page = new ArrayList<>();
                right = false;
                row = 0;
            }
            int x = width / 2 + (right ? 5 : -155);
            int y = height / 6 + 24*(row+1) - 6;
            AbstractButtonWidget btn = null;
            if (f.getType() == boolean.class)
                btn = addButton(new ButtonWidget(x, y, 150, 20, getBoolText(f), button -> {
                    boolean oldValue = getBoolValue(f);
                    setValue(f, !getBoolValue(f));
                    checkChangeCallback(f, oldValue);
                    button.setMessage(getBoolText(f));
                    parent.init();
                    init();
                }));
            else if (f.getType() == int.class)
                btn = addButton(new SliderWidget(x, y, 150, 20, new LiteralText(getCleanName(f) + " : " + getIntValue(f)), getSliderValue(f)) {
                    @Override
                    protected void updateMessage() {
                        setMessage(new LiteralText(getCleanName(f) + " : " + getIntValue(f)));
                    }

                    @Override
                    protected void applyValue() {
                        int[] cramp = getCramp(f);
                        int oldValue = -1;
                        try {
                            oldValue = f.getInt(null);
                        } catch (IllegalAccessException e) {
                            log.catching(e);
                        }
                        setValue(f, (int) MathHelper.lerp(value, cramp[0], cramp[1]));
                        checkChangeCallback(f, oldValue);
                    }
                });
            if (btn != null) {
                page.add(new Pair<>(btn, f));
                btnFields.put(btn, f);
            }
            if (right) row++;
            right = !right;
        }
        if (!page.isEmpty()) pages.add(page);
        if (pages.size() > 1) {
            seekLeft = addButton(new ButtonWidget(width / 4 - 30, height / 6 + 145, 120, 20, new LiteralText("<---"), button -> {
                this.page -= 1;
                updatePage();
            }) {
                @Override
                protected MutableText getNarrationMessage() {
                    return new TranslatableText("gui.narrate.button", new LiteralText("previous page"));
                }
            });
            seekRight = addButton(new ButtonWidget(width / 2 + width / 4 - 90, height / 6 + 145, 120, 20, new LiteralText("--->"), button -> {
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
        addButton(new ButtonWidget(width / 4 - 30, height / 6 + 168, 120, 20, new LiteralText("Reset"), button -> {
            ClientOptions.reset();
            init();
        }));
        addButton(new ButtonWidget(width / 2 + width / 4 - 90, height / 6 + 168, 120, 20, ScreenTexts.DONE, button -> client.openScreen(this.parent)));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        drawCenteredText(matrices, client.textRenderer, getTitle(), width / 2, 10, 0);
        super.render(matrices, mouseX, mouseY, delta);
        btnFields.forEach((btn, field) -> {
            String[] comment;
            if (btn.isMouseOver(mouseX, mouseY) && (comment = getComment(field)) != null) {
                List<Text> texts = new ArrayList<>();
                for (String s : comment)
                    texts.add(new LiteralText(s));
                renderTooltip(matrices, texts, mouseX, mouseY);
            }
        });
    }

    @Override
    public void onClose() {
        client.openScreen(parent);
    }

    private void updatePage() {
        if (pages.size() > 1) {
            seekLeft.active = page > 0;
            seekRight.active = page < pages.size() - 1;
            for (AbstractButtonWidget btn : btnFields.keySet())
                btn.visible = false;
            for (Pair<AbstractButtonWidget, Field> pair : pages.get(page))
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

    private void setValue(Field f, Object value) {
        try {
            f.set(null, value);
            ClientOptions.write();
        } catch (IllegalAccessException e) {
            log.catching(e);
        }
    }

    private Text getBoolText(Field f) {
        return new LiteralText(getCleanName(f) + " : " + Command.formatFromBool(getBoolValue(f)) + String.valueOf(getBoolValue(f)).toUpperCase());
    }

    private double getSliderValue(Field f) {
        int[] cramp = getCramp(f);
        int value = getIntValue(f);
        return MathHelper.clamp((double) (value - cramp[0]) / (double) (cramp[1] - cramp[0]), 0.0D, 1.0D);
    }

    private int getIntValue(Field f) {
        if (f.getType() == int.class) {
            try {
                return f.getInt(null);
            } catch (IllegalAccessException e) {
                log.catching(e);
            }
        }
        return -1;
    }

    private boolean getBoolValue(Field f) {
        if (f.getType() == boolean.class) {
            try {
                return f.getBoolean(null);
            } catch (IllegalAccessException e) {
                log.catching(e);
            }
        }
        return false;
    }

    private int[] getCramp(Field f) {
        int[] cramp = new int[2];
        if (f.getType() == int.class && f.isAnnotationPresent(ClientOptions.Cramp.class)) {
            ClientOptions.Cramp cramp0 = f.getAnnotation(ClientOptions.Cramp.class);
            cramp[0] = cramp0.min();
            cramp[1] = cramp0.max();
        }
        return cramp;
    }

    private String[] getComment(Field f) {
        return f.isAnnotationPresent(ClientOptions.Comment.class) ? f.getAnnotation(ClientOptions.Comment.class).value() : null;
    }

    private boolean isHidden(Field f) {
        return f.isAnnotationPresent(ClientOptions.IsHidden.class) && !Boolean.parseBoolean(ClientOptions.getOption(f.getAnnotation(ClientOptions.IsHidden.class).value()));
    }

    private void checkChangeCallback(Field f, Object oldValue) {
        if (f.isAnnotationPresent(ClientOptions.ChangeCallback.class)) {
            Method method = MoreObjects.firstNonNull(ReflectionHelper.getMethod(f.getDeclaringClass(), f.getAnnotation(ClientOptions.ChangeCallback.class).value(), f.getType(), f.getType()), ReflectionHelper.getMethod(f.getDeclaringClass(), f.getAnnotation(ClientOptions.ChangeCallback.class).value()));
            if (method != null && Modifier.isStatic(method.getModifiers()))
                try {
                    method.setAccessible(true);
                    if (method.getParameterCount() == 2) method.invoke(null, oldValue, f.get(null));
                    else method.invoke(null);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.catching(e);
                }
        }
    }
}
