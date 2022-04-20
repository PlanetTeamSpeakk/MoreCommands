package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandManager.class)
public class MixinCommandManagerCarpetCompat {
	private @Unique ServerCommandSource lastCommandSource = null;

	@Inject(at = @At("HEAD"), method = "execute")
	private void onExecute(ServerCommandSource commandSource, String command, CallbackInfoReturnable<Integer> cbi) {
		lastCommandSource = commandSource;
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;isDebugEnabled()Z", remap = false), method = "execute", require = 0)
	private boolean execute_isDebugEnabled(Logger logger) {
		return lastCommandSource.getWorld() != null && lastCommandSource.getWorld().getGameRules().getBoolean(MoreGameRules.doStacktraceRule) || logger.isDebugEnabled();
	}
}
