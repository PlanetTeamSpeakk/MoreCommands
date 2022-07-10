package com.ptsmods.morecommands.mixin.compat.compat18.min;

import com.ptsmods.morecommands.api.IMoreCommands;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraftClient {

    @Inject(at = @At("HEAD"), method = "createLevel")
    private void createWorldPre(String worldName, LevelSettings levelInfo, RegistryAccess.RegistryHolder registryTracker, WorldGenSettings generatorOptions, CallbackInfo ci) {
        IMoreCommands.get().setCreatingWorld(true);
    }

    @Inject(at = @At("TAIL"), method = "createLevel")
    private void createWorldPost(String worldName, LevelSettings levelInfo, RegistryAccess.RegistryHolder registryTracker, WorldGenSettings generatorOptions, CallbackInfo ci) {
        IMoreCommands.get().setCreatingWorld(false);
    }
}
