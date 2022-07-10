package com.ptsmods.morecommands.mixin.compat.compat19.plus;

import com.ptsmods.morecommands.compat.CommandRegistryAccessHolder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Commands.class)
public class MixinCommandManager {

    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;setConsumer(Lcom/mojang/brigadier/ResultConsumer;)V", shift = At.Shift.BEFORE), method = "<init>")
    private void init(Commands.CommandSelection environment, CommandBuildContext commandRegistryAccess, CallbackInfo ci) {
        CommandRegistryAccessHolder.commandRegistryAccess = commandRegistryAccess;
    }
}
