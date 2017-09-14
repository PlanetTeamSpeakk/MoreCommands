package com.ptsmods.morecommands.miscellaneous;

import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSnow extends net.minecraft.block.BlockSnow {

	public BlockSnow() {
		super();
		setSoundType(SoundType.SNOW);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (worldIn.getGameRules().getBoolean("doMeltBlocks")) super.updateTick(worldIn, pos, state, rand);
	}

}
