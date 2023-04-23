package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.commands.elevated.UnlimitedCommand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockItem.class)
public class MixinBlockItem {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;canUseGameMasterBlocks()Z"), method = "updateCustomBlockEntityTag(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)Z")
    private static boolean isCreativeLevelTwoOp(Player player) {
        return true; // If an itemstack has a BlockEntityTag tag, use that to create the BlockEntity regardless of gamemode. Necessary for silk spawners to work.
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"), method = "place")
    private void place_decrement(ItemStack stack, int i, BlockPlaceContext context) {
        if (!UnlimitedCommand.isUnlimited(stack)) stack.shrink(i);
    }
}
