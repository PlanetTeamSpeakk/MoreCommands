package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import net.minecraft.network.Connection;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Mixin(ServerConnectionListener.class)
public class MixinServerNetworkIo {
    @Redirect(at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;", remap = false), method = "tick")
    private Iterator<Connection> tick_newArrayList(List<Connection> connections) {
        if (Objects.requireNonNull(ReflectionHelper.<ServerConnectionListener>cast(this).getServer().getLevel(Level.OVERWORLD)).getGameRules().getBoolean(MoreGameRules.get().randomOrderPlayerTickRule())) Collections.shuffle(connections);
        return connections.iterator();
    }
}
