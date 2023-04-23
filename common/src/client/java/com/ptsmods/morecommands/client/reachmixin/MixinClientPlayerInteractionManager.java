package com.ptsmods.morecommands.client.reachmixin;

import com.ptsmods.morecommands.commands.elevated.ReachCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(MultiPlayerGameMode.class)
public class MixinClientPlayerInteractionManager {
    @Shadow @Final private Minecraft minecraft;

    @Inject(at = @At("RETURN"), method = "getPickRange", cancellable = true)
    public void getReachDistance(CallbackInfoReturnable<Float> cbi) {
        cbi.setReturnValue((float) ReachCommand.getReach(Objects.requireNonNull(minecraft.player), false));
    }
}
