package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.miscellaneous.ClientOptions;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CreativeInventoryScreen.class)
public class MixinCreativeInventoryScreen {
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen; drawEntity(IIIFFLnet/minecraft/entity/LivingEntity;)V"), method = "drawBackground(Lnet/minecraft/client/util/math/MatrixStack;FII)V")
	public void drawBackground_drawEntity(int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {
		InventoryScreen.drawEntity(x, y, (int) (size * (ClientOptions.Rendering.renderOwnTag.getValue() ? 0.85f : 1f)), mouseX, mouseY, entity);
	}
}
