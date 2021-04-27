package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.PickaxeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public class MixinPlayerInventory {

	@Inject(at = @At("RETURN"), method = "getBlockBreakingSpeed(Lnet/minecraft/block/BlockState;)F")
	public float getBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cbi) {
		PlayerInventory thiz = ReflectionHelper.<PlayerInventory>cast(this);
		if (thiz.main.get(thiz.selectedSlot).getItem() instanceof PickaxeItem && thiz.player.getDataTracker().get(MoreCommands.SUPERPICKAXE)) return Float.MAX_VALUE;
		else return cbi.getReturnValue();
	}

}
