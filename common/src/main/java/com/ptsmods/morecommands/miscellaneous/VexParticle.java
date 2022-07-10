package com.ptsmods.morecommands.miscellaneous;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Huge thanks to Industrial Foregoing for this particle. :3
// I only changed the colour the lines are and the class name and ported it to yarn mappings.
// And I fixed the rendering since that wasn't working properly. (See the if-statement in the for-loop in #buildGeometry)
// https://github.com/InnovativeOnlineIndustries/Industrial-Foregoing/blob/1.16/src/main/java/com/buuz135/industrial/proxy/client/particle/ParticleVex.java
// Only for the cool kids tho, have a look at MoreCommands#isCool.
//
// Currently not working for whatever reason, already looked at what Industrial Foregoing
// changed, but that didn't fix anything here.
public class VexParticle extends Particle {
    private static final Random random = new Random();
    public static final float r = 214f / 255, g = 104f / 255, b = 14f / 255;
    public static final ParticleRenderType pts = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder builder, TextureManager manager) {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(770, 771, 1, 0);
            RenderSystem.lineWidth(2.0F);
            RenderSystem.disableTexture();
            ClientCompat.get().bufferBuilderBegin(builder, 3, DefaultVertexFormat.POSITION_COLOR_LIGHTMAP);
        }

        @Override
        public void end(Tesselator tessellator) {
            tessellator.end();
            RenderSystem.disableBlend();
            RenderSystem.enableTexture();
        }
    };
    private final Entity entity;
    private final List<Direction> directions = new ArrayList<>();
    private final List<Vec3> lines = new ArrayList<>();
    private boolean isDying = false;

    public VexParticle(Entity entity) {
        super((ClientLevel) entity.level, entity.getX() + random.nextDouble() - 0.5, entity.getY() + 1f + random.nextInt(200) / 100f, entity.getZ() + random.nextDouble() - 0.5);
        this.entity = entity;
        Direction prev = Direction.NORTH;
        directions.add(0, prev);
        for (int i = 1; i < 50; i++) {
            prev = directions.get(i - 1);
            directions.add(i, random.nextDouble() < 0.05 ? getRandomFacing(prev) : prev);
        }
        calculateLines();
        lifetime = Integer.MAX_VALUE;
    }

    @Override
    public void tick() {
        super.tick();
        if (entity.position().distanceToSqr(x, y, z) > 2 || age == 200) isDying = true;
        if (!isDying && !removed) {
            directions.add(0, random.nextDouble() < 0.05 ? getRandomFacing(directions.get(0)) : directions.get(0));
            directions.remove(50);
            Vec3 directionVector = new Vec3(directions.get(0).getNormal().getX(), directions.get(0).getNormal().getY(), directions.get(0).getNormal().getZ()).scale(0.01);
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
        if (entity instanceof LocalPlayer && Minecraft.getInstance().player == entity && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON && entity.position().add(0, 1, 0).distanceToSqr(x, y, z) < 3)
            return;
        Vec3 cam = camera.getPosition();
        double x = entity.xo + (cam.x - entity.xo);
        double y = entity.yo + (cam.y - entity.yo);
        double z = entity.zo + (cam.z - entity.zo);
        for (int i = 0; i < lines.size(); i++) {
            Vec3 line = lines.get(i);
            vertex.vertex(line.x - x, line.y - y, line.z - z).color(r, g, b, 0f).uv2(240, 240).endVertex();
            if (i != lines.size() - 1) {
                line = lines.get(i + 1);
                vertex.vertex(line.x - x, line.y - y, line.z - z).color(r, g, b, 1f).uv2(240, 240).endVertex();
            }
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return pts;
    }

    private Direction getRandomFacing(Direction opposite) {
        Direction facing = Compat.get().randomDirection();
        while (facing.getOpposite().equals(opposite)) facing = Compat.get().randomDirection();
        return facing;
    }

    private void calculateLines() {
        lines.clear();
        if (directions.size() == 0) return;
        Direction prev = directions.get(0);
        int currentPosition = 0;
        Vec3 prevBlockPos = new Vec3(x, y, z);
        lines.add(prevBlockPos);
        for (int i = 1; i < directions.size(); i++) {
            if (!directions.get(i).equals(prev) || i == directions.size() - 1) {
                Vec3 directionVector = new Vec3(prev.getNormal().getX(), prev.getNormal().getY(), prev.getNormal().getZ()).scale(0.01);
                Vec3 endBlockPos = new Vec3(prevBlockPos.x + directionVector.x * (i - currentPosition), prevBlockPos.y + directionVector.y * (i - currentPosition), prevBlockPos.z + directionVector.z * (i - currentPosition));
                lines.add(endBlockPos);
                prev = directions.get(i);
                currentPosition = i;
                prevBlockPos = endBlockPos;
            }
        }
    }
}