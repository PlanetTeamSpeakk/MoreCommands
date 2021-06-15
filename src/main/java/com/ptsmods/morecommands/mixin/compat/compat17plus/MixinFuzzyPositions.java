package com.ptsmods.morecommands.mixin.compat.compat17plus;

import com.ptsmods.morecommands.MoreCommands;
import net.minecraft.entity.ai.FuzzyPositions;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;

@Mixin(FuzzyPositions.class)
public class MixinFuzzyPositions {
    @Inject(at = @At("HEAD"), method = "guessBest", cancellable = true)
    private static void guessBest(Supplier<BlockPos> factory, ToDoubleFunction<BlockPos> scorer, CallbackInfoReturnable<Vec3d> cbi) {
        if (!Objects.requireNonNull(MoreCommands.serverInstance.getWorld(World.OVERWORLD)).getGameRules().getBoolean(MoreCommands.doPathFindingRule)) cbi.setReturnValue(null);
    }
}
