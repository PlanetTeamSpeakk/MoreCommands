package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.miscellaneous.ClientOptions;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(FluidBlock.class)
public class MixinFluidBlock {

    @Overwrite
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return ClientOptions.Tweaks.targetFluids ? VoxelShapes.cuboid(0, 0, 0, 1, 1d/1.125d/8*(8-state.get(FluidBlock.LEVEL)), 1) : VoxelShapes.empty();
    }

}
