package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.callbacks.CommandsRegisteredCallback;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import com.ptsmods.morecommands.util.ReflectionHelper;
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

	@Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/server/command/CommandManager$RegistrationEnvironment;)V")
	private void init(CommandManager.RegistrationEnvironment environment, CallbackInfo cbi) {
		CommandsRegisteredCallback.EVENT.invoker().onRegistered(ReflectionHelper.<CommandManager>cast(this).getDispatcher());
	}
}
