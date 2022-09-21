package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.api.IMoreGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FarmBlock.class)
public class MixinFarmBlock {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/FarmBlock;turnToDirt(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"), method = "fallOn")
    private void onLandedUpon_setToDirt(BlockState state, Level world, BlockPos pos) {
        if (world.getGameRules().getBoolean(IMoreGameRules.get().doFarmlandTrampleRule())) FarmBlock.turnToDirt(state, world, pos);
    }
}
