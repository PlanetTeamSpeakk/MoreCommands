package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public class MixinAbstractBlock {

    @Inject(at = @At("RETURN"), method = "getCollisionShape(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", cancellable = true)
    public void getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cbi) {
        cbi.setReturnValue(ClientOptions.Cheats.collideAll.getValue() && MoreCommands.isSingleplayer() && cbi.getReturnValue().isEmpty() && !"INSTANCE".equalsIgnoreCase(String.valueOf(world)) ?
                state.getOutlineShape(world, pos, context) : cbi.getReturnValue());
    }
}
