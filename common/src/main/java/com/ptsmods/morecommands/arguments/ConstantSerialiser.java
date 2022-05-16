package com.ptsmods.morecommands.arguments;

import com.google.gson.JsonObject;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeProperties;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeSerialiser;
import com.ptsmods.morecommands.api.arguments.CompatArgumentType;
import net.minecraft.network.PacketByteBuf;

import java.util.function.Supplier;

public class ConstantSerialiser<A extends CompatArgumentType<A, T, ConstantSerialiser.ConstantProperties<A, T>>, T> implements ArgumentTypeSerialiser<A, T, ConstantSerialiser.ConstantProperties<A, T>> {
	private final Supplier<A> instanceSupplier;
	private final ConstantProperties<A, T> properties;

	public ConstantSerialiser(Supplier<A> instanceSupplier) {
		this.instanceSupplier = instanceSupplier;
		properties = new ConstantProperties<>(this, instanceSupplier);
	}

	@Override
	public A fromPacket(PacketByteBuf buf) {
		return instanceSupplier.get();
	}

	@Override
	public void writeJson(ConstantProperties<A, T> properties, JsonObject json) {}

	public ConstantProperties<A, T> getProperties() {
		return properties;
	}

	public static class ConstantProperties<A extends CompatArgumentType<A, T, ConstantProperties<A, T>>, T> implements ArgumentTypeProperties<A, T, ConstantProperties<A, T>> {
		private final ConstantSerialiser<A, T> serialiser;
		private final Supplier<A> instanceSupplier;

		private ConstantProperties(ConstantSerialiser<A, T> serialiser, Supplier<A> instanceSupplier) {
			this.serialiser = serialiser;
			this.instanceSupplier = instanceSupplier;
		}

		@Override
		public A createType() {
			return instanceSupplier.get();
		}

		@Override
		public ArgumentTypeSerialiser<A, T, ConstantProperties<A, T>> getSerialiser() {
			return serialiser;
		}

		@Override
		public void write(PacketByteBuf buf) {}
	}
}
