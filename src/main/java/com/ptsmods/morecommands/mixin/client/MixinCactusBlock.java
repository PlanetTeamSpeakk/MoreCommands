package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.miscellaneous.ClientOptions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CactusBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CactusBlock.class)
public class MixinCactusBlock {

	@Shadow @Final protected static VoxelShape COLLISION_SHAPE;
	private static final VoxelShape MC_OUTLINE_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 16, 16);

	@Inject(at = @At("RETURN"), method = "getCollisionShape(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;")
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cbi) {
		// When String representation of the world is 'INSTANCE', the game is still initialising, so we pass the original shape.
		return ClientOptions.Cheats.avoidCactusDmg.getValue() && MoreCommands.isSingleplayer() && !"INSTANCE".equalsIgnoreCase(String.valueOf(world)) ? MC_OUTLINE_SHAPE : cbi.getReturnValue();
	}

}
