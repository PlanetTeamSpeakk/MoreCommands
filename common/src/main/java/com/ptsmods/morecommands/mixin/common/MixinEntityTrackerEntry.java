package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.util.DataTrackerHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.server.network.EntityTrackerEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

//"net.minecraft.server.world.ThreadedAnvilChunkStorage$EntityTracker"
@Mixin(EntityTrackerEntry.class)
public class MixinEntityTrackerEntry {

	@Shadow @Final private Entity entity;

	@Inject(at = @At("HEAD"), method = "sendPackets(Ljava/util/function/Consumer;)V", cancellable = true)
	public void sendPackets(Consumer<Packet<?>> sender, CallbackInfo cbi) {
		if (entity instanceof PlayerEntity && entity.getDataTracker().get(DataTrackerHelper.VANISH)) cbi.cancel();
	}

}
