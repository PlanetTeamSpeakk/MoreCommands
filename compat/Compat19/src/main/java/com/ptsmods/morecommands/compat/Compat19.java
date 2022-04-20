package com.ptsmods.morecommands.compat;

import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeProperties;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeSerialiser;
import com.ptsmods.morecommands.api.arguments.CompatArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.Registry;

import java.lang.invoke.MethodHandle;
import java.util.Objects;

public class Compat19 extends Compat182 {
	private static final MethodHandle registerArgumentType = Objects.requireNonNull(ReflectionHelper.unreflect(ReflectionHelper.getYarnMethod(
			ArgumentTypes.class, "register", "method_10017", Registry.class, String.class, Class.class, ArgumentSerializer.class)));

	@SuppressWarnings("unchecked")
	@Override
	public <A extends CompatArgumentType<A, T, P>, T, P extends ArgumentTypeProperties<A, T, P>> void registerArgumentType
			(String identifier, Class<A> clazz, ArgumentTypeSerialiser<A, T, P> serialiser) {
		try {
			registerArgumentType.invoke(Registry.COMMAND_ARGUMENT_TYPE, identifier, clazz, (ArgumentSerializer<A, ArgumentSerializer.ArgumentTypeProperties<A>>) serialiser.toVanillaSerialiser());
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public BlockStateArgumentType createBlockStateArgumentType() {
		return BlockStateArgumentType.blockState((CommandRegistryAccess) CommandRegistryAccessHolder.commandRegistryAccess);
	}

	@Override
	public Direction randomDirection() {
		return Direction.random(AbstractRandom.create());
	}
}
