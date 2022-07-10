package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.api.IDataTrackerHelper;
import com.ptsmods.morecommands.api.ReflectionHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public class MixinPlayerInventory {

    @Inject(at = @At("RETURN"), method = "getDestroySpeed", cancellable = true)
    public void getBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cbi) {
        Inventory thiz = ReflectionHelper.cast(this);
        if (thiz.items.get(thiz.selected).getItem() instanceof PickaxeItem && thiz.player.getEntityData().get(IDataTrackerHelper.get().superpickaxe())) cbi.setReturnValue(Float.MAX_VALUE);
    }
}
