package com.ptsmods.morecommands.mixin.fabric;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.fabricmc.fabric.impl.client.item.group.FabricCreativeGuiComponents;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(FabricCreativeGuiComponents.ItemGroupButtonWidget.class)
public class MixinItemGroupButtonWidget {
    private static final @Unique ResourceLocation BUTTON_TEX = new ResourceLocation("morecommands:textures/gui/creative_buttons.png");

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/impl/client/item/group/FabricCreativeGuiComponents$ItemGroupButtonWidget;blit(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIII)V"), method = "render")
    private void render_drawTexture_height(FabricCreativeGuiComponents.ItemGroupButtonWidget itemGroupButtonWidget, PoseStack matrices, int x, int y, int u, int v, int width, int height) {
        if (ClientOptions.Rendering.bigTabPager.getValue()) {
            RenderSystem.setShaderTexture(0, BUTTON_TEX);
            itemGroupButtonWidget.blit(matrices, x, y, (itemGroupButtonWidget.active && itemGroupButtonWidget.isHoveredOrFocused() ? 44 : 0) +
                    (((MixinItemGroupButtonWidgetAccessor) itemGroupButtonWidget).getType() == FabricCreativeGuiComponents.Type.NEXT ? 22 : 0), itemGroupButtonWidget.active ? 0 : 22, 22, 22);
        } else itemGroupButtonWidget.blit(matrices, x, y, u, v, width, height);
    }
}
