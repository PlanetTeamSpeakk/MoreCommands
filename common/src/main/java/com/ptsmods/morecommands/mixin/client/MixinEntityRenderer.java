package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer<T extends Entity> {
	@Shadow @Final protected EntityRenderDispatcher dispatcher;

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"), method = "render")
	public void render_renderLabelIfPresent(EntityRenderer<?> thiz, T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		if (MoreCommands.isCute(entity))
			text = Compat.get().builderFromText(text)
					.append(LiteralTextBuilder.builder(" \u2764")
							.withStyle(Style.EMPTY.withFormatting(Formatting.DARK_PURPLE)))
					.append(LiteralTextBuilder.builder(" cutie")
							.withStyle(Style.EMPTY.withFormatting(Formatting.LIGHT_PURPLE, Formatting.BOLD)))
					.build();
		renderLabelIfPresent(entity, text, matrices, vertexConsumers, light);
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSneaky()Z"), method = "renderLabelIfPresent")
	public boolean renderLabelIfPresent_isSneaky(Entity entity) {
		return !ClientOptions.Rendering.seeTagSneaking.getValue() && entity.isSneaky();
	}

	@Shadow protected void renderLabelIfPresent(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {}
}