package com.ptsmods.morecommands.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.api.util.text.TranslatableTextBuilder;
import com.ptsmods.morecommands.client.mixin.accessor.MixinAbstractWidgetAccessor;
import com.ptsmods.morecommands.miscellaneous.EnumRule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import java.util.List;
import java.util.Locale;

@Environment(EnvType.CLIENT)
public final class EnumRuleWidget<E extends Enum<E>> extends EditGameRulesScreen.GameRuleEntry {
    private final Button buttonWidget;
    private final String rootTranslationKey;

    public EnumRuleWidget(EditGameRulesScreen gameRuleScreen, Component name, List<FormattedCharSequence> description, final String ruleName, EnumRule<E> rule, String translationKey) {
        gameRuleScreen.super(description, name);

        this.rootTranslationKey = translationKey;
        this.buttonWidget = ClientCompat.get().newButton(gameRuleScreen, 10, 5, 88, 20, this.getValueText(rule.get()), btn -> {
            rule.cycle();
            btn.setMessage(this.getValueText(rule.get()));
        }, null);

        this.children.add(this.buttonWidget);
    }

    public Component getValueText(E value) {
        String key = this.rootTranslationKey + "." + value.name().toLowerCase(Locale.ROOT);
        return (I18n.exists(key) ? TranslatableTextBuilder.translatable(key) : LiteralTextBuilder.literal(value.toString()));
    }

    public void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        this.renderLabel(matrices, y, x);

        MixinAbstractWidgetAccessor accessor = (MixinAbstractWidgetAccessor) this.buttonWidget;
        accessor.setX_(x + entryWidth - 89);
        accessor.setY_(y);
        this.buttonWidget.render(matrices, mouseX, mouseY, tickDelta);
    }
}
