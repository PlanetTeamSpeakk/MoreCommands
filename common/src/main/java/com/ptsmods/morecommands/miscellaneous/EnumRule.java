package com.ptsmods.morecommands.miscellaneous;

import com.mojang.brigadier.context.CommandContext;
import com.ptsmods.morecommands.arguments.EnumArgumentType;
import lombok.SneakyThrows;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

import static com.google.common.base.Preconditions.checkNotNull;

public final class EnumRule<E extends Enum<E>> extends GameRules.Rule<EnumRule<E>> {
	private final Class<E> classType;
	private final List<E> supportedValues;
	private E value;

	public static <E extends Enum<E>> GameRules.Type<EnumRule<E>> createEnumRule(Class<E> clazz, E defaultValue, BiConsumer<MinecraftServer, EnumRule<E>> changeListener) {
		return new GameRules.Type<>(() -> EnumArgumentType.enumType(clazz, clazz.getEnumConstants()), type -> new EnumRule<>(type, defaultValue, clazz.getEnumConstants()),
				(server, rule) -> {}, (visitor, key, type) -> {
			if (visitor instanceof MoreCommandsGameRuleVisitor)
				((MoreCommandsGameRuleVisitor) visitor).visitMCEnum(key, type);
		});
	}

	@Deprecated
	public EnumRule(GameRules.Type<EnumRule<E>> type, E value, E[] supportedValues) {
		this(type, value, Arrays.asList(supportedValues));
	}

	@Deprecated
	public EnumRule(GameRules.Type<EnumRule<E>> type, E value, Collection<E> supportedValues) {
		super(type);
		this.classType = value.getDeclaringClass();
		this.value = value;
		this.supportedValues = new ArrayList<>(supportedValues);

		if (!this.supports(value)) {
			throw new IllegalArgumentException("Cannot set default value");
		}
	}

	@Override
	@SneakyThrows
	protected void setFromArgument(CommandContext<ServerCommandSource> context, String name) {
		value = EnumArgumentType.getEnum(context, name);
	}

	@Override
	protected void deserialize(String value) {
		try {
			E deserialized = Enum.valueOf(this.classType, value);

			if (this.supports(deserialized))
				this.set(deserialized, null);
		} catch (IllegalArgumentException ignored) {}
	}

	@Override
	public String serialize() {
		return this.value.name();
	}

	@Override
	public int getCommandResult() {
		return this.value.ordinal();
	}

	@Override
	protected EnumRule<E> getThis() {
		return this;
	}

	public Class<E> getEnumClass() {
		return this.classType;
	}

	@Override
	public String toString() {
		return this.value.toString();
	}

	@Override
	protected EnumRule<E> copy() {
		return new EnumRule<>(this.type, this.value, this.supportedValues);
	}

	@Override
	public void setValue(EnumRule<E> rule, MinecraftServer minecraftServer) {
		if (!this.supports(rule.value)) {
			throw new IllegalArgumentException(String.format("Rule does not support value: %s", rule.value));
		}

		this.value = rule.value;
		this.changed(minecraftServer);
	}

	public E get() {
		return this.value;
	}

	public void cycle() {
		int index = this.supportedValues.indexOf(this.value);

		if (index < 0) {
			throw new IllegalArgumentException(String.format("Invalid value: %s", this.value));
		}

		this.set(this.supportedValues.get((index + 1) % this.supportedValues.size()), null);
	}

	public boolean supports(E value) {
		return this.supportedValues.contains(value);
	}

	public void set(E value, @Nullable MinecraftServer server) throws IllegalArgumentException {
		checkNotNull(value);

		if (!this.supports(value)) {
			throw new IllegalArgumentException("Tried to set an unsupported value: " + value.toString());
		}

		this.value = value;
		this.changed(server);
	}
}
