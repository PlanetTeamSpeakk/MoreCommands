package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> {

    @Inject(at = @At("RETURN"), method = "shouldShowName(Lnet/minecraft/world/entity/LivingEntity;)Z", cancellable = true)
    public void hasLabel(T livingEntity, CallbackInfoReturnable<Boolean> cbi) {
        if (ClientOptions.Rendering.renderOwnTag.getValue() && livingEntity == Minecraft.getInstance().player)
            cbi.setReturnValue(true);
    }
}
