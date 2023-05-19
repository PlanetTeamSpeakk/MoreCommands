package com.ptsmods.morecommands.client.miscellaneous;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ptsmods.morecommands.api.util.compat.Compat;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Huge thanks to Industrial Foregoing for this particle. :3
// ~~I only changed the colour the lines are and the class name and ported it to yarn mappings.~~
// Actually, it's no longer Yarn mappings, and I've since then
// And I fixed the rendering since that wasn't working properly. (See the if-statement in the for-loop in #buildGeometry)
// https://github.com/InnovativeOnlineIndustries/Industrial-Foregoing/blob/1.16/src/main/java/com/buuz135/industrial/proxy/client/particle/ParticleVex.java
// Only for the cool kids tho, have a look at MoreCommands#isCool.
public class VexParticle extends Particle {
    private static final Random random = new Random();
    public static final float r = 214f / 255, g = 104f / 255, b = 14f / 255;
    public static final ParticleRenderType prt = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder builder, TextureManager manager) {
            RenderType.lines().setupRenderState();

            builder.begin(RenderType.lines().mode(), RenderType.lines().format());
        }

        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();

            RenderType.lines().clearRenderState();
            // For any particles that render after the vex particles.
            // (This is the default shader for particles, and it is only
            // set once before the particles get rendered)
            RenderSystem.setShader(GameRenderer::getParticleShader);
        }
    };
    private final Entity entity;
    private final List<Direction> directions = new ArrayList<>();
    private final List<Vec3> lines = new ArrayList<>();
    private boolean isDying = false;

    public VexParticle(Entity entity) {
        super((ClientLevel) entity.level, entity.getX() + random.nextDouble() - 0.5, entity.getY() + 1f + random.nextInt(200) / 100f, entity.getZ() + random.nextDouble() - 0.5);
        this.entity = entity;

        Direction d = Compat.get().randomDirection();
        // Begin with a short single line in some arbitrary direction.
        directions.add(d);
        directions.add(d);

        calculateLines();

        lifetime = Integer.MAX_VALUE;
        gravity = 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (entity.position().distanceToSqr(x, y, z) > 2 && directions.size() >= 25 || age == 200) isDying = true;
        if (!isDying && !removed) {
            // Make the particle slowly grow over a period of 50 ticks (2.5 seconds).
            // After this time, the start will begin to deteriorate.
            directions.add(0, random.nextDouble() < 0.05 ? getRandomFacing(directions.get(0)) : directions.get(0));
            if (directions.size() > 50) directions.remove(50);

            Vec3 directionVector = Vec3.atLowerCornerOf(directions.get(0).getNormal()).scale(0.01);
            this.setPos(x - directionVector.x, y - directionVector.y, z - directionVector.z);
            calculateLines();
        } else {
            directions.remove(directions.size() - 1);
            calculateLines();
            if (directions.isEmpty()) this.remove();
        }
    }

    @Override
    public void render(VertexConsumer vertex, Camera camera, float tickDelta) {
        if (lines.size() <= 1 || // Can't render a point as a line
                entity instanceof LocalPlayer && Minecraft.getInstance().player == entity &&
                Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON &&
                entity.getEyePosition().distanceToSqr(x, y, z) < 3)
            return;

        PoseStack stack = new PoseStack();
        Vec3 cam = camera.getPosition();

        for (int i = 0; i < lines.size() - 1; i++) {
            stack.pushPose();
            Vec3 line = lines.get(i);
            Vec3 next = lines.get(i + 1);

            stack.translate(line.x - cam.x, line.y - cam.y, line.z - cam.z);
            PoseStack.Pose pose = stack.last();

            double dx = next.x - line.x;
            double dy = next.y - line.y;
            double dz = next.z - line.z;
            double length = Math.sqrt(dx * dx + dy * dy + dz * dz);

            float nx = (float) (dx / length);
            float ny = (float) (dy / length);
            float nz = (float) (dz / length);

            // For now, no lightmap.
            // Since 1.17, lines now need a normal in order to actually render,
            // there is, however, no shader and no vertex format that uses
            // position, color, normal and lightmap which means that either,
            // we have no lightmap (which means the particle will look bright in the dark)
            // or I make my own shader.
            // Currently not feeling like the latter, so we'll leave it at this.
            vertex.vertex(pose.pose(), 0, 0, 0)
                    .color(r, g, b, 1f)
                    .normal(pose.normal(), nx, ny, nz)
//                    .uv2(getLightColor(tickDelta))
                    .endVertex();

            vertex.vertex(pose.pose(), (float) dx, (float) dy, (float) dz)
                    .color(r, g, b, 1f)
                    .normal(pose.normal(), nx, ny, nz)
//                    .uv2(getLightColor(tickDelta))
                    .endVertex();

            stack.popPose();
        }
    }

    @Override
    public void move(double d, double e, double f) {}

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return prt;
    }

    private Direction getRandomFacing(Direction opposite) {
        Direction facing;
        do facing = Compat.get().randomDirection();
        while (facing.getOpposite().equals(opposite));
        return facing;
    }

    private void calculateLines() {
        lines.clear();
        if (directions.size() == 0) return;

        Direction prev = directions.get(0);
        int currentPosition = 0;
        Vec3 prevPos = new Vec3(x, y, z);
        lines.add(prevPos);

        for (int i = 1; i < directions.size(); i++) {
            // If this is not the last element, and it's the same as the previous one,
            // we don't have to make a separate line for this one.
            if (directions.get(i).equals(prev) && i != directions.size() - 1) continue;

            // Get the normal of the previous direction, turn it into a Vec3 and scale it down.
            Vec3 directionVector = Vec3.atLowerCornerOf(prev.getNormal()).scale(0.01);

            // We may have skipped a couple elements (see if-statement at beginning of this loop),
            // this means that this line will be very short. Compensate for that by multiplying it
            // by the amount of elements we've skipped.
            int coe = i - currentPosition;
            lines.add(prevPos = prevPos.add(directionVector.multiply(coe, coe, coe)));

            prev = directions.get(i);
            currentPosition = i;
        }
    }
}
