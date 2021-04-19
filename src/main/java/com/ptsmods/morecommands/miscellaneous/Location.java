package com.ptsmods.morecommands.miscellaneous;

import com.google.common.base.MoreObjects;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Location<T extends World> {

	private final T world;
	private final Vec3d pos;
	private final Vec2f rot;

	public Location(T world, Vec3d pos, Vec2f rot) {
		this.world = world;
		this.pos = pos;
		this.rot = MoreObjects.firstNonNull(rot, Vec2f.ZERO);
	}

	public Location(T world, BlockPos pos, Vec2f rot) {
		this.world = world;
		this.pos = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
		this.rot = MoreObjects.firstNonNull(rot, Vec2f.ZERO);
	}

	public static Location<World> fromEntity(Entity entity) {
		return new Location<>(entity.getEntityWorld(), entity.getPos(), entity.getRotationClient());
	}

	public T getWorld() {
		return world;
	}

	public Vec3d getPos() {
		return pos;
	}

	public Vec2f getRot() {
		return rot;
	}

	@Override
	public String toString() {
		return "Location{" +
				"world=" + world +
				", pos=" + pos +
				", rot=" + rot +
				'}';
	}
}
