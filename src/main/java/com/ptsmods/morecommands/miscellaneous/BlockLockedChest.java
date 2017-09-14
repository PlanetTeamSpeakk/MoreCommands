package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockLockedChest extends Block {

	public BlockLockedChest() {
		this(Material.WOOD);
		setSoundType(SoundType.WOOD);
	}

	private BlockLockedChest(Material materialIn) {
		super(materialIn);
	}

}
