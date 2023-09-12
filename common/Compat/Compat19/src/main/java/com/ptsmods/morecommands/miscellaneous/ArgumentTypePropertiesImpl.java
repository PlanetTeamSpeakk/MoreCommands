package com.ptsmods.morecommands.miscellaneous;

import com.ptsmods.morecommands.api.arguments.ArgumentTypeProperties;
import com.ptsmods.morecommands.api.arguments.CompatArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import org.jetbrains.annotations.NotNull;

public record ArgumentTypePropertiesImpl<A extends CompatArgumentType<A, T, P>, T, P extends ArgumentTypeProperties<A, T, P>>(
        ArgumentTypeProperties<A, T, P> properties) implements ArgumentTypeInfo.Template<A> {

    @Override
    public @NotNull A instantiate(@NotNull CommandBuildContext commandBuildContext) {
        return properties.createType();
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull ArgumentTypeInfo<A, ?> type() {
        return (ArgumentTypeInfo<A, ?>) properties.getSerialiser().toVanillaSerialiser();
    }
}
