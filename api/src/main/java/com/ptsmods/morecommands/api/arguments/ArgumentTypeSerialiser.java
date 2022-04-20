package com.ptsmods.morecommands.api.arguments;

import com.google.gson.JsonObject;
import com.ptsmods.morecommands.api.ReflectionHelper;
import net.minecraft.network.PacketByteBuf;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public interface ArgumentTypeSerialiser<A extends CompatArgumentType<A, T, P>, T, P extends ArgumentTypeProperties<A, T, P>> {
	Map<ArgumentTypeSerialiser<?, ?, ?>, Object> serialisers = new HashMap<>();

	A fromPacket(PacketByteBuf buf);
	void writeJson(P properties, JsonObject json);

	@SuppressWarnings("unchecked")
	default Object toLegacyVanillaSerialiser() {
		return serialisers.computeIfAbsent(this, s -> Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {ReflectionHelper.getMcClass("class_2314")}, (proxy, method, args) -> {
			switch (method.getName()) {
				case "method_10007":
				case "toPacket":
					((A) args[0]).getProperties().write((PacketByteBuf) args[1]);
					return null;

				case "method_10005":
				case "fromPacket":
					return fromPacket((PacketByteBuf) args[0]);

				case "method_10006":
				case "toJson":
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
		return serialisers.computeIfAbsent(this, s -> Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {ReflectionHelper.getMcClass("class_2314")}, (proxy, method, args) -> {
			switch (method.getName()) {
				case "method_10007":
				case "writePacket":
					((P) args[0]).write((PacketByteBuf) args[1]);
					return null;

				case "method_10005":
				case "fromPacket":
					return fromPacket((PacketByteBuf) args[0]).getProperties().toVanillaProperties();

				case "method_10006":
				case "writeJson":
					writeJson((P) args[0], (JsonObject) args[1]);
					return null;

				case "method_41726":
				case "getArgumentTypeProperties":
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
