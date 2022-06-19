package com.ptsmods.morecommands.mixin.compat.compat17.plus;

import com.ptsmods.morecommands.api.IMoreGameRules;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FarmlandBlock.class)
public class MixinFarmlandBlock {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/FarmlandBlock;setToDirt(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"), method = "onLandedUpon")
    private void onLandedUpon_setToDirt(BlockState state, World world, BlockPos pos) {
        if (world.getGameRules().getBoolean(IMoreGameRules.get().doFarmlandTrampleRule())) FarmlandBlock.setToDirt(state, world, pos);
    }
}
