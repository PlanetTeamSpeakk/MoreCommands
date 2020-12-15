package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.ClientOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer<T extends Entity> {

    @Shadow @Final protected EntityRenderDispatcher dispatcher;

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer; renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"), method = "render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V")
    public void render_renderLabelIfPresent(EntityRenderer<?> thiz, T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (MoreCommands.isCute(entity)) text = new LiteralText("").append(text).append(new LiteralText(" \u2764").setStyle(Style.EMPTY.withFormatting(Formatting.DARK_PURPLE))).append(new LiteralText(" cutie").setStyle(Style.EMPTY.withFormatting(Formatting.LIGHT_PURPLE, Formatting.BOLD)));
        renderLabelIfPresent(entity, text, matrices, vertexConsumers, light);
    }

    @Overwrite
    public void renderLabelIfPresent(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        double d = this.dispatcher.getSquaredDistanceToCamera(entity);
        if (d <= 4096.0D) {
            boolean bl = ClientOptions.Rendering.seeTagSneaking || !entity.isSneaky(); // <-- Only changed line, quite OP if you ask me tbh.
            float f = entity.getHeight() + 0.5F;
            int i = "deadmau5".equals(text.getString()) ? -10 : 0;
            matrices.push();
            matrices.translate(0.0D, (double)f, 0.0D);
            matrices.multiply(this.dispatcher.getRotation());
            matrices.scale(-0.025F, -0.025F, 0.025F);
            Matrix4f matrix4f = matrices.peek().getModel();
            float g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
            int j = (int)(g * 255.0F) << 24;
            TextRenderer textRenderer = dispatcher.getTextRenderer();
            float h = (float)(-textRenderer.getWidth(text) / 2);
            textRenderer.draw(text, h, (float)i, 553648127, false, matrix4f, vertexConsumers, bl, j, light);
            if (bl) {
                textRenderer.draw(text, h, (float)i, -1, false, matrix4f, vertexConsumers, false, 0, light);
            }
            matrices.pop();
        }
    }

}
