package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(LiquidBlock.class)
public class MixinFluidBlock {
    /**
     * @author PlanetTeamSpeak
     * @reason We give fluids an outline shape when the targetFluids client tweak option is enabled.
     */
    @Overwrite
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return ClientOptions.Tweaks.targetFluids.getValue() ? MoreCommands.getFluidShape(state) : Shapes.empty();
    }
}