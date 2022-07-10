package com.ptsmods.morecommands.miscellaneous;

import com.ptsmods.morecommands.api.util.extensions.ObjectExtensions;
import lombok.experimental.ExtensionMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

@ExtensionMethod(ObjectExtensions.class)
public class Location<T extends Level> {

    private final T world;
    private final Vec3 pos;
    private final Vec2 rot;

    public Location(T world, Vec3 pos, Vec2 rot) {
        this.world = world;
        this.pos = pos;
        this.rot = rot.or(Vec2.ZERO);
    }

    public Location(T world, BlockPos pos, Vec2 rot) {
        this.world = world;
        this.pos = new Vec3(pos.getX(), pos.getY(), pos.getZ());
        this.rot = rot.or(Vec2.ZERO);
    }

    public static Location<Level> fromEntity(Entity entity) {
        return new Location<>(entity.getCommandSenderWorld(), entity.position(), entity.getRotationVector());
    }

    public T getWorld() {
        return world;
    }

    public Vec3 getPos() {
        return pos;
    }

    public Vec2 getRot() {
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
