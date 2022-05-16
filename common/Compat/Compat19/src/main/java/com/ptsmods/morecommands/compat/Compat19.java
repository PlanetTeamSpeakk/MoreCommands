package com.ptsmods.morecommands.compat;

import com.ptsmods.morecommands.api.MixinAccessWidener;
import com.ptsmods.morecommands.api.addons.PaintingEntityAddon;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeProperties;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeSerialiser;
import com.ptsmods.morecommands.api.arguments.CompatArgumentType;
import com.ptsmods.morecommands.api.util.text.EmptyTextBuilder;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.api.util.text.TextBuilder;
import com.ptsmods.morecommands.api.util.text.TranslatableTextBuilder;
import com.ptsmods.morecommands.mixin.compat.compat19.plus.MixinKeybindTranslationsAccessor;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Compat19 extends Compat182 {

	@SuppressWarnings("unchecked")
	@Override
	public <A extends CompatArgumentType<A, T, P>, T, P extends ArgumentTypeProperties<A, T, P>> void registerArgumentType
			(DeferredRegister<?> registry, String identifier, Class<A> clazz, ArgumentTypeSerialiser<A, T, P> serialiser) {
		ArgumentSerializer<A, ArgumentSerializer.ArgumentTypeProperties<A>> serializer = (ArgumentSerializer<A, ArgumentSerializer.ArgumentTypeProperties<A>>) serialiser.toVanillaSerialiser();
		((DeferredRegister<ArgumentSerializer<?, ?>>) registry).register(new Identifier(identifier), () -> serializer);
		((Map<Class<?>, ArgumentSerializer<?, ?>>) MixinAccessWidener.get().argumentTypes$getClassMap()).put(clazz, serializer);
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
		else if (content instanceof KeybindTextContent)
			builder = builderFromText(MixinKeybindTranslationsAccessor.getFactory().apply(((KeybindTextContent) content).getKey()).get());
		else if (content instanceof ScoreTextContent || content instanceof NbtTextContent || content instanceof SelectorTextContent)
			builder = LiteralTextBuilder.builder(content.toString()); // Not sure how to handle this.
		else if (content == TextContent.EMPTY) builder = EmptyTextBuilder.builder();
		else throw new IllegalArgumentException("Given text is not supported.");

		return builder
				.withStyle(style -> style.isEmpty() ? text.getStyle() : style) // Empty check only required in the case of Keybind content.
				.withChildren(text.getSiblings().stream()
						.map(this::builderFromText)
						.collect(Collectors.toList()));
	}

	@Override
	public void broadcast(PlayerManager playerManager, Pair<Integer, Identifier> type, Text message) {
		playerManager.broadcast(message, RegistryKey.of(Registry.MESSAGE_TYPE_KEY, type.getRight()));
	}

	@Override
	public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack, boolean b) {
		state.onStacksDropped(world, pos, stack, b);
	}

	@Override
	public BlockPos getWorldSpawnPos(ServerWorld world) {
		return world.getSpawnPos();
	}
}
