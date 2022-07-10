package com.ptsmods.morecommands.mixin.common;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedstoneWallTorchBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(RedstoneWallTorchBlock.class)
public class MixinWallRedstoneTorchBlock {
    /**
     * @author PlanetTeamSpeak
     * @reason Causes StackOverflowErrors otherwise
     */
    @Overwrite
    public String getDescriptionId() {
        return Blocks.REDSTONE_TORCH.getDescriptionId();
    }
}
