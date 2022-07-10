package com.ptsmods.morecommands.mixin.compat.compat16;

import com.ptsmods.morecommands.api.MixinAccessWidener;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.callbacks.EntityTeleportEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class MixinEntity {
    @Inject(at = @At("HEAD"), method = "setLevel", cancellable = true, remap = false)
    public void setWorld(Level world, CallbackInfo cbi) {
        Entity thiz = ReflectionHelper.cast(this);
        if (EntityTeleportEvent.EVENT.invoker().onTeleport(thiz, thiz.getCommandSenderWorld(), world, thiz.position(), thiz.position())) cbi.cancel();

        // Fix for the glitch that seemingly removes all your xp when you change worlds.
        if (thiz instanceof ServerPlayer) MixinAccessWidener.get().serverPlayerEntity$setSyncedExperience((ServerPlayer) thiz, -1);
    }
}
