package com.ptsmods.morecommands.mixin.compat.compat17.plus;

import com.ptsmods.morecommands.api.callbacks.ClientEntityEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(targets = "net/minecraft/client/world/ClientWorld$ClientEntityHandler")
public class MixinClientEntityHandler {
    @Shadow @Final ClientWorld field_27735;

    @Inject(method = "startTracking(Lnet/minecraft/entity/Entity;)V", at = @At("TAIL"))
    private void invokeLoadEntity(Entity entity, CallbackInfo ci) {
        ClientEntityEvent.ENTITY_LOAD.invoker().onEntity(field_27735, entity);
    }

    @Inject(method = "stopTracking(Lnet/minecraft/entity/Entity;)V", at = @At("HEAD"))
    private void invokeUnloadEntity(Entity entity, CallbackInfo ci) {
        ClientEntityEvent.ENTITY_UNLOAD.invoker().onEntity(field_27735, entity);
    }
}
