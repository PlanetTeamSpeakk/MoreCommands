package com.ptsmods.morecommands.mixin.compat.compat18.min;

import com.ptsmods.morecommands.api.IMoreGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(SnowLayerBlock.class)
public class MixinSnowBlock {

    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random random, CallbackInfo cbi) {
        if (!world.getGameRules().getBoolean(IMoreGameRules.get().doMeltRule())) cbi.cancel();
    }
}
