package com.ptsmods.morecommands.compat;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.stream.DoubleStream;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class Compat18 extends Compat17 {
    @Override
    public CompoundTag writeSpawnerLogicNbt(BaseSpawner logic, Level world, BlockPos pos, CompoundTag nbt) {
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
