package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.commands.server.elevated.ReachCommand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    // Same method as super method, but uses the reach set with the reach command.
    @Overwrite
    public void updateTargetedEntity(float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        Entity entity = client.getCameraEntity();
        if (entity != null) {
            if (client.world != null) {
                client.getProfiler().push("pick");
                client.targetedEntity = null;
                double d = ReachCommand.getReach(client.player, false);
                client.crosshairTarget = entity.rayTrace(d, tickDelta, false);
                Vec3d vec3d = entity.getCameraPosVec(tickDelta);
                double e = d;
                e *= e;
                if (client.crosshairTarget != null) e = client.crosshairTarget.getPos().squaredDistanceTo(vec3d);
                Vec3d vec3d2 = entity.getRotationVec(1.0F);
                Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
                Box box = entity.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0D, 1.0D, 1.0D);
                EntityHitResult entityHitResult = ProjectileUtil.rayTrace(entity, vec3d, vec3d3, box, (entityx) -> !entityx.isSpectator() && entityx.collides(), e);
                if (entityHitResult != null) {
                    Entity entity2 = entityHitResult.getEntity();
                    Vec3d vec3d4 = entityHitResult.getPos();
                    double g = vec3d.squaredDistanceTo(vec3d4);
                    if (g < e || client.crosshairTarget == null) {
                        client.crosshairTarget = entityHitResult;
                        if (entity2 instanceof LivingEntity || entity2 instanceof ItemFrameEntity) client.targetedEntity = entity2;
                    }
                }
                client.getProfiler().pop();
            }
        }
    }

}
