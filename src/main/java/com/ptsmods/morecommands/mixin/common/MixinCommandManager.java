package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.callbacks.CommandsRegisteredCallback;
import net.minecraft.server.command.CommandManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public class MixinCommandManager {

    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/server/command/CommandManager$RegistrationEnvironment;)V")
    private void init(CommandManager.RegistrationEnvironment environment, CallbackInfo cbi) {
        CommandsRegisteredCallback.EVENT.invoker().onRegistered(MoreCommands.<CommandManager>cast(this).getDispatcher());
    }

}
