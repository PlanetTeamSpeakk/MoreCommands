package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Commands.class)
public class MixinCommandManagerCarpetCompat {
    private @Unique CommandSourceStack lastCommandSource = null;

    @Inject(at = @At("HEAD"), method = "performCommand")
    private void onExecute(CommandSourceStack commandSource, String command, CallbackInfoReturnable<Integer> cbi) {
        lastCommandSource = commandSource;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;isDebugEnabled()Z", remap = false), method = "performCommand", require = 0)
    private boolean execute_isDebugEnabled(Logger logger) {
        return lastCommandSource.getLevel() != null && lastCommandSource.getLevel().getGameRules().getBoolean(MoreGameRules.get().doStacktraceRule()) || logger.isDebugEnabled();
    }
}
