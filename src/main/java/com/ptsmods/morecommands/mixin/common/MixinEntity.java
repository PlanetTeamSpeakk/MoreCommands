package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.callbacks.EntityDeathCallback;
import com.ptsmods.morecommands.callbacks.EntityTeleportCallback;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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
        if (thiz.squaredDistanceTo(x, y, z) >= 16 && EntityTeleportCallback.EVENT.invoker().onTeleport(thiz, thiz.getEntityWorld(), thiz.getEntityWorld(), thiz.getPos(), new Vec3d(x, y, z))) cbi.cancel();
    }

    @Inject(at = @At("HEAD"), method = "setWorld(Lnet/minecraft/world/World;)V", cancellable = true)
    public void setWorld(World world, CallbackInfo cbi) {
        Entity thiz = ReflectionHelper.cast(this);
        if (EntityTeleportCallback.EVENT.invoker().onTeleport(thiz, thiz.getEntityWorld(), world, thiz.getPos(), thiz.getPos())) cbi.cancel();
        if (thiz instanceof ServerPlayerEntity) ((MixinServerPlayerEntityAccessor) thiz).setSyncedExperience(-1); // Fix for the glitch that seemingly removes all your xp when you change worlds.
    }

    public void tick(CallbackInfo cbi) {
        Entity thiz = ReflectionHelper.cast(this);
        if (thiz.removed && !mc_lastRemoved) EntityDeathCallback.EVENT.invoker().onDeath(thiz);
        mc_lastRemoved = thiz.removed;
    }

}
