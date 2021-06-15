package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.callbacks.CommandsRegisteredCallback;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandManager.class)
public class MixinCommandManager {
	private @Unique ServerCommandSource lastCommandSource = null;

	@Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/server/command/CommandManager$RegistrationEnvironment;)V")
	private void init(CommandManager.RegistrationEnvironment environment, CallbackInfo cbi) {
		CommandsRegisteredCallback.EVENT.invoker().onRegistered(ReflectionHelper.<CommandManager>cast(this).getDispatcher());
	}

	@Inject(at = @At("HEAD"), method = "execute")
	private void onExecute(ServerCommandSource commandSource, String command, CallbackInfoReturnable<Integer> cir) {
		lastCommandSource = commandSource;
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;isDebugEnabled()Z"), method = "execute")
	private boolean execute_isDebugEnabled(Logger logger) {
		return lastCommandSource.getWorld().getGameRules().getBoolean(MoreCommands.doStacktraceRule) || logger.isDebugEnabled();
	}
}
