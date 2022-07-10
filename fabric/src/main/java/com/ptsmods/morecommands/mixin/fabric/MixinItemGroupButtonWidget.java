package com.ptsmods.morecommands.mixin.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.fabricmc.fabric.impl.item.group.FabricCreativeGuiComponents;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FabricCreativeGuiComponents.ItemGroupButtonWidget.class)
public class MixinItemGroupButtonWidget {
    private static final @Unique ResourceLocation BUTTON_TEX = new ResourceLocation("morecommands:textures/gui/creative_buttons.png");

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/impl/item/group/FabricCreativeGuiComponents$ItemGroupButtonWidget;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"), method = "render")
    private void render_drawTexture_height(FabricCreativeGuiComponents.ItemGroupButtonWidget itemGroupButtonWidget, PoseStack matrices, int x, int y, int u, int v, int width, int height) {
        if (ClientOptions.Rendering.bigTabPager.getValue()) {
            ClientCompat.get().bindTexture(BUTTON_TEX);
            itemGroupButtonWidget.blit(matrices, x, y, (itemGroupButtonWidget.active && itemGroupButtonWidget.isHoveredOrFocused() ? 44 : 0) +
                    (((MixinItemGroupButtonWidgetAccessor) itemGroupButtonWidget).getType() == FabricCreativeGuiComponents.Type.NEXT ? 22 : 0), itemGroupButtonWidget.active ? 0 : 22, 22, 22);
        } else itemGroupButtonWidget.blit(matrices, x, y, u, v, width, height);
    }
}
