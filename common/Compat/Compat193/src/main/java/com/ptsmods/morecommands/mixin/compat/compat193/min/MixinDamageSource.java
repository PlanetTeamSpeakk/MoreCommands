package com.ptsmods.morecommands.mixin.compat.compat193.min;

import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.ReflectionHelper;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DamageSource.class)
public class MixinDamageSource {

    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(String name, CallbackInfo cbi) {
        // Only add normal damage sources to the map, not parameterised ones.
        if (ReflectionHelper.cast(this).getClass() == DamageSource.class) IMoreCommands.DAMAGE_SOURCES.put(name, ReflectionHelper.cast(this));
    }
}
