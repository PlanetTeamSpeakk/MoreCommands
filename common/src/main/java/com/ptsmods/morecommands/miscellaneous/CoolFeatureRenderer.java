package com.ptsmods.morecommands.miscellaneous;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.addons.PlayerEntityModelAddon;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class CoolFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

	public CoolFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
		super(context);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity,
					   float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		if (!MoreCommands.isCool(entity)) return;

		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(new Identifier("morecommands:textures/crown.png")));
		int overlay = LivingEntityRenderer.getOverlay(entity, 0);

		ModelPart crown = ((PlayerEntityModelAddon) getContextModel()).getCrown();
		if (crown != null) {
			matrices.push();

			float scale = 0.6075f;
			matrices.scale(scale, scale, scale);

			crown.copyTransform(getContextModel().head);
			crown.render(matrices, vertexConsumer, light, overlay);

			matrices.pop();
		}
	}
}
