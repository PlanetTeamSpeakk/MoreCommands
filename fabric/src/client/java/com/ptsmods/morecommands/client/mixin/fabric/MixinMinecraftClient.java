package com.ptsmods.morecommands.client.mixin.fabric;

import com.ptsmods.morecommands.api.callbacks.PostInitEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class, priority = Integer.MAX_VALUE)
public class MixinMinecraftClient {

    @Group(name = "postInitClient", min = 1, max = 1)
    @Inject(at = @At(value = "INVOKE", target = "Lnet/fabricmc/loader/entrypoint/minecraft/hooks/EntrypointClient;start(Ljava/io/File;Ljava/lang/Object;)V", remap = false, shift = At.Shift.AFTER), method = "<init>")
    private void postInitOld(GameConfig args, CallbackInfo cbi) {
        PostInitEvent.EVENT.invoker().postInit();
    }

    @Group(name = "postInitClient", min = 1, max = 1)
    @Inject(at = @At(value = "INVOKE", target = "Lnet/fabricmc/loader/impl/game/minecraft/Hooks;startClient(Ljava/io/File;Ljava/lang/Object;)V", remap = false, shift = At.Shift.AFTER), method = "<init>")
    private void postInitNew(GameConfig args, CallbackInfo cbi) {
        PostInitEvent.EVENT.invoker().postInit();
    }
}
