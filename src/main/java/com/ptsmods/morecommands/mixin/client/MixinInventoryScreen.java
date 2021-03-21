package com.ptsmods.morecommands.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.ptsmods.morecommands.miscellaneous.ClientOptions;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InventoryScreen.class)
public class MixinInventoryScreen {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen; drawEntity(IIIFFLnet/minecraft/entity/LivingEntity;)V"), method = "drawBackground(Lnet/minecraft/client/util/math/MatrixStack;FII)V")
    public void drawBackground_drawEntity(int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {
        InventoryScreen.drawEntity(x, y, (int) (size * (ClientOptions.Rendering.renderOwnTag.getValue() ? 0.95f : 1f)), mouseX, mouseY, entity);
    }

}
