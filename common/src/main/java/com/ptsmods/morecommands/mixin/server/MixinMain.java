package com.ptsmods.morecommands.mixin.server;

import com.ptsmods.morecommands.api.callbacks.PostInitEvent;
import net.minecraft.server.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Main.class, priority = 1100)
public class MixinMain {

    @Group(name = "postInitServer", min = 1, max = 1)
    @Inject(at = @At(value = "INVOKE", target = "Lnet/fabricmc/loader/entrypoint/minecraft/hooks/EntrypointServer;start(Ljava/io/File;Ljava/lang/Object;)V", remap = false, shift = At.Shift.AFTER), method = "main", remap = false)
    private static void postInitOld(String[] args, CallbackInfo cbi) {
        PostInitEvent.EVENT.invoker().postInit();
    }

    @Group(name = "postInitServer", min = 1, max = 1)
    @Inject(at = @At(value = "INVOKE", target = "Lnet/fabricmc/loader/impl/game/minecraft/Hooks;startServer(Ljava/io/File;Ljava/lang/Object;)V", remap = false, shift = At.Shift.AFTER), method = "main", remap = false)
    private static void postInitNew(String[] args, CallbackInfo cbi) {
        PostInitEvent.EVENT.invoker().postInit();
    }
}
