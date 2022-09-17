package com.ptsmods.morecommands.api.arguments;

import com.google.gson.JsonObject;
import com.ptsmods.morecommands.api.util.compat.Compat;
import net.minecraft.network.FriendlyByteBuf;

import java.util.HashMap;
import java.util.Map;

public interface ArgumentTypeSerialiser<A extends CompatArgumentType<A, T, P>, T, P extends ArgumentTypeProperties<A, T, P>> {
    Map<ArgumentTypeSerialiser<?, ?, ?>, Object> serialisers = new HashMap<>();

    A fromPacket(FriendlyByteBuf buf);

    void writeJson(P properties, JsonObject json);

    default Object toVanillaSerialiser() {
        return Compat.get().newArgumentSerialiserImpl(this);
    }
}
