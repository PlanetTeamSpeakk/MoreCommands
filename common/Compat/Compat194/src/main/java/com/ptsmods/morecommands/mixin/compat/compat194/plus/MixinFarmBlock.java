package com.ptsmods.morecommands.mixin.compat.compat194.plus;

import com.ptsmods.morecommands.api.IMoreGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FarmBlock.class)
public class MixinFarmBlock {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/FarmBlock;turnToDirt(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"), method = "fallOn")
    private void onLandedUpon_setToDirt(Entity entity, BlockState blockState, Level level, BlockPos blockPos) {
        if (level.getGameRules().getBoolean(IMoreGameRules.get().doFarmlandTrampleRule())) FarmBlock.turnToDirt(entity, blockState, level, blockPos);
    }
}
