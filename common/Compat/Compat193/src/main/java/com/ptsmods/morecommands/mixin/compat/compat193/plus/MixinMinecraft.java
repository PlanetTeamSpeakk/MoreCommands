package com.ptsmods.morecommands.mixin.compat.compat193.plus;

import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.IMoreCommandsClient;
import net.minecraft.client.Minecraft;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "doWorldLoad", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/network/Connection;connectToLocalServer(Ljava/net/SocketAddress;)Lnet/minecraft/network/Connection;"))
    private void startIntegratedServer_integratedServerConnectionNew(String string, LevelStorageSource.LevelStorageAccess levelStorageAccess,
                                                                     PackRepository packRepository, WorldStem worldStem, boolean isNewWorld, CallbackInfo ci) {
        IMoreCommandsClient.get().setScheduleWorldInitCommands(isNewWorld);
        IMoreCommands.get().setCreatingWorld(false);
    }
}
