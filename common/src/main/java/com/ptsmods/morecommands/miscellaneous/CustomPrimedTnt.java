package com.ptsmods.morecommands.miscellaneous;

import lombok.Getter;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class CustomPrimedTnt extends PrimedTnt {
    @Getter
    private final float power;

    public CustomPrimedTnt(Level level, double x, double y, double z, @Nullable LivingEntity owner,
                           float power, int fuse) {
        super(level, x, y, z, owner);
        this.power = power;
        setFuse(fuse);
    }
}
