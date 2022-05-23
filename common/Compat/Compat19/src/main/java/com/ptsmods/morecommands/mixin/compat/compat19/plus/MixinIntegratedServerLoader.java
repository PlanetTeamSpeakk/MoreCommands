package com.ptsmods.morecommands.mixin.compat.compat19.plus;

import com.ptsmods.morecommands.api.IMoreCommands;
import net.minecraft.server.DataPackContents;
import net.minecraft.server.integrated.IntegratedServerLoader;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IntegratedServerLoader.class)
public class MixinIntegratedServerLoader {

    @Inject(at = @At("HEAD"), method = "start(Lnet/minecraft/world/level/storage/LevelStorage$Session;Lnet/minecraft/server/DataPackContents;Lnet/minecraft/util/registry/DynamicRegistryManager$Immutable;Lnet/minecraft/world/SaveProperties;)V")
    private void createWorldPre(LevelStorage.Session session, DataPackContents dataPackContents, DynamicRegistryManager.Immutable dynamicRegistryManager, SaveProperties saveProperties, CallbackInfo cbi) {
        IMoreCommands.get().setCreatingWorld(true);
    }

    @Inject(at = @At("TAIL"), method = "start(Lnet/minecraft/world/level/storage/LevelStorage$Session;Lnet/minecraft/server/DataPackContents;Lnet/minecraft/util/registry/DynamicRegistryManager$Immutable;Lnet/minecraft/world/SaveProperties;)V")
    private void createWorldPost(LevelStorage.Session session, DataPackContents dataPackContents, DynamicRegistryManager.Immutable dynamicRegistryManager, SaveProperties saveProperties, CallbackInfo cbi) {
        IMoreCommands.get().setCreatingWorld(false);
    }
}
