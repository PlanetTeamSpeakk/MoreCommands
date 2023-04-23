package com.ptsmods.morecommands.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ptsmods.morecommands.client.MoreCommandsClient;
import com.ptsmods.morecommands.client.miscellaneous.CoolFeatureRenderer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class MixinPlayerRenderer extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public MixinPlayerRenderer(EntityRendererProvider.Context ctx, PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(EntityRendererProvider.Context ctx, boolean slim, CallbackInfo ci) {
        addLayer(new CoolFeatureRenderer(this));
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"), method = "scale(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;F)V")
    private void scale_scale(PoseStack stack, float x, float y, float z, AbstractClientPlayer entity, PoseStack matrixStack, float f) {
        float m = MoreCommandsClient.isCool(entity) ? 1.5f : 1f;
        stack.scale(x*m, y*m, z*m);
    }
}
