package com.ptsmods.morecommands.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.mixin.client.accessor.MixinItemGroupButtonWidgetAccessor;
import net.fabricmc.fabric.impl.item.group.FabricCreativeGuiComponents;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FabricCreativeGuiComponents.ItemGroupButtonWidget.class)
public class MixinItemGroupButtonWidget {
    private static final Identifier mc_BUTTON_TEX = new Identifier("morecommands:textures/gui/creative_buttons.png");
    // FIXME /fireball
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/impl/item/group/FabricCreativeGuiComponents$ItemGroupButtonWidget;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"), method = "render", remap = false)
    private void render_drawTexture_height(FabricCreativeGuiComponents.ItemGroupButtonWidget itemGroupButtonWidget, MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
        if (ClientOptions.Rendering.bigTabPager.getValue()) {
            RenderSystem.setShaderTexture(0, mc_BUTTON_TEX);
            itemGroupButtonWidget.drawTexture(matrices, x, y, (itemGroupButtonWidget.active && itemGroupButtonWidget.isHovered() ? 44 : 0) + (((MixinItemGroupButtonWidgetAccessor) itemGroupButtonWidget).getType() == FabricCreativeGuiComponents.Type.NEXT ? 22 : 0), itemGroupButtonWidget.active ? 0 : 22, 22, 22);
        } else itemGroupButtonWidget.drawTexture(matrices, x, y, u, v, width, height);
    }
}
