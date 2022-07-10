package com.ptsmods.morecommands.mixin.compat.compat17.plus;

import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.callbacks.ClientEntityEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(targets = "net/minecraft/client/multiplayer/ClientLevel$EntityCallbacks")
public class MixinClientEntityHandler {
    @Inject(method = "onTrackingStart(Lnet/minecraft/world/entity/Entity;)V", at = @At("TAIL"))
    private void invokeLoadEntity(Entity entity, CallbackInfo ci) {
        ClientEntityEvent.ENTITY_LOAD.invoker().onEntity(ReflectionHelper.cast(this), entity);
    }

    @Inject(method = "onTrackingEnd(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"))
    private void invokeUnloadEntity(Entity entity, CallbackInfo ci) {
        ClientEntityEvent.ENTITY_UNLOAD.invoker().onEntity(ReflectionHelper.cast(this), entity);
    }
}
