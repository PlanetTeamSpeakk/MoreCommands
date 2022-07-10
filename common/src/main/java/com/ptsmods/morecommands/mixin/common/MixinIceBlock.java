package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IceBlock.class)
public class MixinIceBlock {
    @Inject(at = @At("HEAD"), method = "melt", cancellable = true)
    protected void melt(BlockState state, Level world, BlockPos pos, CallbackInfo cbi) {
        if (!world.getGameRules().getBoolean(MoreGameRules.get().doMeltRule())) cbi.cancel();
    }
}
