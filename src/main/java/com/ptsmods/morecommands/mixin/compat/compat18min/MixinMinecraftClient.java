package com.ptsmods.morecommands.mixin.compat.compat18min;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

	// TODO DynamicRegistryManager.Impl class doesn't exist anymore so we can't use mixins for this anymore,
	//  perhaps reimplement this with ASM?

//	@Inject(at = @At("HEAD"), method = "method_29607", remap = false)
//	private void createWorldPre(String worldName, LevelInfo levelInfo, DynamicRegistryManager.Impl registryTracker, GeneratorOptions generatorOptions, CallbackInfo ci) {
//		MoreCommands.creatingWorld = true;
//	}
//
//	@Inject(at = @At("TAIL"), method = "method_29607", remap = false)
//	private void createWorldPost(String worldName, LevelInfo levelInfo, DynamicRegistryManager.Impl registryTracker, GeneratorOptions generatorOptions, CallbackInfo ci) {
//		MoreCommands.creatingWorld = false;
//	}
}
