package com.ptsmods.morecommands.compat;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.stream.DoubleStream;

public class Compat18 extends Compat17 {
    @Override
    public CompoundTag writeBaseSpawnerNbt(BaseSpawner logic, Level world, BlockPos pos, CompoundTag nbt) {
        return logic.save(nbt);
    }

    @Override
    public CompoundTag writeBENBT(BlockEntity be) {
        return be.saveWithFullMetadata();
    }

    @Override
    public DoubleStream doubleStream(DoubleList doubles) {
        return doubles.doubleStream();
    }
}
