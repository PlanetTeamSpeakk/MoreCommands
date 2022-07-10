package com.ptsmods.morecommands.mixin.compat.compat19.plus;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ptsmods.morecommands.api.IRainbow;
import com.ptsmods.morecommands.api.clientoptions.BooleanClientOption;
import com.ptsmods.morecommands.api.clientoptions.ClientOption;
import com.ptsmods.morecommands.api.clientoptions.ClientOptionCategory;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.awt.*;

@Mixin(LevelRenderer.class)
public abstract class MixinWorldRenderer {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderShape(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/phys/shapes/VoxelShape;DDDFFFF)V"), method = "renderHitOutline")
    private void drawBlockOutline_drawShapeOutline(PoseStack stack, VertexConsumer vertexConsumer, VoxelShape shape, double x, double y, double z, float r, float g, float b, float a) {
        if (((BooleanClientOption) ClientOption.getOptions().get(ClientOptionCategory.EASTER_EGGS).get("Rainbows")).getValue() && IRainbow.get() != null) {
            Color c = new Color(IRainbow.get().getRainbowColour(false));
            r = c.getRed() / 255f;
            g = c.getGreen() / 255f;
            b = c.getBlue() / 255f;
        }

        renderShape(stack, vertexConsumer, shape, x, y, z, r, g, b, a);
    }

    @Shadow private static void renderShape(PoseStack matrixStack, VertexConsumer vertexConsumer, VoxelShape voxelShape, double d, double e, double f, float g, float h, float i, float j) {}
}
