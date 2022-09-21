package com.ptsmods.morecommands.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer<T extends Entity> {
    @Shadow @Final protected EntityRenderDispatcher entityRenderDispatcher;

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"), method = "render")
    public void render_renderLabelIfPresent(EntityRenderer<?> thiz, T entity, Component text, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        if (MoreCommands.INSTANCE.isCute(entity))
            text = Compat.get().builderFromText(text)
                    .append(LiteralTextBuilder.builder(" \u2764")
                            .withStyle(Style.EMPTY.applyFormat(ChatFormatting.DARK_PURPLE)))
                    .append(LiteralTextBuilder.builder(" cutie")
                            .withStyle(Style.EMPTY.applyFormats(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD)))
                    .build();
        renderNameTag(entity, text, matrices, vertexConsumers, light);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isDiscrete()Z"), method = "renderNameTag")
    public boolean renderLabelIfPresent_isSneaky(Entity entity) {
        return !ClientOptions.Rendering.seeTagSneaking.getValue() && entity.isDiscrete();
    }

    @Shadow protected void renderNameTag(T entity, Component text, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {}
}