package com.ptsmods.morecommands.mixin.common;

import net.minecraft.block.Blocks;
import net.minecraft.block.WallRedstoneTorchBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(WallRedstoneTorchBlock.class)
public class MixinWallRedstoneTorchBlock {
	/**
	 * @author PlanetTeamSpeak
	 * @reason Causes StackOverflowErrors otherwise
	 */
	@Overwrite
	public String getTranslationKey() {
		return Blocks.REDSTONE_TORCH.getTranslationKey();
	}
}
