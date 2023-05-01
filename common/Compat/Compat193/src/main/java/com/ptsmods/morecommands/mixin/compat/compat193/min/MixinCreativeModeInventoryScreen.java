package com.ptsmods.morecommands.mixin.compat.compat193.min;

import com.ptsmods.morecommands.api.clientoptions.ClientOption;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CreativeModeInventoryScreen.class)
public class MixinCreativeModeInventoryScreen {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;renderEntityInInventory(IIIFFLnet/minecraft/world/entity/LivingEntity;)V"), method = "renderBg")
    public void drawBackground_drawEntity(int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {
        InventoryScreen.renderEntityInInventory(x, y, (int) (size * (ClientOption.getBoolean("renderOwnTag") ? 0.85f : 1f)), mouseX, mouseY, entity);
    }
}
