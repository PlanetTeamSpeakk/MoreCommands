package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> {

    @Inject(at = @At("RETURN"), method = "hasLabel(Lnet/minecraft/entity/LivingEntity;)Z", cancellable = true)
    public void hasLabel(T livingEntity, CallbackInfoReturnable<Boolean> cbi) {
        if (ClientOptions.Rendering.renderOwnTag.getValue() && livingEntity == MinecraftClient.getInstance().player)
            cbi.setReturnValue(true);
    }
}
