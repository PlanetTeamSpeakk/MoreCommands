package com.ptsmods.morecommands.mixin.compat.compat18min;

import com.ptsmods.morecommands.util.MixinMethods;
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

    @Inject(at = @At("RETURN"), method = "method_2896", remap = false)
    public void interactBlock(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cbi) {
        MixinMethods.doMultiDoorInteract(ReflectionHelper.cast(this), player, world, hand, hit, cbi);
    }
}
