package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.commands.server.elevated.ReachCommand;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ServerPlayerInteractionManager.class)
public class MixinServerPlayerInteractionManager {

    @Shadow private static Logger LOGGER;
    @Shadow public ServerWorld world;
    @Shadow public ServerPlayerEntity player;
    @Shadow private GameMode gameMode;
    @Shadow private boolean mining;
    @Shadow private BlockPos miningPos;
    @Shadow private int startMiningTime;
    @Shadow private int tickCounter;
    @Shadow private boolean failedToMine;
    @Shadow private BlockPos failedMiningPos;
    @Shadow private int failedStartMiningTime;
    @Shadow private int blockBreakingProgress;
    private boolean mc_isFlying = false;

    @Overwrite
    public void processBlockBreakingAction(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight) {
        double d = player.getX() - ((double)pos.getX() + 0.5D);
        double e = player.getY() - ((double)pos.getY() + 0.5D) + 1.5D;
        double f = player.getZ() - ((double)pos.getZ() + 0.5D);
        double g = d * d + e * e + f * f;
        if (g > ReachCommand.getReach(player, true)) { // The only line I changed. lulw
            player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(pos, world.getBlockState(pos), action, false, "too far"));
        } else if (pos.getY() >= worldHeight) {
            player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(pos, world.getBlockState(pos), action, false, "too high"));
        } else {
            BlockState blockState;
            if (action == net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action.START_DESTROY_BLOCK) {
                if (!world.canPlayerModifyAt(player, pos)) {
                    player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(pos, world.getBlockState(pos), action, false, "may not interact"));
                    return;
                }

                if (player.interactionManager.isCreative()) {
                    player.interactionManager.finishMining(pos, action, "creative destroy");
                    return;
                }

                if (player.isBlockBreakingRestricted(world, pos, gameMode)) {
                    player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(pos, world.getBlockState(pos), action, false, "block action restricted"));
                    return;
                }

                startMiningTime = tickCounter;
                float h = 1.0F;
                blockState = world.getBlockState(pos);
                if (!blockState.isAir()) {
                    blockState.onBlockBreakStart(world, pos, player);
                    h = blockState.calcBlockBreakingDelta(player, player.world, pos);
                }

                if (!blockState.isAir() && h >= 1.0F) {
                    player.interactionManager.finishMining(pos, action, "insta mine");
                } else {
                    if (mining) {
                        player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(miningPos, world.getBlockState(miningPos), PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, false, "abort destroying since another started (client insta mine, server disagreed)"));
                    }

                    mining = true;
                    miningPos = pos.toImmutable();
                    int i = (int)(h * 10.0F);
                    world.setBlockBreakingInfo(player.getEntityId(), pos, i);
                    player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(pos, world.getBlockState(pos), action, true, "actual start of destroying"));
                    blockBreakingProgress = i;
                }
            } else if (action == PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK) {
                if (pos.equals(miningPos)) {
                    int j = tickCounter - startMiningTime;
                    blockState = world.getBlockState(pos);
                    if (!blockState.isAir()) {
                        float k = blockState.calcBlockBreakingDelta(player, player.world, pos) * (float)(j + 1);
                        if (k >= 0.7F) {
                            mining = false;
                            world.setBlockBreakingInfo(player.getEntityId(), pos, -1);
                            player.interactionManager.finishMining(pos, action, "destroyed");
                            return;
                        }

                        if (!failedToMine) {
                            mining = false;
                            failedToMine = true;
                            failedMiningPos = pos;
                            failedStartMiningTime = startMiningTime;
                        }
                    }
                }

                player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(pos, world.getBlockState(pos), action, true, "stopped destroying"));
            } else if (action == net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK) {
                mining = false;
                if (!Objects.equals(miningPos, pos)) {
                    LOGGER.warn("Mismatch in destroy block pos: " + miningPos + " " + pos);
                    world.setBlockBreakingInfo(player.getEntityId(), miningPos, -1);
                    player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(miningPos, world.getBlockState(miningPos), action, true, "aborted mismatched destroying"));
                }
                world.setBlockBreakingInfo(player.getEntityId(), pos, -1);
                player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(pos, world.getBlockState(pos), action, true, "aborted destroying"));
            }

        }
    }

    // Preventing packet from being sent
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity; sendAbilitiesUpdate()V"), method = "setGameMode(Lnet/minecraft/world/GameMode;Lnet/minecraft/world/GameMode;)V")
    public void sendAbilitiesUpdate(ServerPlayerEntity player) {}

    @Inject(at = @At("HEAD"), method = "setGameMode(Lnet/minecraft/world/GameMode;Lnet/minecraft/world/GameMode;)V")
    public void setGameModePre(GameMode gameMode, GameMode gameMode2, CallbackInfo cbi) {
        mc_isFlying = player.abilities.flying; // Making sure you don't fall down while flying when going from creative to survival or when joining.
    }

    @Inject(at = @At("TAIL"), method = "setGameMode(Lnet/minecraft/world/GameMode;Lnet/minecraft/world/GameMode;)V")
    public void setGameModePost(GameMode gameMode, GameMode gameMode2, CallbackInfo cbi) {
        // If MAY_FLY is false, let the gamemode decide whether the player may fly or not.
        // If we just straight up set it to the value of MAY_FLY, the player would never be able to fly, not even in creative, when flight is disabled with the /fly command even when you switch gamemode.
        // Spectators can always fly, they'll fall through the map otherwise.
        if (player.getDataTracker().get(MoreCommands.MAY_FLY) || gameMode == GameMode.SPECTATOR) player.abilities.allowFlying = true;
        if (player.abilities.allowFlying) player.abilities.flying = mc_isFlying;
        else player.abilities.flying = false;
        if (player.getDataTracker().get(MoreCommands.INVULNERABLE)) player.abilities.invulnerable = true;
        player.sendAbilitiesUpdate();
    }

}
