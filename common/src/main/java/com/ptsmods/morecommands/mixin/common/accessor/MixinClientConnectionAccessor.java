package com.ptsmods.morecommands.mixin.common.accessor;

import io.netty.channel.Channel;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Connection.class)
public interface MixinClientConnectionAccessor {

    @Accessor
    void setChannel(Channel channel);
}
