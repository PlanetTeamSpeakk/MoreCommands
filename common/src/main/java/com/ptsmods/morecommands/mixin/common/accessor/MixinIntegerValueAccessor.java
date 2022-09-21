package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRules.IntegerValue.class)
public interface MixinIntegerValueAccessor {
    @Invoker
    static GameRules.Type<GameRules.IntegerValue> callCreate(int defaultValue) {
        throw new AssertionError("This shouldn't happen.");
    }
}
