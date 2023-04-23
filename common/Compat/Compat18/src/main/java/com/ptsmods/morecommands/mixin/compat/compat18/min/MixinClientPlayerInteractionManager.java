package com.ptsmods.morecommands.mixin.compat.compat18.min;

import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.clientoptions.ClientOption;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MixinClientPlayerInteractionManager {
    private @Unique boolean ignoreInteract;

    @Inject(at = @At("RETURN"), method = "useItemOn")
    public void interactBlock(LocalPlayer player, ClientLevel world, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cbi) {
        MultiPlayerGameMode thiz = ReflectionHelper.cast(this);
        if (ignoreInteract || !ClientOption.getBoolean("openDoubleDoors") || thiz.getPlayerMode() == GameType.SPECTATOR || !cbi.getReturnValue().consumesAction() ||
                !Compat.get().tagContains(new ResourceLocation("minecraft:wooden_doors"), world.getBlockState(hit.getBlockPos()).getBlock()))
            return;

        DoorHingeSide hinge = world.getBlockState(hit.getBlockPos()).getValue(DoorBlock.HINGE);
        Direction facing = world.getBlockState(hit.getBlockPos()).getValue(DoorBlock.FACING);
        BlockPos.MutableBlockPos other = hit.getBlockPos().mutable();
        Vec3 pos = hit.getLocation();
        switch (facing) {
            case NORTH -> {
                switch (hinge) {
                    case LEFT -> {
                        other.move(Direction.EAST);
                        pos = pos.add(1, 0, 0);
                    }
                    case RIGHT -> {
                        other.move(Direction.WEST);
                        pos = pos.add(-1, 0, 0);
                    }
                }
            }
            case SOUTH -> {
                switch (hinge) {
                    case LEFT -> {
                        other.move(Direction.WEST);
                        pos = pos.add(-1, 0, 0);
                    }
                    case RIGHT -> {
                        other.move(Direction.EAST);
                        pos = pos.add(1, 0, 0);
                    }
                }
            }
            case EAST -> {
                switch (hinge) {
                    case LEFT -> {
                        other.move(Direction.SOUTH);
                        pos = pos.add(0, 0, 1);
                    }
                    case RIGHT -> {
                        other.move(Direction.NORTH);
                        pos = pos.add(0, 0, -1);
                    }
                }
            }
            case WEST -> {
                switch (hinge) {
                    case LEFT -> {
                        other.move(Direction.NORTH);
                        pos = pos.add(0, 0, -1);
                    }
                    case RIGHT -> {
                        other.move(Direction.SOUTH);
                        pos = pos.add(0, 0, 1);
                    }
                }
            }
        }

        BlockState state = world.getBlockState(hit.getBlockPos());
        BlockState state0 = world.getBlockState(other);
        if (Compat.get().tagContains(new ResourceLocation("minecraft:wooden_doors"), state0.getBlock()) && state0.getValue(DoorBlock.FACING) == state.getValue(DoorBlock.FACING) &&
                state0.getValue(DoorBlock.HINGE) != state.getValue(DoorBlock.HINGE) && state0.getValue(DoorBlock.OPEN) != state.getValue(DoorBlock.OPEN)) {
            // Open must not be equal cuz the other door already got opened at this stage.
            ignoreInteract = true;
            ClientCompat.get().interactBlock(thiz, player, world, hand, new BlockHitResult(pos, hit.getDirection(), other, hit.isInside()));
            ignoreInteract = false;
        }
    }
}
