package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.gui.EnumRuleWidget;
import com.ptsmods.morecommands.miscellaneous.EnumRule;
import com.ptsmods.morecommands.miscellaneous.MoreCommandsGameRuleVisitor;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net/minecraft/client/gui/screen/world/EditGameRulesScreen$RuleListWidget$1")
public abstract class MixinRuleListWidgetVisitor implements GameRules.Visitor, MoreCommandsGameRuleVisitor {

    @Shadow @Final EditGameRulesScreen field_24314;
    @Shadow protected abstract <T extends GameRules.Rule<T>> void createRuleWidget(GameRules.Key<T> key, EditGameRulesScreen.RuleWidgetFactory<T> ruleWidgetFactory);

    @Override
    public <E extends Enum<E>> void visitMCEnum(GameRules.Key<EnumRule<E>> key, GameRules.Type<EnumRule<E>> type) {
        createRuleWidget(key, (name, description, ruleName, rule) -> new EnumRuleWidget<>(field_24314, name, description, ruleName, rule, key.getTranslationKey()));
    }
}
