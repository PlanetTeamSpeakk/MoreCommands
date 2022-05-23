package com.ptsmods.morecommands.util;

import com.ptsmods.morecommands.api.MixinAccessWidener;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import com.ptsmods.morecommands.mixin.common.accessor.MixinServerPlayerEntityAccessor;
import com.ptsmods.morecommands.mixin.common.accessor.MixinSignBlockEntityAccessor;
import com.ptsmods.morecommands.mixin.compat.compat19.plus.MixinArgumentTypesAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

public class MixinAccessWidenerImpl implements MixinAccessWidener {
    private static boolean ignoreInteract;

    @Override
    public Map<Class<?>, ?> argumentTypes$getClassMap() {
        return MixinArgumentTypesAccessor.getClassMap();
    }

    @Override
    public void serverPlayerEntity$setSyncedExperience(ServerPlayerEntity player, int experience) {
        ((MixinServerPlayerEntityAccessor) player).setSyncedExperience(experience);
    }

    @Override
    public char serverPlayNetworkHandler$gameMsgCharAt(ServerPlayNetworkHandler thiz, String string, int index, ServerPlayerEntity player, MinecraftServer server) {
        char ch = string.charAt(index);
        if (!string.startsWith("/") && ch == '\u00A7' && (MoreGameRules.get().checkBooleanWithPerm(thiz.player.world.getGameRules(), MoreGameRules.get().doChatColoursRule(), thiz.player)
                || player.hasPermissionLevel(server.getOpPermissionLevel()))) ch = '&';
        return ch;
    }

    @Override
    public Text[] signBlockEntity$getTexts(SignBlockEntity sbe) {
        return ((MixinSignBlockEntityAccessor) sbe).getTexts();
    }

    @Override
    public void doMultiDoorInteract(ClientPlayerInteractionManager interactionManager, ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cbi) {
        if (ClientOptions.Tweaks.openDoubleDoors.getValue() && interactionManager.getCurrentGameMode() != GameMode.SPECTATOR && cbi.getReturnValue().isAccepted() &&
                Compat.get().tagContains(new Identifier("minecraft:wooden_doors"), world.getBlockState(hit.getBlockPos()).getBlock())) {
            if (ignoreInteract) {
                ignoreInteract = false;
                return;
            }
            DoorHinge hinge = world.getBlockState(hit.getBlockPos()).get(DoorBlock.HINGE);
            Direction facing = world.getBlockState(hit.getBlockPos()).get(DoorBlock.FACING);
            BlockPos.Mutable other = hit.getBlockPos().mutableCopy();
            Vec3d pos = hit.getPos();
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
            if (Compat.get().tagContains(new Identifier("minecraft:wooden_doors"), state0.getBlock()) && state0.get(DoorBlock.FACING) == state.get(DoorBlock.FACING) &&
                    state0.get(DoorBlock.HINGE) != state.get(DoorBlock.HINGE) && state0.get(DoorBlock.OPEN) != state.get(DoorBlock.OPEN)) {
                // Open must not be equal cuz the other door already got opened at this stage.
                ignoreInteract = true;
                ClientCompat.get().interactBlock(interactionManager, player, world, hand, new BlockHitResult(pos, hit.getSide(), other, hit.isInsideBlock()));
            }
        }
    }
}
