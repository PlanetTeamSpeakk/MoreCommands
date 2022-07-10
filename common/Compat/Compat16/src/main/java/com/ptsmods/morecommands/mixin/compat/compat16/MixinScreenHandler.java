package com.ptsmods.morecommands.mixin.compat.compat16;

import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.miscellaneous.InvSeeScreenHandler;
import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerMenu.class)
public class MixinScreenHandler {
    @Inject(at = @At("HEAD"), method = "clicked", cancellable = true)
    public void onSlotClick(int slotIndex, int button, ClickType actionType, Player player, CallbackInfoReturnable<ItemStack> cbi) {
        if (ReflectionHelper.<AbstractContainerMenu>cast(this) instanceof InvSeeScreenHandler && (Platform.getEnv() != EnvType.CLIENT || !(ReflectionHelper.<InvSeeScreenHandler>cast(this).target instanceof RemotePlayer)))
            cbi.setReturnValue(ItemStack.EMPTY);
    }
}
