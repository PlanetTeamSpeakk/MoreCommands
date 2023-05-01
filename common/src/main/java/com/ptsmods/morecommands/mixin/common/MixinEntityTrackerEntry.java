package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.api.IDataTrackerHelper;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

//"net.minecraft.server.world.ThreadedAnvilChunkStorage$EntityTracker"
@Mixin(ServerEntity.class)
public class MixinEntityTrackerEntry {

    @Shadow @Final private Entity entity;

    @Inject(at = @At("HEAD"), method = "sendPairingData", cancellable = true)
    public void sendPackets(Consumer<Packet<?>> sender, CallbackInfo cbi) {
        if (entity instanceof Player && entity.getEntityData().get(IDataTrackerHelper.get().vanish())) cbi.cancel();
    }
}
