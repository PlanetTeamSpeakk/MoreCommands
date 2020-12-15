package com.ptsmods.morecommands.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.ptsmods.morecommands.miscellaneous.ClientOptions;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InventoryScreen.class)
public class MixinInventoryScreen {

    @Redirect(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem; scalef(FFF)V"), method = "drawEntity(IIIFFLnet/minecraft/entity/LivingEntity;)V")
    private static void scalef(float x, float y, float z) {
        float m = ClientOptions.Rendering.renderOwnTag ? 0.85f : 1f; // So there's room for the tag.
        RenderSystem.scalef(m*x, m*y, m*z);
    }

}
