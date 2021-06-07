package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
	@Inject(at = @At("RETURN"), method = {"startServer", "method_29740"}, require = 1)
	private static <S extends MinecraftServer> void startServer(Function<Thread, S> serverFactory, CallbackInfoReturnable<S> cbi) {
		MoreCommands.setServerInstance(cbi.getReturnValue());
	}
}
