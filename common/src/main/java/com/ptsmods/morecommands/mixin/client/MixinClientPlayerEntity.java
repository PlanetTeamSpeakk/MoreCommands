package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class MixinClientPlayerEntity {
    private @Unique boolean moveStopped = false;

    @Inject(at = @At("HEAD"), method = "moveTowardsClosestSpace", cancellable = true)
    protected void pushOutOfBlocks(double x, double z, CallbackInfo cbi) {
        if (!ClientOptions.Tweaks.doBlockPush.getValue()) cbi.cancel();
    }

    @Inject(at = @At("HEAD"), method = "aiStep")
    private void tickMovement(CallbackInfo cbi) {
        LocalPlayer thiz = ReflectionHelper.cast(this);
        if (!thiz.input.shiftKeyDown && !thiz.input.jumping) {
            if (!moveStopped && ClientOptions.Tweaks.immediateMoveStop.getValue()) {
                thiz.setDeltaMovement(thiz.getDeltaMovement().x(), Math.min(0d, thiz.getDeltaMovement().y()), thiz.getDeltaMovement().z());
                moveStopped = true; // Without this variable, you would be able to bhop by combining sprintAutoJump and immediateMoveStop and immediateMoveStop would also act as anti-kb.
            }
        } else moveStopped = false;
        if (ClientOptions.Cheats.sprintAutoJump.getValue() && MoreCommands.isSingleplayer() && thiz.isSprinting() &&
                (thiz.zza != 0 || thiz.xxa != 0) && thiz.isOnGround() && !thiz.isShiftKeyDown())
            thiz.jumpFromGround();
    }
}
