package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.api.IDataTrackerHelper;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(MultiPlayerGameMode.class)
public class MixinClientPlayerInteractionManager {
    @Shadow private int destroyDelay;
    @Shadow private GameType localPlayerMode;

    @Inject(at = @At("RETURN"), method = "startDestroyBlock", cancellable = true)
    public void attackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cbi) {
        cbi.setReturnValue(updateAndReturn(cbi));
    }

    @Inject(at = @At("RETURN"), method = "continueDestroyBlock", cancellable = true)
    public void updateBlockBreakingProgress(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cbi) {
        cbi.setReturnValue(updateAndReturn(cbi));
    }

    @Unique
    private boolean updateAndReturn(CallbackInfoReturnable<Boolean> cbi) {
        if (localPlayerMode.isCreative() && Objects.requireNonNull(Minecraft.getInstance().player).getEntityData().get(IDataTrackerHelper.get().superpickaxe()) &&
                Minecraft.getInstance().player.getMainHandItem().getItem() instanceof PickaxeItem) destroyDelay = 0;
        return cbi.getReturnValue();
    }

    @Inject(at = @At("RETURN"), method = "isAlwaysFlying", cancellable = true)
    public void isFlyingLocked(CallbackInfoReturnable<Boolean> cbi) {
        cbi.setReturnValue(cbi.getReturnValueZ() || ClientOptions.Tweaks.lockFlying.getValue());
    }
}
