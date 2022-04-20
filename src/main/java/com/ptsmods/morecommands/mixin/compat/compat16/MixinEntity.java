package com.ptsmods.morecommands.mixin.compat.compat16;

import com.ptsmods.morecommands.callbacks.EntityTeleportCallback;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.mixin.common.accessor.MixinServerPlayerEntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class MixinEntity {
    @Inject(at = @At("HEAD"), method = "method_5866", cancellable = true, remap = false)
    public void setWorld(World world, CallbackInfo cbi) {
        Entity thiz = ReflectionHelper.cast(this);
        if (EntityTeleportCallback.EVENT.invoker().onTeleport(thiz, thiz.getEntityWorld(), world, thiz.getPos(), thiz.getPos())) cbi.cancel();
        if (thiz instanceof ServerPlayerEntity) ((MixinServerPlayerEntityAccessor) thiz).setSyncedExperience(-1); // Fix for the glitch that seemingly removes all your xp when you change worlds.
    }
}
