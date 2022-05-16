package com.ptsmods.morecommands.api.arguments;

import com.mojang.brigadier.arguments.ArgumentType;

public interface CompatArgumentType<S extends CompatArgumentType<S, T, P>, T, P extends ArgumentTypeProperties<S, T, P>> extends ArgumentType<T> {
	ArgumentType<T> toVanillaArgumentType();

	P getProperties();
}
