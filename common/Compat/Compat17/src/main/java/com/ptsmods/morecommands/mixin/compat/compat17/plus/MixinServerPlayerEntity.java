package com.ptsmods.morecommands.mixin.compat.compat17.plus;

import com.ptsmods.morecommands.api.MixinAccessWidener;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.callbacks.EntityTeleportEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class MixinServerPlayerEntity {
    @Inject(at = @At("HEAD"), method = "setLevel", cancellable = true)
    public void setWorld(ServerLevel world, CallbackInfo cbi) {
        Entity thiz = ReflectionHelper.cast(this);
        if (EntityTeleportEvent.EVENT.invoker().onTeleport(thiz, thiz.getCommandSenderWorld(), world, thiz.position(), thiz.position())) cbi.cancel();
        if (thiz instanceof ServerPlayer) MixinAccessWidener.get().serverPlayerEntity$setSyncedExperience((ServerPlayer) thiz, -1); // Fix for the glitch that seemingly removes all your xp when you change worlds.
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setLevel(Lnet/minecraft/server/level/ServerLevel;)V"), method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDFF)V")
    private void teleport_setWorld(ServerPlayer thiz, ServerLevel targetWorld, ServerLevel targetWorld0, double x, double y, double z, float yaw, float pitch) {
        thiz.moveTo(x, y, z, yaw, pitch);
    }
}
