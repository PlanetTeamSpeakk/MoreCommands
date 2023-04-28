package com.ptsmods.morecommands.compat;

import com.mojang.authlib.GameProfile;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.addons.PaintingEntityAddon;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeProperties;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeSerialiser;
import com.ptsmods.morecommands.api.arguments.CompatArgumentType;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.api.util.text.EmptyTextBuilder;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.api.util.text.TextBuilder;
import com.ptsmods.morecommands.api.util.text.TranslatableTextBuilder;
import com.ptsmods.morecommands.miscellaneous.ArgumentTypePropertiesImpl;
import com.ptsmods.morecommands.miscellaneous.ModernCompatArgumentSerializer;
import com.ptsmods.morecommands.mixin.compat.compat19.plus.MixinArgumentTypesAccessor;
import com.ptsmods.morecommands.mixin.compat.compat19.plus.MixinKeybindTranslationsAccessor;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

public class Compat19 extends Compat182 {

    @Override
    public boolean isRemoved(Entity entity) {
        return entity.isRemoved(); // Method did not change or move, but since 1.19, srg calls it m_213877_ instead of m_146910_.
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A extends CompatArgumentType<A, T, P>, T, P extends ArgumentTypeProperties<A, T, P>> void registerArgumentType
            (DeferredRegister<?> registry, String identifier, Class<A> clazz, ArgumentTypeSerialiser<A, T, P> serialiser) {
        ArgumentTypeInfo<A, ArgumentTypeInfo.Template<A>> serializer = (ArgumentTypeInfo<A, ArgumentTypeInfo.Template<A>>) serialiser.toVanillaSerialiser();
        ((DeferredRegister<ArgumentTypeInfo<?, ?>>) registry).register(new ResourceLocation(identifier), () -> serializer);
        MixinArgumentTypesAccessor.getClassMap().put(clazz, serializer);
    }

    @Override
    public BlockStateArgument createBlockStateArgumentType() {
        return BlockStateArgument.block((CommandBuildContext) Compat.get().newCommandBuildContext());
    }

    @Override
    public Direction randomDirection() {
        return Direction.getRandom(RandomSource.create());
    }

    @Override
    public ResourceLocation getPaintingVariant(Painting painting) {
        return Compat.get().<PaintingVariant>getBuiltInRegistry("painting_variant").getKey(painting.getVariant().value());
    }

    @Override
    public ResourceLocation nextPaintingVariant(ResourceLocation variant) {
        MappedRegistry<PaintingVariant> registry = Compat.get().getBuiltInRegistry("painting_variant");
        return registry.getKey(registry.byId((registry.getId(registry.get(variant)) + 1) % registry.size()));
    }

    @Override
    public void setPaintingVariant(Painting entity, ResourceLocation variant) {
        ((PaintingEntityAddon) entity).mc$setVariant(Compat.get().<PaintingVariant>getBuiltInRegistry("painting_variant")
                .get(variant));
    }

    @Override
    public MutableComponent buildText(LiteralTextBuilder builder) {
        return buildText(new LiteralContents(builder.getLiteral()), builder);
    }

    @Override
    public MutableComponent buildText(TranslatableTextBuilder builder) {
        return buildText(builder.getArgs().length == 0 ? new TranslatableContents(builder.getKey()) : new TranslatableContents(builder.getKey(), Arrays.stream(builder.getArgs())
                .map(o -> o instanceof TextBuilder ? ((TextBuilder<?>) o).build() : o)
                .toArray(Object[]::new)), builder);
    }

    @Override
    public MutableComponent buildText(EmptyTextBuilder builder) {
        return buildText(ComponentContents.EMPTY, builder);
    }

    private MutableComponent buildText(ComponentContents content, TextBuilder<?> builder) {
        MutableComponent text = MutableComponent.create(content).setStyle(builder.getStyle());
        builder.getChildren().forEach(child -> text.append(child.build()));
        return text;
    }

    @Override
    public TextBuilder<?> builderFromText(Component text) {
        ComponentContents content = text.getContents();
        TextBuilder<?> builder;
        if (content instanceof LiteralContents)
            builder = LiteralTextBuilder.builder(((LiteralContents) content).text());
        else if (content instanceof TranslatableContents)
            builder = TranslatableTextBuilder.builder(((TranslatableContents) content).getKey(), Arrays.stream(((TranslatableContents) content).getArgs())
                    .map(o -> o instanceof Component ? builderFromText((Component) o) : o)
                    .toArray(Object[]::new));
        else if (content instanceof KeybindContents)
            builder = builderFromText(MixinKeybindTranslationsAccessor.getFactory().apply(((KeybindContents) content).getName()).get());
        else if (content instanceof ScoreContents || content instanceof NbtContents || content instanceof SelectorContents)
            builder = LiteralTextBuilder.builder(content.toString()); // Not sure how to handle this.
        else if (content == ComponentContents.EMPTY) builder = EmptyTextBuilder.builder();
        else throw new IllegalArgumentException("Given text is not supported.");

        return builder
                .withStyle(style -> style.isEmpty() ? text.getStyle() : style) // Empty check only required in the case of Keybind content.
                .withChildren(text.getSiblings().stream()
                        .map(this::builderFromText)
                        .collect(Collectors.toList()));
    }

    @Override
    public void broadcast(PlayerList playerManager, Tuple<Integer, ResourceLocation> type, Component message) {
        playerManager.broadcastSystemMessage(message, ResourceKey.create(
                ResourceKey.createRegistryKey(new ResourceLocation("chat_type")), type.getB()));
    }

    @Override
    public void onStacksDropped(BlockState state, ServerLevel world, BlockPos pos, ItemStack stack, boolean b) {
        state.spawnAfterBreak(world, pos, stack, b);
    }

    @Override
    public BlockPos getWorldSpawnPos(ServerLevel world) {
        return world.getSharedSpawnPos();
    }

    @Override
    public <A extends CompatArgumentType<A, T, P>, T, P extends ArgumentTypeProperties<A, T, P>> Object newArgumentTypePropertiesImpl(ArgumentTypeProperties<A, T, P> properties) {
        return new ArgumentTypePropertiesImpl<>(properties);
    }

    @Override
    public <A extends CompatArgumentType<A, T, P>, T, P extends ArgumentTypeProperties<A, T, P>> Object newArgumentSerialiserImpl(ArgumentTypeSerialiser<A, T, P> serialiser) {
        return new ModernCompatArgumentSerializer<>(serialiser);
    }

    @Override
    public UUID getUUID(Entity entity) {
        return entity.getUUID();
    }

    @Override
    public AABB getBoundingBox(Entity entity) {
        return entity.getBoundingBox();
    }

    @Override
    public BlockPos blockPosition(Entity entity) {
        return entity.blockPosition();
    }

    @Override
    public ServerPlayer newServerPlayerEntity(MinecraftServer server, ServerLevel world, GameProfile profile) {
        return new ServerPlayer(server, world, profile, null);
    }

    @Override
    public Object newCommandBuildContext() {
        return new CommandBuildContext(IMoreCommands.get().getServer() == null ?
                RegistryAccess.builtinCopy().freeze() : IMoreCommands.get().getServer().registryAccess());
    }
}
