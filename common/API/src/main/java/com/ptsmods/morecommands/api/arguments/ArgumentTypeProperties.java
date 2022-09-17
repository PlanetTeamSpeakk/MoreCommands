package com.ptsmods.morecommands.api.arguments;

import com.ptsmods.morecommands.api.util.compat.Compat;
import net.minecraft.network.FriendlyByteBuf;

public interface ArgumentTypeProperties<A extends CompatArgumentType<A, T, P>, T, P extends ArgumentTypeProperties<A, T, P>> {

    A createType();

    ArgumentTypeSerialiser<A, T, P> getSerialiser();

    void write(FriendlyByteBuf buf);

    default Object toVanillaProperties() {
        return Compat.get().newArgumentTypePropertiesImpl(this);
    }
}
