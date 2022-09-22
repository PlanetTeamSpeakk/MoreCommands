package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.gui.EnumRuleWidget;
import com.ptsmods.morecommands.miscellaneous.EnumRule;
import com.ptsmods.morecommands.miscellaneous.MoreCommandsGameRuleVisitor;
import net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net/minecraft/client/gui/screens/worldselection/EditGameRulesScreen$RuleList$1")
public abstract class MixinRuleListVisitor implements GameRules.GameRuleTypeVisitor, MoreCommandsGameRuleVisitor {

    @Shadow @Final EditGameRulesScreen this$0;
    @Shadow protected abstract <T extends GameRules.Value<T>> void createRuleWidget(GameRules.Key<T> key, EditGameRulesScreen.EntryFactory<T> ruleWidgetFactory);

    @Override
    public <E extends Enum<E>> void visitMCEnum(GameRules.Key<EnumRule<E>> key, GameRules.Type<EnumRule<E>> type) {
        createRuleWidget(key, (name, description, ruleName, rule) -> new EnumRuleWidget<>(this$0, name, description, ruleName, rule, key.getDescriptionId()));
    }
}
