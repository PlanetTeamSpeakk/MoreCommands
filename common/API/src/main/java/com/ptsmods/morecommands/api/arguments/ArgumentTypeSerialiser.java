package com.ptsmods.morecommands.api.arguments;

import com.google.gson.JsonObject;
import com.ptsmods.morecommands.api.ReflectionHelper;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.FriendlyByteBuf;

public interface ArgumentTypeSerialiser<A extends CompatArgumentType<A, T, P>, T, P extends ArgumentTypeProperties<A, T, P>> {
    Map<ArgumentTypeSerialiser<?, ?, ?>, Object> serialisers = new HashMap<>();

    A fromPacket(FriendlyByteBuf buf);

    void writeJson(P properties, JsonObject json);

    @SuppressWarnings("unchecked")
    default Object toLegacyVanillaSerialiser() {
        return serialisers.computeIfAbsent(this, s -> Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{ReflectionHelper.getMcClass("class_2314",
                "net/minecraft/commands/synchronization/ArgumentSerializer")}, (proxy, method, args) -> {
            switch (method.getName()) {
                case "method_10007":
                case "toPacket":
                case "func_197072_a":
                case "serializeToNetwork":
                    ((A) args[0]).getProperties().write((FriendlyByteBuf) args[1]);
                    return null;

                case "method_10005":
                case "fromPacket":
                case "func_197071_b":
                case "deserializeFromNetwork":
                    return fromPacket((FriendlyByteBuf) args[0]);

                case "method_10006":
                case "toJson":
                case "func_212244_a":
                case "serializeToJson":
                    writeJson(((A) args[0]).getProperties(), (JsonObject) args[1]);
                    return null;

                // BASE METHODS
                case "equals":
                    return equals(args[0]);
                case "hashCode":
                    return hashCode();
                case "toString":
                    return toString();
            }

            return null;
        }));
    }

    @SuppressWarnings("unchecked")
    default Object toVanillaSerialiser() {
        return serialisers.computeIfAbsent(this, s -> Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{ReflectionHelper.getMcClass("class_2314",
                "net/minecraft/commands/synchronization/ArgumentTypeInfo")}, (proxy, method, args) -> {
            switch (method.getName()) {
                case "method_10007":
                case "writePacket":
                case "serializeToNetwork":
                    ((P) args[0]).write((FriendlyByteBuf) args[1]);
                    return null;

                case "method_10005":
                case "fromPacket":
                case "deserializeFromNetwork":
                    return fromPacket((FriendlyByteBuf) args[0]).getProperties().toVanillaProperties();

                case "method_10006":
                case "writeJson":
                case "serializeToJson":
                    writeJson((P) args[0], (JsonObject) args[1]);
                    return null;

                case "method_41726":
                case "getArgumentTypeProperties":
                case "unpack":
                    return ((A) args[0]).getProperties().toVanillaProperties();

                // BASE METHODS
                case "equals":
                    return equals(args[0]);
                case "hashCode":
                    return hashCode();
                case "toString":
                    return toString();
            }

            return null;
        }));
    }
}
