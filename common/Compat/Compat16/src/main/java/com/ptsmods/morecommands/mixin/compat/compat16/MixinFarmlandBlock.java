package com.ptsmods.morecommands.mixin.compat.compat16;

import com.ptsmods.morecommands.api.IMoreGameRules;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FarmlandBlock.class)
public class MixinFarmlandBlock {

    @Inject(at = @At("HEAD"), method = "onLandedUpon", cancellable = true)
    private void onLandedUpon(World world, BlockPos pos, Entity entity, float fallDistance, CallbackInfo cbi) {
        if (!IMoreGameRules.get().checkBooleanWithPerm(world.getGameRules(), IMoreGameRules.get().doFarmlandTrampleRule(), entity)) cbi.cancel();
    }
}
