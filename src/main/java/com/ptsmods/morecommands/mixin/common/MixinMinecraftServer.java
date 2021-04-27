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

	@Inject(at = @At("RETURN"), method = "startServer(Ljava/util/function/Function;)Lnet/minecraft/server/MinecraftServer;")
	private static <S extends MinecraftServer> S startServer(Function<Thread, S> serverFactory, CallbackInfoReturnable<S> cbi) {
		MoreCommands.setServerInstance(cbi.getReturnValue());
		return cbi.getReturnValue();
	}

}
