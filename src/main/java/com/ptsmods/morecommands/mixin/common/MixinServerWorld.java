package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import net.minecraft.fluid.Fluid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.ScheduledTick;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class MixinServerWorld {

    @Inject(at = @At("HEAD"), method = "tickFluid(Lnet/minecraft/world/ScheduledTick;)V", cancellable = true)
    private void tickFluid(ScheduledTick<Fluid> tick, CallbackInfo cbi) {
        if (!ReflectionHelper.<ServerWorld>cast(this).getGameRules().getBoolean(MoreCommands.doLiquidFlowRule)) cbi.cancel();
    }

}
