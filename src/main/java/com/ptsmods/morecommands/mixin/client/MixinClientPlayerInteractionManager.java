package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.ClientOptions;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.PickaxeItem;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Shadow @Final private ClientPlayNetworkHandler networkHandler;
    @Shadow private int blockBreakingCooldown;
    @Shadow private GameMode gameMode;
    private boolean mc_ignore = false;

    @Inject(at = @At("RETURN"), method = "attackBlock(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z")
    public boolean attackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cbi) {
        return mc_updateAndReturn(cbi);
    }

    @Inject(at = @At("RETURN"), method = "updateBlockBreakingProgress(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z")
    public boolean updateBlockBreakingProgress(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cbi) {
        return mc_updateAndReturn(cbi);
    }

    private boolean mc_updateAndReturn(CallbackInfoReturnable<Boolean> cbi) {
        if (gameMode.isCreative() && MinecraftClient.getInstance().player.getDataTracker().get(MoreCommands.SUPERPICKAXE) && MinecraftClient.getInstance().player.getMainHandStack().getItem() instanceof PickaxeItem) blockBreakingCooldown = 0;
        return cbi.getReturnValue();
    }

    @Inject(at = @At("RETURN"), method = "interactBlock(Lnet/minecraft/client/network/ClientPlayerEntity;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;")
    public ActionResult interactBlock(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cbi) {
        if (ClientOptions.Tweaks.openDoubleDoors && gameMode != GameMode.SPECTATOR && cbi.getReturnValue().isAccepted() && BlockTags.WOODEN_DOORS.contains(world.getBlockState(hitResult.getBlockPos()).getBlock())) {
            if (mc_ignore) {
                mc_ignore = false;
                return cbi.getReturnValue();
            }
            DoorHinge hinge = world.getBlockState(hitResult.getBlockPos()).get(DoorBlock.HINGE);
            Direction facing = world.getBlockState(hitResult.getBlockPos()).get(DoorBlock.FACING);
            BlockPos.Mutable other = hitResult.getBlockPos().mutableCopy();
            Vec3d pos = hitResult.getPos();
            switch (facing) {
                case NORTH:
                    switch (hinge) {
                        case LEFT:
                            other.move(Direction.EAST);
                            pos = pos.add(1, 0, 0);
                            break;
                        case RIGHT:
                            other.move(Direction.WEST);
                            pos = pos.add(-1, 0, 0);
                            break;
                    }
                    break;
                case SOUTH:
                    switch (hinge) {
                        case LEFT:
                            other.move(Direction.WEST);
                            pos = pos.add(-1, 0, 0);
                            break;
                        case RIGHT:
                            other.move(Direction.EAST);
                            pos = pos.add(1, 0, 0);
                            break;
                    }
                    break;
                case EAST:
                    switch (hinge) {
                        case LEFT:
                            other.move(Direction.SOUTH);
                            pos = pos.add(0, 0, 1);
                            break;
                        case RIGHT:
                            other.move(Direction.NORTH);
                            pos = pos.add(0, 0, -1);
                            break;
                    }
                    break;
                case WEST:
                    switch (hinge) {
                        case LEFT:
                            other.move(Direction.NORTH);
                            pos = pos.add(0, 0, -1);
                            break;
                        case RIGHT:
                            other.move(Direction.SOUTH);
                            pos = pos.add(0, 0, 1);
                            break;
                    }
                    break;
            }
            BlockState state = world.getBlockState(hitResult.getBlockPos());
            BlockState state0 = world.getBlockState(other);
            if (BlockTags.WOODEN_DOORS.contains(state0.getBlock()) && state0.get(DoorBlock.FACING) == state.get(DoorBlock.FACING) && state0.get(DoorBlock.HINGE) != state.get(DoorBlock.HINGE) && state0.get(DoorBlock.OPEN) != state.get(DoorBlock.OPEN)) { // Open must not be equal cuz the other door already got opened at this stage.
                mc_ignore = true;
                ReflectionHelper.<ClientPlayerInteractionManager>cast(this).interactBlock(player, world, hand, new BlockHitResult(pos, hitResult.getSide(), other, hitResult.isInsideBlock()));
            }
        }
        return cbi.getReturnValue();
    }

    @Inject(at = @At("RETURN"), method = "isFlyingLocked()Z")
    public boolean isFlyingLocked(CallbackInfoReturnable<Boolean> cbi) {
        return cbi.getReturnValueZ() || ClientOptions.Tweaks.lockFlying;
    }

}
