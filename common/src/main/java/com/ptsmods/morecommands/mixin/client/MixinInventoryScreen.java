package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InventoryScreen.class)
public class MixinInventoryScreen {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;renderEntityInInventory(IIIFFLnet/minecraft/world/entity/LivingEntity;)V"), method = "renderBg")
    public void drawBackground_drawEntity(int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {
        InventoryScreen.renderEntityInInventory(x, y, (int) (size * (ClientOptions.Rendering.renderOwnTag.getValue() ? 0.95f : 1f)), mouseX, mouseY, entity);
    }

}
