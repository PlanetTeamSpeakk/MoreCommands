package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.ReflectionHelper;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class MixinEntity {

    @Inject(at = @At("RETURN"), method = "getBbHeight", cancellable = true)
    public final void getHeight(CallbackInfoReturnable<Float> cbi) {
        if (MoreCommands.isCool(ReflectionHelper.cast(this))) cbi.setReturnValue(cbi.getReturnValueF() * 1.5f);
    }
}
