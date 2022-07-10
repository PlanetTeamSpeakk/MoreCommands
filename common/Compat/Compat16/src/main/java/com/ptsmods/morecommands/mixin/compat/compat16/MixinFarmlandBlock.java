package com.ptsmods.morecommands.mixin.compat.compat16;

import com.ptsmods.morecommands.api.IMoreGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FarmBlock.class)
public class MixinFarmlandBlock {

    @Inject(at = @At("HEAD"), method = "fallOn", cancellable = true)
    private void onLandedUpon(Level world, BlockPos pos, Entity entity, float fallDistance, CallbackInfo cbi) {
        if (!IMoreGameRules.get().checkBooleanWithPerm(world.getGameRules(), IMoreGameRules.get().doFarmlandTrampleRule(), entity)) cbi.cancel();
    }
}
