package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.api.IDataTrackerHelper;
import com.ptsmods.morecommands.mixin.common.accessor.MixinPlayerEntityAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerGameMode.class)
public class MixinServerPlayerInteractionManager {
    @Shadow protected ServerLevel level;
    @Final @Shadow protected ServerPlayer player;
    private @Unique boolean isFlying = false;

    @Inject(at = @At("HEAD"), method = "setGameModeForPlayer")
    public void setGameModePre(GameType gameMode, GameType gameMode2, CallbackInfo cbi) {
        isFlying = ((MixinPlayerEntityAccessor) player).getAbilities_().flying; // Making sure you don't fall down while flying when going from creative to survival or when joining.
    }

    @Inject(at = @At("TAIL"), method = "setGameModeForPlayer")
    public void setGameModePost(GameType gameMode, GameType gameMode2, CallbackInfo cbi) {
        // If MAY_FLY is false, let the gamemode decide whether the player may fly or not.
        // If we just straight up set it to the value of MAY_FLY, the player would never be able to fly, not even in creative, when flight is disabled with the /fly command even when you switch gamemode.
        // Spectators can always fly, they'll fall through the map otherwise.
        MixinPlayerEntityAccessor accessor = (MixinPlayerEntityAccessor) player;

        if (player.getEntityData().get(IDataTrackerHelper.get().mayFly()) || gameMode == GameType.SPECTATOR) accessor.getAbilities_().mayfly = true;
        if (accessor.getAbilities_().mayfly) accessor.getAbilities_().flying = isFlying;
        else accessor.getAbilities_().flying = false;
        if (player.getEntityData().get(IDataTrackerHelper.get().invulnerable())) accessor.getAbilities_().invulnerable = true;
        player.onUpdateAbilities();
    }
}
