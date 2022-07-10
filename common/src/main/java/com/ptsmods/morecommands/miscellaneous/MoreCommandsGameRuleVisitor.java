package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.world.level.GameRules;

public interface MoreCommandsGameRuleVisitor {
    default <E extends Enum<E>> void visitMCEnum(GameRules.Key<EnumRule<E>> key, GameRules.Type<EnumRule<E>> type) {}
}
