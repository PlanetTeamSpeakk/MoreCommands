package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.callbacks.EntityDeathEvent;
import com.ptsmods.morecommands.api.callbacks.EntityTeleportEvent;
import com.ptsmods.morecommands.api.util.compat.Compat;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class MixinEntity {
    private boolean mc_lastRemoved = false;

    @Inject(at = @At("HEAD"), method = "setPos(DDD)V", cancellable = true)
    public void teleport(double x, double y, double z, CallbackInfo cbi) {
        Entity thiz = ReflectionHelper.cast(this);
        if (thiz.squaredDistanceTo(x, y, z) >= 16 && EntityTeleportEvent.EVENT.invoker().onTeleport(thiz, thiz.getEntityWorld(), thiz.getEntityWorld(), thiz.getPos(), new Vec3d(x, y, z))) cbi.cancel();
    }

    @Inject(at = @At("RETURN"), method = "tick()V")
    public void tick(CallbackInfo cbi) {
        Entity thiz = ReflectionHelper.cast(this);
        boolean removed = Compat.get().isRemoved(thiz);
        if (removed && !mc_lastRemoved) EntityDeathEvent.EVENT.invoker().onDeath(thiz);
        mc_lastRemoved = removed;
    }

}
