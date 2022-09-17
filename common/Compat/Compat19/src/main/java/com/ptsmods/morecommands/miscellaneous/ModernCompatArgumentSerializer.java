package com.ptsmods.morecommands.miscellaneous;

import com.google.gson.JsonObject;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeProperties;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeSerialiser;
import com.ptsmods.morecommands.api.arguments.CompatArgumentType;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ModernCompatArgumentSerializer<A extends CompatArgumentType<A, T, P>, T, P extends ArgumentTypeProperties<A, T, P>> implements ArgumentTypeInfo<A, ArgumentTypePropertiesImpl<A, T, P>> {
    private final ArgumentTypeSerialiser<A, T, P> serialiser;

    public ModernCompatArgumentSerializer(ArgumentTypeSerialiser<A, T, P> serialiser) {
        this.serialiser = serialiser;
    }

    @Override
    public void serializeToNetwork(ArgumentTypePropertiesImpl<A, T, P> template, FriendlyByteBuf buf) {
        template.getProperties().write(buf);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ArgumentTypePropertiesImpl<A, T, P> deserializeFromNetwork(FriendlyByteBuf buf) {
        return (ArgumentTypePropertiesImpl<A, T, P>) serialiser.fromPacket(buf).getProperties().toVanillaProperties();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void serializeToJson(ArgumentTypePropertiesImpl<A, T, P> template, JsonObject jsonObject) {
        serialiser.writeJson((P) template.getProperties(), jsonObject);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ArgumentTypePropertiesImpl<A, T, P> unpack(A argumentType) {
        return (ArgumentTypePropertiesImpl<A, T, P>) argumentType.getProperties().toVanillaProperties();
    }
}
