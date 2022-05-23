package com.ptsmods.morecommands.mixin.compat.compat17.plus;

import com.ptsmods.morecommands.api.IMoreGameRules;
import net.minecraft.block.BlockState;
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

    @Inject(at = @At("HEAD"), method = "onLandedUpon")
    private void onLandedUpon_setToDirt(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo cbi) {
        if (world.getGameRules().getBoolean(IMoreGameRules.get().doFarmlandTrampleRule())) FarmlandBlock.setToDirt(state, world, pos);
    }
}
