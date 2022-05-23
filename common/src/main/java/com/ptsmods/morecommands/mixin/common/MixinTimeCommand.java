package com.ptsmods.morecommands.mixin.common;

import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TimeCommand;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TimeCommand.class)
public class MixinTimeCommand {
    // Applies a fix so the time immediately updates on clients once the command is run.
    @Inject(at = @At("RETURN"), method = "executeSet(Lnet/minecraft/server/command/ServerCommandSource;I)I")
    private static void executeSet(ServerCommandSource source, int time, CallbackInfoReturnable<Integer> cbi) {
        source.getServer().getPlayerManager().sendToAll(new WorldTimeUpdateS2CPacket(source.getWorld().getTime(), source.getWorld().getTimeOfDay(), source.getWorld().getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)));
    }
}
