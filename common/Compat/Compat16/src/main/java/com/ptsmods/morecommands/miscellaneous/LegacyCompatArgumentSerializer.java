package com.ptsmods.morecommands.miscellaneous;

import com.google.gson.JsonObject;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeProperties;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeSerialiser;
import com.ptsmods.morecommands.api.arguments.CompatArgumentType;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;

public class LegacyCompatArgumentSerializer<A extends CompatArgumentType<A, T, P>, T, P extends ArgumentTypeProperties<A, T, P>> implements ArgumentSerializer<A> {
    private final ArgumentTypeSerialiser<A, T, P> serialiser;

    public LegacyCompatArgumentSerializer(ArgumentTypeSerialiser<A, T, P> serialiser) {
        this.serialiser = serialiser;
    }

    @Override
    public void serializeToNetwork(A argumentType, FriendlyByteBuf buf) {
        argumentType.getProperties().write(buf);
    }

    @Override
    public A deserializeFromNetwork(FriendlyByteBuf buf) {
        return serialiser.fromPacket(buf);
    }

    @Override
    public void serializeToJson(A argumentType, JsonObject jsonObject) {
        serialiser.writeJson(argumentType.getProperties(), jsonObject);
    }
}
