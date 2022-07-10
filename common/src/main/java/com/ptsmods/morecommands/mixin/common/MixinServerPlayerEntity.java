package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.util.DataTrackerHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class MixinServerPlayerEntity {
    @Unique private int lastPing = -1;

    /**
     * @author PlanetTeamSpeak
     * @reason To account for nicknames.
     */
    @Nullable
    @Overwrite
    public Component getTabListDisplayName() {
        return ReflectionHelper.<ServerPlayer>cast(this).getDisplayName();
    }

    @Inject(at = @At("HEAD"), method = "doTick")
    public void playerTick(CallbackInfo cbi) {
        ServerPlayer thiz = ReflectionHelper.cast(this);
        if (thiz.latency != lastPing) {
            lastPing = thiz.latency;
            updateScoreForCriteria(MoreCommands.LATENCY, lastPing);
        }
    }

    @Shadow private void updateScoreForCriteria(ObjectiveCriteria criterion, int score) {
        throw new AssertionError("This should not happen.");
    }

    // First changing world, then teleporting to different position seeing as that's how it's done naturally when going through a portal.
    // Normal behaviour breaks the back command when using it twice in a row after going through a portal.
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;moveTo(DDDFF)V"), method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDFF)V")
    private void teleport_refreshPositionAndAngles(ServerPlayer thiz, double x, double y, double z, float yaw, float pitch, ServerLevel targetWorld, double x0, double y0, double z0, float yaw0, float pitch0) {
        Compat.get().playerSetWorld(thiz, targetWorld);
    }

    @SuppressWarnings({"unchecked", "rawtypes"}) // It is necessary in this case.
    @Inject(at = @At("RETURN"), method = "restoreFrom")
    private void copyFrom(ServerPlayer player, boolean alive, CallbackInfo cbi) {
        SynchedEntityData dataTrackerNew = ReflectionHelper.<ServerPlayer>cast(this).getEntityData();
        SynchedEntityData dataTrackerOld = player.getEntityData();

        for (DataTrackerHelper.DataTrackerEntry<?> dataEntry : DataTrackerHelper.getDataEntries(ReflectionHelper.<ServerPlayer>cast(this).getClass())) {
            EntityDataAccessor dataRaw = dataEntry.getData();
            dataTrackerNew.set(dataRaw, dataTrackerOld.get(dataRaw));
        }
    }
}
