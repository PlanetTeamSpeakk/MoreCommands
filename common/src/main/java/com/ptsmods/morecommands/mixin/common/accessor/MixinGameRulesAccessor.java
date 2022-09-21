package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRules.class)
public interface MixinGameRulesAccessor {
    @Invoker
    static <T extends GameRules.Value<T>> GameRules.Key<T> callRegister(String string, GameRules.Category category, GameRules.Type<T> type) {
        throw new AssertionError("This shouldn't happen.");
    }
}
