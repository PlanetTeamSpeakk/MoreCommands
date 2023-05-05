package com.ptsmods.morecommands.client.mixin.fabric;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.fabricmc.fabric.impl.client.itemgroup.FabricCreativeGuiComponents;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("UnstableApiUsage")
@Pseudo
@Mixin(FabricCreativeGuiComponents.ItemGroupButtonWidget.class)
public class MixinItemGroupButtonWidget {
    @Shadow @Final
    FabricCreativeGuiComponents.Type type;
    private static final @Unique ResourceLocation BUTTON_TEX = new ResourceLocation("morecommands:textures/gui/creative_buttons.png");

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/impl/client/itemgroup/FabricCreativeGuiComponents$ItemGroupButtonWidget;blit(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIII)V"), method = "render")
    private void render_drawTexture_height(PoseStack matrices, int x, int y, int u, int v, int width, int height) {
        FabricCreativeGuiComponents.ItemGroupButtonWidget thiz = ReflectionHelper.cast(this);

        if (ClientOptions.Rendering.bigTabPager.getValue()) {
            RenderSystem.setShaderTexture(0, BUTTON_TEX);
            FabricCreativeGuiComponents.ItemGroupButtonWidget.blit(matrices, x, y, (thiz.active && thiz.isHoveredOrFocused() ? 44 : 0) +
                    (type == FabricCreativeGuiComponents.Type.NEXT ? 22 : 0), thiz.active ? 0 : 22, 22, 22);
        } else FabricCreativeGuiComponents.ItemGroupButtonWidget.blit(matrices, x, y, u, v, width, height);
    }
}
