package com.ptsmods.morecommands.client.miscellaneous;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ptsmods.morecommands.api.addons.PlayerEntityModelAddon;
import com.ptsmods.morecommands.client.MoreCommandsClient;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CoolFeatureRenderer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public CoolFeatureRenderer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> context) {
        super(context);
    }

    @Override
    public void render(@NotNull PoseStack matrices, @NotNull MultiBufferSource vertexConsumers, int light, @NotNull AbstractClientPlayer entity,
                       float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (!MoreCommandsClient.isCool(entity)) return;

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.entitySolid(new ResourceLocation("morecommands:textures/crown.png")));
        int overlay = LivingEntityRenderer.getOverlayCoords(entity, 0);

        ModelPart crown = ((PlayerEntityModelAddon) getParentModel()).getCrown();
        if (crown == null) return;

        matrices.pushPose();

        float scale = 0.6075f;
        matrices.scale(scale, scale, scale);

        crown.copyFrom(getParentModel().head);
        crown.render(matrices, vertexConsumer, light, overlay);

        matrices.popPose();
    }
}