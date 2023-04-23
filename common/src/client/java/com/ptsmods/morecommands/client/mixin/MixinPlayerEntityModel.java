package com.ptsmods.morecommands.client.mixin;

import com.ptsmods.morecommands.api.addons.PlayerEntityModelAddon;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerModel.class)
public class MixinPlayerEntityModel implements PlayerEntityModelAddon {
    private static final @Unique String crownId = "mccrown";
    private @Unique ModelPart crown;

    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(ModelPart root, boolean thinArms, CallbackInfo cbi) {
        crown = root.getChild(crownId);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/builders/PartDefinition;addOrReplaceChild(Ljava/lang/String;Lnet/minecraft/client/model/geom/builders/CubeListBuilder;Lnet/minecraft/client/model/geom/PartPose;)Lnet/minecraft/client/model/geom/builders/PartDefinition;",
            ordinal = 0, shift = At.Shift.AFTER), method = "createMesh", locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void getTexturedModelData(CubeDeformation dilation, boolean slim, CallbackInfoReturnable<MeshDefinition> cbi, MeshDefinition modelData, PartDefinition root) {
        final float top = -18f, left = -8f, right = 7f, front = -8f, back = 7f;
        root.addOrReplaceChild(crownId, CubeListBuilder.create()
                        .addBox("North - Main", left, top + 4, front, 16, 6, 1, dilation, 30, 0)

                        .texOffs(0, 0)
                        .addBox("North - Spike Left 1", left, top, front, 1, 4, 1, dilation)
                        .addBox("North - Spike Left 2", left + 1, top + 1, front, 1, 3, 1, dilation)
                        .addBox("North - Spike Left 3", left + 2, top + 2, front, 1, 2, 1, dilation)
                        .addBox("North - Spike Left 4", left + 3, top + 3, front, 1, 1, 1, dilation)

                        .addBox("North - Spike Right 1", right, top, front, 1, 4, 1, dilation)
                        .addBox("North - Spike Right 2", right - 1, top + 1, front, 1, 3, 1, dilation)
                        .addBox("North - Spike Right 3", right - 2, top + 2, front, 1, 2, 1, dilation)
                        .addBox("North - Spike Right 4", right - 3, top + 3, front, 1, 1, 1, dilation)

                        .addBox("North - Center Base", left + 6, top + 3, front, 4, 1, 1, dilation, 4, 0)
                        .addBox("North - Center Post", left + 7, top + 1, front, 2, 2, 1, dilation, 4, 2)
                        .addBox("North - Center Middle", left + 5, top - 1, front, 6, 2, 1, dilation, 10, 2)
                        .addBox("North - Center Top", left + 7, top - 3, front, 2, 2, 1, dilation, 4, 5)

                        .addBox("West - Main", right, top + 4, front, 1, 6, 16, dilation, 30, 14)

                        .texOffs(0, 0)
                        .addBox("West - Spike Left 1", right, top + 0, front + 0, 1, 4, 1, dilation)
                        .addBox("West - Spike Left 2", right, top + 1, front + 1, 1, 3, 1, dilation)
                        .addBox("West - Spike Left 3", right, top + 2, front + 2, 1, 2, 1, dilation)
                        .addBox("West - Spike Left 4", right, top + 3, front + 3, 1, 1, 1, dilation)

                        .addBox("West - Spike Middle 1", right, top + 3, front + 4, 1, 1, 1, dilation)
                        .addBox("West - Spike Middle 2", right, top + 2, front + 5, 1, 2, 1, dilation)
                        .addBox("West - Spike Middle 3", right, top + 1, front + 6, 1, 3, 1, dilation)
                        .addBox("West - Spike Middle 4", right, top + 0, front + 7, 1, 4, 1, dilation)
                        .addBox("West - Spike Middle 5", right, top + 0, front + 8, 1, 4, 1, dilation)
                        .addBox("West - Spike Middle 6", right, top + 1, front + 9, 1, 3, 1, dilation)
                        .addBox("West - Spike Middle 7", right, top + 2, front + 10, 1, 2, 1, dilation)
                        .addBox("West - Spike Middle 8", right, top + 3, front + 11, 1, 1, 1, dilation)

                        .addBox("West - Spike Right 1", right, top + 0, back - 0, 1, 4, 1, dilation)
                        .addBox("West - Spike Right 2", right, top + 1, back - 1, 1, 3, 1, dilation)
                        .addBox("West - Spike Right 3", right, top + 2, back - 2, 1, 2, 1, dilation)
                        .addBox("West - Spike Right 4", right, top + 3, back - 3, 1, 1, 1, dilation)

                        .addBox("East - Main", left, top + 4, front, 1, 6, 16, dilation, 30, 36)

                        .texOffs(0, 0)
                        .addBox("East - Spike Left 1", left, top + 0, front + 0, 1, 4, 1, dilation)
                        .addBox("East - Spike Left 2", left, top + 1, front + 1, 1, 3, 1, dilation)
                        .addBox("East - Spike Left 3", left, top + 2, front + 2, 1, 2, 1, dilation)
                        .addBox("East - Spike Left 4", left, top + 3, front + 3, 1, 1, 1, dilation)

                        .addBox("East - Spike Middle 1", left, top + 3, front + 4, 1, 1, 1, dilation)
                        .addBox("East - Spike Middle 2", left, top + 2, front + 5, 1, 2, 1, dilation)
                        .addBox("East - Spike Middle 3", left, top + 1, front + 6, 1, 3, 1, dilation)
                        .addBox("East - Spike Middle 4", left, top + 0, front + 7, 1, 4, 1, dilation)
                        .addBox("East - Spike Middle 5", left, top + 0, front + 8, 1, 4, 1, dilation)
                        .addBox("East - Spike Middle 6", left, top + 1, front + 9, 1, 3, 1, dilation)
                        .addBox("East - Spike Middle 7", left, top + 2, front + 10, 1, 2, 1, dilation)
                        .addBox("East - Spike Middle 8", left, top + 3, front + 11, 1, 1, 1, dilation)

                        .addBox("East - Spike Right 1", left, top + 0, back - 0, 1, 4, 1, dilation)
                        .addBox("East - Spike Right 2", left, top + 1, back - 1, 1, 3, 1, dilation)
                        .addBox("East - Spike Right 3", left, top + 2, back - 2, 1, 2, 1, dilation)
                        .addBox("East - Spike Right 4", left, top + 3, back - 3, 1, 1, 1, dilation)

                        .addBox("South - Main", left, top + 4, back, 16, 6, 1, dilation, 30, 7)

                        .texOffs(0, 0)
                        .addBox("South - Spike Left 1", left + 0, top + 0, back, 1, 4, 1, dilation)
                        .addBox("South - Spike Left 2", left + 1, top + 1, back, 1, 3, 1, dilation)
                        .addBox("South - Spike Left 3", left + 2, top + 2, back, 1, 2, 1, dilation)
                        .addBox("South - Spike Left 4", left + 3, top + 3, back, 1, 1, 1, dilation)

                        .addBox("South - Spike Middle 1", left + 4, top + 3, back, 1, 1, 1, dilation)
                        .addBox("South - Spike Middle 2", left + 5, top + 2, back, 1, 2, 1, dilation)
                        .addBox("South - Spike Middle 3", left + 6, top + 1, back, 1, 3, 1, dilation)
                        .addBox("South - Spike Middle 4", left + 7, top + 0, back, 1, 4, 1, dilation)
                        .addBox("South - Spike Middle 5", left + 8, top + 0, back, 1, 4, 1, dilation)
                        .addBox("South - Spike Middle 6", left + 9, top + 1, back, 1, 3, 1, dilation)
                        .addBox("South - Spike Middle 7", left + 10, top + 2, back, 1, 2, 1, dilation)
                        .addBox("South - Spike Middle 8", left + 11, top + 3, back, 1, 1, 1, dilation)

                        .addBox("South - Spike Right 1", right - 0, top + 0, back, 1, 4, 1, dilation)
                        .addBox("South - Spike Right 2", right - 1, top + 1, back, 1, 3, 1, dilation)
                        .addBox("South - Spike Right 3", right - 2, top + 2, back, 1, 2, 1, dilation)
                        .addBox("South - Spike Right 4", right - 3, top + 3, back, 1, 1, 1, dilation),
                PartPose.ZERO);
    }

    @Inject(at = @At("RETURN"), method = "setAllVisible")
    public void setVisible(boolean visible, CallbackInfo ci) {
        crown.visible = visible;
    }

    @Override
    public ModelPart getCrown() {
        return crown;
    }
}
