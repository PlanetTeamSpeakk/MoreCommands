package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.clientoption.ClientOptions;
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
    /**
     * @author PlanetTeamSpeak
     * @reason We give fluids an outline shape when the targetFluids client tweak option is enabled.
     */
    @Overwrite
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return ClientOptions.Tweaks.targetFluids.getValue() ? MoreCommands.getFluidShape(state) : VoxelShapes.empty();
    }
}