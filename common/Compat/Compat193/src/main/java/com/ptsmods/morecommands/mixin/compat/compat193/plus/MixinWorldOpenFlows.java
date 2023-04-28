package com.ptsmods.morecommands.mixin.compat.compat193.plus;

import com.ptsmods.morecommands.api.IMoreCommands;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(WorldOpenFlows.class)
public class MixinWorldOpenFlows {
    @Inject(at = @At("HEAD"), method = "createLevelFromExistingSettings")
    private void createLevelPre(LevelStorageSource.LevelStorageAccess levelStorageAccess, ReloadableServerResources reloadableServerResources, LayeredRegistryAccess<RegistryLayer> layeredRegistryAccess, WorldData worldData, CallbackInfo ci) {
        IMoreCommands.get().setCreatingWorld(true);
    }

    @Inject(at = @At("TAIL"), method = "createLevelFromExistingSettings")
    private void createLevelPost(LevelStorageSource.LevelStorageAccess levelStorageAccess, ReloadableServerResources reloadableServerResources, LayeredRegistryAccess<RegistryLayer> layeredRegistryAccess, WorldData worldData, CallbackInfo ci) {
        IMoreCommands.get().setCreatingWorld(false);
    }

    @Inject(at = @At("HEAD"), method = "createFreshLevel")
    private void createFreshLevelPre(String string, LevelSettings levelSettings, WorldOptions worldOptions, Function<RegistryAccess, WorldDimensions> function, CallbackInfo ci) {
        IMoreCommands.get().setCreatingWorld(true);
    }

    @Inject(at = @At("TAIL"), method = "createFreshLevel")
    private void createFreshLevelPost(String string, LevelSettings levelSettings, WorldOptions worldOptions, Function<RegistryAccess, WorldDimensions> function, CallbackInfo ci) {
        IMoreCommands.get().setCreatingWorld(false);
    }
}
