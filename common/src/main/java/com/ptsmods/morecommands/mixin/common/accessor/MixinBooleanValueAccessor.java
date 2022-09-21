package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRules.BooleanValue.class)
public interface MixinBooleanValueAccessor {
    @Invoker
    static GameRules.Type<GameRules.BooleanValue> callCreate(boolean defaultValue) {
        throw new AssertionError("This shouldn't happen.");
    }
}
