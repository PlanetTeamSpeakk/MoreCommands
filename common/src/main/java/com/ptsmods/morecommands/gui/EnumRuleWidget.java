package com.ptsmods.morecommands.gui;

import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.api.util.text.TranslatableTextBuilder;
import com.ptsmods.morecommands.miscellaneous.EnumRule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Locale;

@Environment(EnvType.CLIENT)
public final class EnumRuleWidget<E extends Enum<E>> extends EditGameRulesScreen.NamedRuleWidget {
    private final ButtonWidget buttonWidget;
    private final String rootTranslationKey;

    public EnumRuleWidget(EditGameRulesScreen gameRuleScreen, Text name, List<OrderedText> description, final String ruleName, EnumRule<E> rule, String translationKey) {
        gameRuleScreen.super(description, name);

        this.rootTranslationKey = translationKey;
        this.buttonWidget = new ButtonWidget(10, 5, 88, 20, this.getValueText(rule.get()), (buttonWidget) -> {
            rule.cycle();
            buttonWidget.setMessage(this.getValueText(rule.get()));
        });

        this.children.add(this.buttonWidget);
    }

    public Text getValueText(E value) {
        String key = this.rootTranslationKey + "." + value.name().toLowerCase(Locale.ROOT);
        return (I18n.hasTranslation(key) ? TranslatableTextBuilder.translatable(key) : LiteralTextBuilder.literal(value.toString()));
    }

    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        this.drawName(matrices, y, x);

        this.buttonWidget.x = x + entryWidth - 89;
        this.buttonWidget.y = y;
        this.buttonWidget.render(matrices, mouseX, mouseY, tickDelta);
    }
}
