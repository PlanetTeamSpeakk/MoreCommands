package com.ptsmods.morecommands.miscellaneous;

import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockIce extends net.minecraft.block.BlockIce {

	public BlockIce() {
		super();
		setSoundType(SoundType.GLASS);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if (world.getGameRules().getBoolean("doMeltBlocks")) super.updateTick(world, pos, state, rand);
	}

	@Override
	public void turnIntoWater(World world, BlockPos pos) {
		if (world.getGameRules().getBoolean("doMeltBlocks")) super.turnIntoWater(world, pos);
	}

}
