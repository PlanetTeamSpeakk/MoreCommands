package com.ptsmods.morecommands.mixin.server;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftDedicatedServer.class)
public class MixinMinecraftDedicatedServer {

    @Inject(at = @At("TAIL"), method = "setupServer()Z")
    public boolean setupServer(CallbackInfoReturnable<Boolean> cbi) {
        MoreCommands.setServerInstance(ReflectionHelper.cast(this));
        return cbi.getReturnValueZ();
    }

}
