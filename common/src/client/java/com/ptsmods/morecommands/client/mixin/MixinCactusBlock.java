package com.ptsmods.morecommands.client.mixin;

import com.ptsmods.morecommands.api.ClientOnly;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CactusBlock.class)
public class MixinCactusBlock {
    @Unique private static final VoxelShape OUTLINE_SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    @Inject(at = @At("RETURN"), method = "getCollisionShape", cancellable = true)
    public void getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cbi) {
        // When String representation of the world is 'INSTANCE', the game is still initialising, so we pass the original shape.
        cbi.setReturnValue(ClientOptions.Cheats.avoidCactusDmg.getValue() && ClientOnly.get().isSingleplayer() &&
                !"INSTANCE".equalsIgnoreCase(String.valueOf(world)) ? OUTLINE_SHAPE : cbi.getReturnValue());
    }
}
