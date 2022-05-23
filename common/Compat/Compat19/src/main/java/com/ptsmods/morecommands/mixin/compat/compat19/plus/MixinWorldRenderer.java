package com.ptsmods.morecommands.mixin.compat.compat19.plus;

import com.ptsmods.morecommands.api.IRainbow;
import com.ptsmods.morecommands.api.clientoptions.BooleanClientOption;
import com.ptsmods.morecommands.api.clientoptions.ClientOption;
import com.ptsmods.morecommands.api.clientoptions.ClientOptionCategory;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.awt.*;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;drawCuboidShapeOutline(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/util/shape/VoxelShape;DDDFFFF)V"), method = "drawBlockOutline")
    private void drawBlockOutline_drawShapeOutline(MatrixStack stack, VertexConsumer vertexConsumer, VoxelShape shape, double x, double y, double z, float r, float g, float b, float a) {
        if (((BooleanClientOption) ClientOption.getOptions().get(ClientOptionCategory.EASTER_EGGS).get("Rainbows")).getValue() && IRainbow.get() != null) {
            Color c = new Color(IRainbow.get().getRainbowColour(false));
            r = c.getRed() / 255f;
            g = c.getGreen() / 255f;
            b = c.getBlue() / 255f;
        }

        drawCuboidShapeOutline(stack, vertexConsumer, shape, x, y, z, r, g, b, a);
    }

    @Shadow private static void drawCuboidShapeOutline(MatrixStack matrixStack, VertexConsumer vertexConsumer, VoxelShape voxelShape, double d, double e, double f, float g, float h, float i, float j) {}
}
