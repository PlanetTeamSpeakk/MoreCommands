package com.ptsmods.morecommands.mixin.compat.compat18.min;

import com.ptsmods.morecommands.api.IMoreCommands;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Inject(at = @At("HEAD"), method = "createWorld")
    private void createWorldPre(String worldName, LevelInfo levelInfo, DynamicRegistryManager.Impl registryTracker, GeneratorOptions generatorOptions, CallbackInfo ci) {
        IMoreCommands.get().setCreatingWorld(true);
    }

    @Inject(at = @At("TAIL"), method = "createWorld")
    private void createWorldPost(String worldName, LevelInfo levelInfo, DynamicRegistryManager.Impl registryTracker, GeneratorOptions generatorOptions, CallbackInfo ci) {
        IMoreCommands.get().setCreatingWorld(false);
    }
}
