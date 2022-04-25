package com.ptsmods.morecommands.compat;

import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.addons.PaintingEntityAddon;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeProperties;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeSerialiser;
import com.ptsmods.morecommands.api.arguments.CompatArgumentType;
import com.ptsmods.morecommands.api.text.LiteralTextBuilder;
import com.ptsmods.morecommands.api.text.TextBuilder;
import com.ptsmods.morecommands.api.text.TranslatableTextBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.text.*;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.Registry;

import java.lang.invoke.MethodHandle;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

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

	@Override
	public Object getPaintingVariant(PaintingEntity painting) {
		return painting.getVariant().value();
	}

	@Override
	public void setPaintingVariant(PaintingEntity entity, Object variant) {
		((PaintingEntityAddon) entity).mc$setVariant(variant);
	}

	@Override
	public MutableText buildText(LiteralTextBuilder builder) {
		return buildText(new LiteralTextContent(builder.getLiteral()), builder);
	}

	@Override
	public MutableText buildText(TranslatableTextBuilder builder) {
		return buildText(builder.getArgs().length == 0 ? new TranslatableTextContent(builder.getKey()) : new TranslatableTextContent(builder.getKey(), Arrays.stream(builder.getArgs())
				.map(o -> o instanceof TextBuilder ? ((TextBuilder<?>) o).build() : o)
				.toArray(Object[]::new)), builder);
	}

	private MutableText buildText(TextContent content, TextBuilder<?> builder) {
		MutableText text = MutableText.of(content).setStyle(builder.getStyle());
		builder.getChildren().forEach(child -> text.append(child.build()));
		return text;
	}

	@Override
	public TextBuilder<?> builderFromText(Text text) {
		TextContent content = text.getContent();
		TextBuilder<?> builder;
		if (content instanceof LiteralTextContent)
			builder = LiteralTextBuilder.builder(((LiteralTextContent) content).string());
		else if (content instanceof TranslatableTextContent)
			builder = TranslatableTextBuilder.builder(((TranslatableTextContent) content).getKey(), Arrays.stream(((TranslatableTextContent) content).getArgs())
					.map(o -> o instanceof Text ? builderFromText((Text) o) : o)
					.toArray(Object[]::new));
		else throw new IllegalArgumentException("Given text was neither literal nor translatable.");

		return builder
				.withStyle(text.getStyle())
				.withChildren(text.getSiblings().stream()
						.map(this::builderFromText)
						.collect(Collectors.toList()));
	}
}
