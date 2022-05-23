package com.ptsmods.morecommands.api.arguments;

import com.ptsmods.morecommands.api.ReflectionHelper;
import net.minecraft.network.PacketByteBuf;

import java.lang.reflect.Proxy;

public interface ArgumentTypeProperties<A extends CompatArgumentType<A, T, P>, T, P extends ArgumentTypeProperties<A, T, P>> {

    A createType();

    ArgumentTypeSerialiser<A, T, P> getSerialiser();

    void write(PacketByteBuf buf);

    default Object toVanillaProperties() {
        return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{ReflectionHelper.getMcClass("class_2314$class_7217",
                "net/minecraft/commands/synchronization/ArgumentTypeInfo$Template"), ArgumentTypeProperties.class}, (proxy, method, args) -> {
            if (method.getDeclaringClass() == ArgumentTypeProperties.class || ArgumentTypeProperties.class.isAssignableFrom(method.getDeclaringClass()))
                return method.invoke(this, args);

            switch (method.getName()) {
                case "method_41730":
                case "createType":
                case "instantiate":
                    return createType();

                case "method_41728":
                case "getSerializer":
                case "type":
                    return getSerialiser().toVanillaSerialiser();

                // BASE METHODS
                case "equals":
                    return equals(args[0]);
                case "hashCode":
                    return hashCode();
                case "toString":
                    return toString();
            }

            return null;
        });
    }
}
