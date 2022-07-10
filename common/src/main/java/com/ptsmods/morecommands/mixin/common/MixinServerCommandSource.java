package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CommandSourceStack.class)
public abstract class MixinServerCommandSource {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/CommandSourceStack;broadcastToAdmins(Lnet/minecraft/network/chat/Component;)V"), method = "sendSuccess")
    private void sendFeedback_sendToOps(CommandSourceStack source, Component message) {
        if (source.getLevel() == null || MoreGameRules.get().checkBooleanWithPerm(source.getLevel().getGameRules(), MoreGameRules.get().sendCommandFeedbackToOpsRule(), source.getEntity()))
            broadcastToAdmins(Compat.get().builderFromText(message).withStyle(Style.EMPTY).build());
    }

    @Shadow private void broadcastToAdmins(Component message) {}
}
