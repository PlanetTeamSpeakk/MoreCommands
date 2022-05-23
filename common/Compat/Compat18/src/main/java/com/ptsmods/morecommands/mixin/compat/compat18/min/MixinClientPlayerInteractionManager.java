package com.ptsmods.morecommands.mixin.compat.compat18.min;

import com.ptsmods.morecommands.api.MixinAccessWidener;
import com.ptsmods.morecommands.api.ReflectionHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Inject(at = @At("RETURN"), method = "interactBlock")
    public void interactBlock(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cbi) {
        MixinAccessWidener.get().doMultiDoorInteract(ReflectionHelper.cast(this), player, world, hand, hit, cbi);
    }
}
