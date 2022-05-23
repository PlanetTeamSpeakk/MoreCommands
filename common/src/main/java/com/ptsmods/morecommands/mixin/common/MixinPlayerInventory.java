package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.util.DataTrackerHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.PickaxeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public class MixinPlayerInventory {

    @Inject(at = @At("RETURN"), method = "getBlockBreakingSpeed(Lnet/minecraft/block/BlockState;)F", cancellable = true)
    public void getBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cbi) {
        PlayerInventory thiz = ReflectionHelper.cast(this);
        if (thiz.main.get(thiz.selectedSlot).getItem() instanceof PickaxeItem && thiz.player.getDataTracker().get(DataTrackerHelper.SUPERPICKAXE)) cbi.setReturnValue(Float.MAX_VALUE);
    }
}
