package com.ptsmods.morecommands.mixin.common;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.server.commands.TimeCommand;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TimeCommand.class)
public class MixinTimeCommand {
    // Applies a fix so the time immediately updates on clients once the command is run.
    @Inject(at = @At("RETURN"), method = "setTime")
    private static void executeSet(CommandSourceStack source, int time, CallbackInfoReturnable<Integer> cbi) {
        source.getServer().getPlayerList().broadcastAll(new ClientboundSetTimePacket(source.getLevel().getGameTime(), source.getLevel().getDayTime(), source.getLevel().getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)));
    }
}
