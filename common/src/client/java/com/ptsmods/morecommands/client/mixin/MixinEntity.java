package com.ptsmods.morecommands.client.mixin;

import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.client.MoreCommandsClient;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class MixinEntity {

    @Inject(at = @At("RETURN"), method = "getBbHeight", cancellable = true)
    public final void getHeight(CallbackInfoReturnable<Float> cbi) {
        if (MoreCommandsClient.isCool(ReflectionHelper.cast(this))) cbi.setReturnValue(cbi.getReturnValueF() * 1.5f);
    }
}
