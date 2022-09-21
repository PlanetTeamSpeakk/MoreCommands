package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.IMoreGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;

@Mixin(RandomPos.class)
public class MixinRandomPos {
    @Inject(at = @At("HEAD"), method = "generateRandomPos(Ljava/util/function/Supplier;Ljava/util/function/ToDoubleFunction;)Lnet/minecraft/world/phys/Vec3;", cancellable = true)
    private static void guessBest(Supplier<BlockPos> factory, ToDoubleFunction<BlockPos> scorer, CallbackInfoReturnable<Vec3> cbi) {
        if (!Objects.requireNonNull(IMoreCommands.get().getServer().getLevel(Level.OVERWORLD)).getGameRules().getBoolean(IMoreGameRules.get().doPathFindingRule())) cbi.setReturnValue(null);
    }
}
