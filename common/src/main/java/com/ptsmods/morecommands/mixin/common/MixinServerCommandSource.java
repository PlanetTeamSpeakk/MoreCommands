package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerCommandSource.class)
public abstract class MixinServerCommandSource {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/ServerCommandSource;sendToOps(Lnet/minecraft/text/Text;)V"), method = "sendFeedback")
    private void sendFeedback_sendToOps(ServerCommandSource source, Text message) {
        if (source.getWorld() == null || MoreGameRules.get().checkBooleanWithPerm(source.getWorld().getGameRules(), MoreGameRules.get().sendCommandFeedbackToOpsRule(), source.getEntity()))
            sendToOps(Compat.get().builderFromText(message).withStyle(Style.EMPTY).build());
    }

    @Shadow private void sendToOps(Text message) {}
}
