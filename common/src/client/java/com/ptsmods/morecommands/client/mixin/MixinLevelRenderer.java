package com.ptsmods.morecommands.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import com.ptsmods.morecommands.client.commands.SearchItemCommand;
import com.ptsmods.morecommands.client.util.VertexConsumerExtensions;
import lombok.experimental.ExtensionMethod;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@ExtensionMethod(VertexConsumerExtensions.class)
@Mixin(LevelRenderer.class)
public abstract class MixinLevelRenderer {
    private static final @Unique ResourceLocation unknownContentsTexture = new ResourceLocation("morecommands", "textures/unknown_contents.png");
    @Shadow private @Nullable ClientLevel level;

    @Inject(method = "renderLevel", at = @At("TAIL"))
    private void renderSearchOverlay(PoseStack poseStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, CallbackInfo ci) {
        ProfilerFiller profiler = Objects.requireNonNull(level).getProfiler();
        profiler.popPush("MoreCommands_ItemSearchOverlay");

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();

        // Create new stack and translate it
        PoseStack stack = new PoseStack();
        stack.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
        stack.mulPose(Axis.YP.rotationDegrees(camera.getYRot() + 180));

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buff = tess.getBuilder();

        // Begin our buffer.
        buff.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        BufferBuilder finalBuff = buff;
        SearchItemCommand.RESULTS.forEach((pos, res) -> renderBlockOverlay(stack, camera, finalBuff, pos,
                res.r, res.g, res.b, 0.33f, null));
        tess.end();

        RenderSystem.disableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        buff = tess.getBuilder();
        buff.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        BufferBuilder finalBuff1 = buff;
        SearchItemCommand.RESULTS.forEach((pos, res) -> {
            if (res.isUnknown()) renderBlockOverlay(stack, camera, finalBuff1, pos, 0, 0, 0, 1, unknownContentsTexture);
        });
        tess.end();

        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
    }

    private @Unique void renderBlockOverlay(PoseStack stack, Camera cam, BufferBuilder buff, BlockPos pos, float r, float g, float b, float a,
                                            @Nullable ResourceLocation texture) {
        stack.pushPose();
        Vec3 renderPos = new Vec3(pos.getX(), pos.getY(), pos.getZ()).subtract(cam.getPosition());
        stack.translate(renderPos.x, renderPos.y, renderPos.z);

        PoseStack.Pose pose = stack.last();
        boolean isTex = texture != null;

        if (texture != null) RenderSystem.setShaderTexture(0, texture);

        // Up
        vertex(buff, pose, 0, 1, 0, r, g, b, a, 0, 0, isTex);
        vertex(buff, pose, 1, 1, 0, r, g, b, a, 1, 0, isTex);
        vertex(buff, pose, 1, 1, 1, r, g, b, a, 1, 1, isTex);
        vertex(buff, pose, 0, 1, 1, r, g, b, a, 0, 1, isTex);

        // Down
        vertex(buff, pose, 0, 0, 0, r, g, b, a, 0, 1, isTex);
        vertex(buff, pose, 1, 0, 0, r, g, b, a, 1, 1, isTex);
        vertex(buff, pose, 1, 0, 1, r, g, b, a, 1, 0, isTex);
        vertex(buff, pose, 0, 0, 1, r, g, b, a, 0, 0, isTex);

        // North
        vertex(buff, pose, 0, 0, 0, r, g, b, a, 1, 1, isTex);
        vertex(buff, pose, 0, 1, 0, r, g, b, a, 1, 0, isTex);
        vertex(buff, pose, 1, 1, 0, r, g, b, a, 0, 0, isTex);
        vertex(buff, pose, 1, 0, 0, r, g, b, a, 0, 1, isTex);

        // East
        vertex(buff, pose, 1, 0, 0, r, g, b, a, 1, 1, isTex);
        vertex(buff, pose, 1, 1, 0, r, g, b, a, 1, 0, isTex);
        vertex(buff, pose, 1, 1, 1, r, g, b, a, 0, 0, isTex);
        vertex(buff, pose, 1, 0, 1, r, g, b, a, 0, 1, isTex);

        // South
        vertex(buff, pose, 0, 0, 1, r, g, b, a, 0, 1, isTex);
        vertex(buff, pose, 0, 1, 1, r, g, b, a, 0, 0, isTex);
        vertex(buff, pose, 1, 1, 1, r, g, b, a, 1, 0, isTex);
        vertex(buff, pose, 1, 0, 1, r, g, b, a, 1, 1, isTex);

        // West
        vertex(buff, pose, 0, 0, 0, r, g, b, a, 0, 1, isTex);
        vertex(buff, pose, 0, 1, 0, r, g, b, a, 0, 0, isTex);
        vertex(buff, pose, 0, 1, 1, r, g, b, a, 1, 0, isTex);
        vertex(buff, pose, 0, 0, 1, r, g, b, a, 1, 1, isTex);

        stack.popPose();
    }

    private @Unique void vertex(VertexConsumer buff, PoseStack.Pose pose,
                                float x, float y, float z,
                                float r, float g, float b, float a,
                                float u, float v, boolean isTex) {
        buff.compVertex(pose, x, y, z);
        if (isTex) buff.uv(u, v);
        else buff.color(r, g, b, a);
        buff.endVertex();
    }
}
