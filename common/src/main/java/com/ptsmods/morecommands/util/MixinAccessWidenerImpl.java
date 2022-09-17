package com.ptsmods.morecommands.util;

import com.ptsmods.morecommands.api.MixinAccessWidener;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import com.ptsmods.morecommands.mixin.common.accessor.MixinServerPlayerEntityAccessor;
import com.ptsmods.morecommands.mixin.common.accessor.MixinSignBlockEntityAccessor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class MixinAccessWidenerImpl implements MixinAccessWidener {
    private static boolean ignoreInteract;

    @Override
    public void serverPlayerEntity$setSyncedExperience(ServerPlayer player, int experience) {
        ((MixinServerPlayerEntityAccessor) player).setLastSentExp(experience);
    }

    @Override
    public char serverPlayNetworkHandler$gameMsgCharAt(ServerGamePacketListenerImpl thiz, String string, int index, ServerPlayer player, MinecraftServer server) {
        char ch = string.charAt(index);
        if (!string.startsWith("/") && ch == '\u00A7' && (MoreGameRules.get().checkBooleanWithPerm(thiz.player.level.getGameRules(), MoreGameRules.get().doChatColoursRule(), thiz.player)
                || player.hasPermissions(server.getOperatorUserPermissionLevel()))) ch = '&';
        return ch;
    }

    @Override
    public Component[] signBlockEntity$getTexts(SignBlockEntity sbe) {
        return ((MixinSignBlockEntityAccessor) sbe).getMessages();
    }

    @Override
    public void doMultiDoorInteract(MultiPlayerGameMode interactionManager, LocalPlayer player, ClientLevel world, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cbi) {
        if (ClientOptions.Tweaks.openDoubleDoors.getValue() && interactionManager.getPlayerMode() != GameType.SPECTATOR && cbi.getReturnValue().consumesAction() &&
                Compat.get().tagContains(new ResourceLocation("minecraft:wooden_doors"), world.getBlockState(hit.getBlockPos()).getBlock())) {
            if (ignoreInteract) {
                ignoreInteract = false;
                return;
            }
            DoorHingeSide hinge = world.getBlockState(hit.getBlockPos()).getValue(DoorBlock.HINGE);
            Direction facing = world.getBlockState(hit.getBlockPos()).getValue(DoorBlock.FACING);
            BlockPos.MutableBlockPos other = hit.getBlockPos().mutable();
            Vec3 pos = hit.getLocation();
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
            BlockState state = world.getBlockState(hit.getBlockPos());
            BlockState state0 = world.getBlockState(other);
            if (Compat.get().tagContains(new ResourceLocation("minecraft:wooden_doors"), state0.getBlock()) && state0.getValue(DoorBlock.FACING) == state.getValue(DoorBlock.FACING) &&
                    state0.getValue(DoorBlock.HINGE) != state.getValue(DoorBlock.HINGE) && state0.getValue(DoorBlock.OPEN) != state.getValue(DoorBlock.OPEN)) {
                // Open must not be equal cuz the other door already got opened at this stage.
                ignoreInteract = true;
                ClientCompat.get().interactBlock(interactionManager, player, world, hand, new BlockHitResult(pos, hit.getDirection(), other, hit.isInside()));
            }
        }
    }
}
