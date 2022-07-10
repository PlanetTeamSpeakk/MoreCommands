package com.ptsmods.morecommands.mixin.compat.compat16;

import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.callbacks.ClientEntityEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class MixinClientWorld {

    @Inject(at = @At("TAIL"), method = "addEntity")
    private void onEntityLoad(int id, Entity entity, CallbackInfo cbi) {
        ClientEntityEvent.ENTITY_LOAD.invoker().onEntity(ReflectionHelper.cast(this), entity);
    }

    @Inject(at = @At("HEAD"), method = "onEntityRemoved")
    private void onEntityUnload(Entity entity, CallbackInfo cbi) {
        ClientEntityEvent.ENTITY_UNLOAD.invoker().onEntity(ReflectionHelper.cast(this), entity);
    }
}
