package com.ptsmods.morecommands.miscellaneous;

import com.mojang.blaze3d.systems.RenderSystem;
import com.ptsmods.morecommands.util.CompatHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

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
	public static final ParticleTextureSheet pts = new ParticleTextureSheet() {
		@Override
		public void begin(BufferBuilder builder, TextureManager manager) {
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(770, 771, 1, 0);
			RenderSystem.lineWidth(2.0F);
			RenderSystem.disableTexture();
			CompatHolder.getClientCompat().bufferBuilderBegin(builder, 3, VertexFormats.POSITION_COLOR_LIGHT);
		}

		@Override
		public void draw(Tessellator tessellator) {
			tessellator.draw();
			RenderSystem.disableBlend();
			RenderSystem.enableTexture();
		}
	};
	private final Entity entity;
	private final List<Direction> directions = new ArrayList<>();
	private final List<Vec3d> lines = new ArrayList<>();
	private boolean isDying = false;

	public VexParticle(Entity entity) {
		super((ClientWorld) entity.world, entity.getX() + random.nextDouble() - 0.5, entity.getY() + 1f + random.nextInt(200) / 100f, entity.getZ() + random.nextDouble() - 0.5);
		this.entity = entity;
		Direction prev = Direction.NORTH;
		directions.add(0, prev);
		for (int i = 1; i < 50; i++) {
			prev = directions.get(i - 1);
			directions.add(i, random.nextDouble() < 0.05 ? getRandomFacing(prev) : prev);
		}
		calculateLines();
		maxAge = Integer.MAX_VALUE;
	}

	@Override
	public void tick() {
		super.tick();
		if (entity.getPos().squaredDistanceTo(x, y, z) > 2 || age == 200) isDying = true;
		if (!isDying && !dead) {
			directions.add(0, random.nextDouble() < 0.05 ? getRandomFacing(directions.get(0)) : directions.get(0));
			directions.remove(50);
			Vec3d directionVector = new Vec3d(directions.get(0).getVector().getX(), directions.get(0).getVector().getY(), directions.get(0).getVector().getZ()).multiply(0.01);
			this.setPos(x - directionVector.x, y - directionVector.y, z - directionVector.z);
			calculateLines();
		} else {
			directions.remove(directions.size() - 1);
			calculateLines();
			if (directions.isEmpty()) this.markDead();
		}
	}

	@Override
	public void buildGeometry(VertexConsumer vertex, Camera camera, float tickDelta) {
		if (entity instanceof ClientPlayerEntity && MinecraftClient.getInstance().player == entity && MinecraftClient.getInstance().options.getPerspective() == Perspective.FIRST_PERSON && entity.getPos().add(0, 1, 0).squaredDistanceTo(x, y, z) < 3)
			return;
		Vec3d cam = camera.getPos();
		double x = entity.prevX + (cam.x - entity.prevX);
		double y = entity.prevY + (cam.y - entity.prevY);
		double z = entity.prevZ + (cam.z - entity.prevZ);
		for (int i = 0; i < lines.size(); i++) {
			Vec3d line = lines.get(i);
			vertex.vertex(line.x - x, line.y - y, line.z - z).color(r, g, b, 0f).light(240, 240).next();
			if (i != lines.size() - 1) {
				line = lines.get(i + 1);
				vertex.vertex(line.x - x, line.y - y, line.z - z).color(r, g, b, 1f).light(240, 240).next();
			}
		}
	}

	@Override
	public ParticleTextureSheet getType() {
		return pts;
	}

	private Direction getRandomFacing(Direction opposite) {
		Direction facing = CompatHolder.getCompat().randomDirection();
		while (facing.getOpposite().equals(opposite)) facing = CompatHolder.getCompat().randomDirection();
		return facing;
	}

	private void calculateLines() {
		lines.clear();
		if (directions.size() == 0) return;
		Direction prev = directions.get(0);
		int currentPosition = 0;
		Vec3d prevBlockPos = new Vec3d(x, y, z);
		lines.add(prevBlockPos);
		for (int i = 1; i < directions.size(); i++) {
			if (!directions.get(i).equals(prev) || i == directions.size() - 1) {
				Vec3d directionVector = new Vec3d(prev.getVector().getX(), prev.getVector().getY(), prev.getVector().getZ()).multiply(0.01);
				Vec3d endBlockPos = new Vec3d(prevBlockPos.x + directionVector.x * (i - currentPosition), prevBlockPos.y + directionVector.y * (i - currentPosition), prevBlockPos.z + directionVector.z * (i - currentPosition));
				lines.add(endBlockPos);
				prev = directions.get(i);
				currentPosition = i;
				prevBlockPos = endBlockPos;
			}
		}
	}
}