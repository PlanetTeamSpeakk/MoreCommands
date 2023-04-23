package com.ptsmods.morecommands.client.mixin;

import com.ptsmods.morecommands.api.callbacks.ClientEntityEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(targets = "net/minecraft/client/multiplayer/ClientLevel$EntityCallbacks")
public class MixinEntityCallbacks {
    @Inject(method = "onTrackingStart(Lnet/minecraft/world/entity/Entity;)V", at = @At("TAIL"))
    private void invokeLoadEntity(Entity entity, CallbackInfo ci) {
        ClientEntityEvent.ENTITY_LOAD.invoker().onEntity(Minecraft.getInstance().level, entity);
    }

    @Inject(method = "onTrackingEnd(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"))
    private void invokeUnloadEntity(Entity entity, CallbackInfo ci) {
        ClientEntityEvent.ENTITY_UNLOAD.invoker().onEntity(Minecraft.getInstance().level, entity);
    }
}
