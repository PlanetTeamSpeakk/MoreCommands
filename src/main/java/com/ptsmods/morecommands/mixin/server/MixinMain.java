package com.ptsmods.morecommands.mixin.server;

import com.ptsmods.morecommands.callbacks.PostInitCallback;
import net.minecraft.server.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class MixinMain {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/fabricmc/loader/entrypoint/minecraft/hooks/EntrypointServer;start(Ljava/io/File;Ljava/lang/Object;)V", remap = false, shift = At.Shift.AFTER), method = "main", remap = false)
    private static void main(String[] args, CallbackInfo ci) {
        PostInitCallback.EVENT.invoker().postInit();
    }
}
