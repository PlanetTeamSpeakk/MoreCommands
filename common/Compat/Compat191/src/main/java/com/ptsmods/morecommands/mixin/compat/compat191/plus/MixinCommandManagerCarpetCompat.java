package com.ptsmods.morecommands.mixin.compat.compat191.plus;

import com.ptsmods.morecommands.api.IMoreGameRules;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Commands.class)
public class MixinCommandManagerCarpetCompat {
    private @Unique CommandSourceStack lastCommandSource = null;

    @ModifyVariable(at = @At("STORE"), method = "performCommand")
    private CommandSourceStack performCommand_source(CommandSourceStack source) {
        return lastCommandSource = source;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;isDebugEnabled()Z", remap = false), method = "performCommand", require = 0)
    private boolean execute_isDebugEnabled(Logger logger) {
        return lastCommandSource.getLevel() != null && lastCommandSource.getLevel().getGameRules().getBoolean(IMoreGameRules.get().doStacktraceRule()) || logger.isDebugEnabled();
    }
}
