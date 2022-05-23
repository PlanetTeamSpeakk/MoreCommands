package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.ServerNetworkIo;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Mixin(ServerNetworkIo.class)
public class MixinServerNetworkIo {
    @Redirect(at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;", remap = false), method = "tick")
    private Iterator<ClientConnection> tick_newArrayList(List<ClientConnection> connections) {
        if (Objects.requireNonNull(ReflectionHelper.<ServerNetworkIo>cast(this).getServer().getWorld(World.OVERWORLD)).getGameRules().getBoolean(MoreGameRules.get().randomOrderPlayerTickRule())) Collections.shuffle(connections);
        return connections.iterator();
    }
}
