package com.ptsmods.morecommands.miscellaneous;

import com.ptsmods.morecommands.api.arguments.ArgumentTypeProperties;
import com.ptsmods.morecommands.api.arguments.CompatArgumentType;
import lombok.Getter;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import org.jetbrains.annotations.NotNull;

public class ArgumentTypePropertiesImpl<A extends CompatArgumentType<A, T, P>, T, P extends ArgumentTypeProperties<A, T, P>> implements ArgumentTypeInfo.Template<A> {
    @Getter
    private final ArgumentTypeProperties<A, T, P> properties;

    public ArgumentTypePropertiesImpl(ArgumentTypeProperties<A, T, P> properties) {
        this.properties = properties;
    }

    @Override
    public A instantiate(@NotNull CommandBuildContext commandBuildContext) {
        return properties.createType();
    }

    @SuppressWarnings("unchecked")
    @Override
    public ArgumentTypeInfo<A, ?> type() {
        return (ArgumentTypeInfo<A, ?>) properties.getSerialiser().toVanillaSerialiser();
    }
}
