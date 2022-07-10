package com.ptsmods.morecommands.mixin.compat.compat19.plus;

import com.ptsmods.morecommands.api.IMoreCommands;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldOpenFlows.class)
public class MixinIntegratedServerLoader {

    @Inject(at = @At("HEAD"), method = "createLevelFromExistingSettings")
    private void createWorldPre(LevelStorageSource.LevelStorageAccess session, ReloadableServerResources dataPackContents, RegistryAccess.Frozen dynamicRegistryManager, WorldData saveProperties, CallbackInfo cbi) {
        IMoreCommands.get().setCreatingWorld(true);
    }

    @Inject(at = @At("TAIL"), method = "createLevelFromExistingSettings")
    private void createWorldPost(LevelStorageSource.LevelStorageAccess session, ReloadableServerResources dataPackContents, RegistryAccess.Frozen dynamicRegistryManager, WorldData saveProperties, CallbackInfo cbi) {
        IMoreCommands.get().setCreatingWorld(false);
    }
}
