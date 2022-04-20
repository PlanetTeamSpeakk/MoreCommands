package com.ptsmods.morecommands.compat;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;

import java.util.stream.DoubleStream;

public class Compat18 extends Compat17 {
	@Override
	public NbtCompound writeSpawnerLogicNbt(MobSpawnerLogic logic, World world, BlockPos pos, NbtCompound nbt) {
		return logic.writeNbt(nbt);
	}

	@Override
	public NbtCompound writeBENBT(BlockEntity be) {
		return be.createNbtWithIdentifyingData();
	}

	@Override
	public DoubleStream doubleStream(DoubleList doubles) {
		return doubles.doubleStream();
	}
}
