package com.ptsmods.morecommands.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;

public class VertexConsumerExtensions {
    public static VertexConsumer compVertex(VertexConsumer self, PoseStack.Pose pose, float x, float y, float z) {
        return ClientCompat.get().vertex(self, pose, x, y, z);
    }

    public static VertexConsumer compNormal(VertexConsumer self, PoseStack.Pose pose, float nx, float ny, float nz) {
        return ClientCompat.get().normal(self, pose, nx, ny, nz);
    }
}
