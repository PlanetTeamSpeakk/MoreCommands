package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public class MixinAbstractBlock {

    @Inject(at = @At("RETURN"), method = "getCollisionShape", cancellable = true)
    public void getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cbi) {
        cbi.setReturnValue(ClientOptions.Cheats.collideAll.getValue() && MoreCommands.isSingleplayer() && cbi.getReturnValue().isEmpty() && !"INSTANCE".equalsIgnoreCase(String.valueOf(world)) ?
                state.getShape(world, pos, context) : cbi.getReturnValue());
    }
}
