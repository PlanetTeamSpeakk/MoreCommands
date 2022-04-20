package com.ptsmods.morecommands.mixin.compat.compat17plus;

import com.ptsmods.morecommands.commands.server.elevated.InvseeCommand;
import com.ptsmods.morecommands.api.ReflectionHelper;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public class MixinScreenHandler {
    @Inject(at = @At("HEAD"), method = "onSlotClick", cancellable = true)
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo cbi) {
        if (ReflectionHelper.<ScreenHandler>cast(this) instanceof InvseeCommand.InvSeeScreenHandler && ReflectionHelper.<InvseeCommand.InvSeeScreenHandler>cast(this).target instanceof OtherClientPlayerEntity) cbi.cancel();
    }
}
