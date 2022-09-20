package com.ptsmods.morecommands.mixin.compat.compat19.plus;

import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.IMoreCommandsClient;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @ModifyVariable(at = @At("STORE"), method = "doWorldLoad", require = 1)
    private Connection startIntegratedServer_integratedServerConnectionNew(Connection connection) {
        if (IMoreCommands.get().isCreatingWorld()) IMoreCommandsClient.get().setScheduleWorldInitCommands(true);
        IMoreCommands.get().setCreatingWorld(false);
        return connection;
    }
}
