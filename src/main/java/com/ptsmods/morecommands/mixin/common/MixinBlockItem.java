package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.commands.server.elevated.UnlimitedCommand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockItem.class)
public class MixinBlockItem {

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity; isCreativeLevelTwoOp()Z"), method = "writeTagToBlockEntity(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;)Z")
	private static boolean isCreativeLevelTwoOp(PlayerEntity player) {
		return true; // If an itemstack has a BlockEntityTag tag, use that to create the BlockEntity regardless of gamemode. Necessary for silk spawners to work.
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack; decrement(I)V"), method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;")
	private void place_decrement(ItemStack stack, int i, ItemPlacementContext context) {
		if (!UnlimitedCommand.isUnlimited(stack)) stack.decrement(i);
	}

}
