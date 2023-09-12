package com.ptsmods.morecommands.mixin.compat.compat193.plus;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ptsmods.morecommands.api.IMoreCommandsClient;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(LevelRenderer.class)
public abstract class MixinLevelRenderer {
    @Shadow private @Nullable ClientLevel level;

    @Inject(method = "renderLevel", at = @At("TAIL"))
    private void renderSearchOverlay(PoseStack poseStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, CallbackInfo ci) {
        ProfilerFiller profiler = Objects.requireNonNull(level).getProfiler();
        profiler.popPush("MoreCommands_ItemSearchOverlay");

        // Create a new stack and translate it
        PoseStack stack = new PoseStack();
        stack.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
        stack.mulPose(Axis.YP.rotationDegrees(camera.getYRot() + 180));

        IMoreCommandsClient.get().renderSearchItemResults(stack, camera);
    }
}
