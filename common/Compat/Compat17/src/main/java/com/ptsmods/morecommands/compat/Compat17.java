package com.ptsmods.morecommands.compat;

import com.google.common.collect.ImmutableMap;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeProperties;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeSerialiser;
import com.ptsmods.morecommands.api.arguments.CompatArgumentType;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.api.util.text.EmptyTextBuilder;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.api.util.text.TextBuilder;
import com.ptsmods.morecommands.api.util.text.TranslatableTextBuilder;
import com.ptsmods.morecommands.miscellaneous.LegacyCompatArgumentSerializer;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.registry.registries.DeferredRegister;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import lombok.Getter;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.DoubleStream;

public class Compat17 implements Compat {
    private static Map<ResourceLocation, Object> blockTags = null;
    // For some reason, I cannot reference this class directly. Arch transformer throws an error saying
    // the class doesn't exist (which is true in the runtime version) if I do.
    @Getter(lazy = true)
    private static final Constructor<DamageSource> entityDamageSourceCtor = ReflectionHelper.<DamageSource>getCtor(
            ReflectionHelper.<DamageSource>getMcClass("class_1285",
            "net.minecraft.world.damagesource.EntityDamageSource"), String.class, Entity.class);

    @Override
    public boolean isRemoved(Entity entity) {
        return entity.isRemoved();
    }

    @Override
    public ServerPlayer newServerPlayerEntity(MinecraftServer server, ServerLevel world, GameProfile profile) {
        return new ServerPlayer(server, world, profile);
    }

    @Override
    public CompoundTag writeBaseSpawnerNbt(BaseSpawner logic, Level world, BlockPos pos, CompoundTag nbt) {
        return logic.save(world, pos, nbt);
    }

    @Override
    public <E> Registry<E> getRegistry(RegistryAccess manager, ResourceKey<? extends Registry<E>> key) {
        return manager.registryOrThrow(key);
    }

    @Override
    public <T> boolean registryContainsId(MappedRegistry<T> registry, ResourceLocation id) {
        return registry.get(id) != null; // containsId is client-only
    }

    @Override
    public <E> ResourceLocation getKeyFromRegistry(Registry<? super E> registry, E object) {
        return registry.getKey(object);
    }

    @Override
    public CompoundTag writeBENBT(BlockEntity be) {
        return be.save(new CompoundTag());
    }

    @Override
    public <A extends CompatArgumentType<A, T, P>, T, P extends ArgumentTypeProperties<A, T, P>> void registerArgumentType
            (DeferredRegister<?> registry, String identifier, Class<A> clazz, ArgumentTypeSerialiser<A, T, P> serialiser) {
        ArgumentTypes.register(identifier, clazz, new LegacyCompatArgumentSerializer<>(serialiser));
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean tagContains(Object tag, Object obj) {
        return ((net.minecraft.tags.Tag<Object>) tag).contains(obj);
    }

    @Override
    public Biome getBiome(Level world, BlockPos pos) {
        return world.getBiome(pos);
    }

    @Override
    public BlockStateArgument createBlockStateArgument() {
        return BlockStateArgument.block();
    }

    @Override
    public ItemPredicateArgument createItemPredicateArgument() {
        return ItemPredicateArgument.itemPredicate();
    }

    @Override
    public Direction randomDirection() {
        return Direction.getRandom(new Random());
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public Map<ResourceLocation, Object> getBlockTags() {
        return blockTags == null ? blockTags = BlockTags.getAllTags().getAllTags().entrySet().stream()
                .map(entry -> new Tuple<>(entry.getKey(), (Object) entry.getValue()))
                .collect(ImmutableMap.toImmutableMap(Tuple::getA, Tuple::getB)) : blockTags;
    }

    @Override
    public DoubleStream doubleStream(DoubleList doubles) {
        return doubles.stream().mapToDouble(d -> d);
    }

    @Override
    public ResourceLocation getPaintingVariant(Painting painting) {
        return Registry.MOTIVE.getKey(painting.motive);
    }

    @Override
    public ResourceLocation nextPaintingVariant(ResourceLocation variant) {
        return Registry.MOTIVE.getKey(Registry.MOTIVE.byId((int) ((Registry.MOTIVE.getId(Registry.MOTIVE.get(variant)) + 1) %
                Registry.MOTIVE.stream().count())));
    }

    @Override
    public void setPaintingVariant(Painting entity, ResourceLocation variant) {
        entity.motive = Registry.MOTIVE.get(variant);
    }

    @Override
    public MutableComponent buildText(LiteralTextBuilder builder) {
        return PrivateCompat17.buildText(builder);
    }

    @Override
    public MutableComponent buildText(TranslatableTextBuilder builder) {
        return PrivateCompat17.buildText(builder);
    }

    @Override
    public MutableComponent buildText(EmptyTextBuilder builder) {
        return PrivateCompat17.buildText(builder);
    }

    @Override
    public TextBuilder<?> builderFromText(Component text) {
        return PrivateCompat17.builderFromText(text);
    }

    @Override
    public void broadcast(PlayerList playerManager, Tuple<Integer, ResourceLocation> type, Component message) {
        playerManager.broadcastMessage(message, ChatType.values()[type.getA()], Util.NIL_UUID);
    }

    @Override
    public void onStacksDropped(BlockState state, ServerLevel world, BlockPos pos, ItemStack stack, boolean b) {
        state.spawnAfterBreak(world, pos, stack);
    }

    @Override
    public BlockPos getWorldSpawnPos(ServerLevel world) {
        return world.getSharedSpawnPos();
    }

    @Override
    public void registerCommandRegistrationEventListener(BiConsumer<CommandDispatcher<CommandSourceStack>, Commands.CommandSelection> listener) {
        CommandRegistrationEvent.EVENT.register(listener::accept);
    }

    @Override
    public int performCommand(Commands commands, CommandSourceStack source, String command) {
        return commands.performCommand(source, command);
    }

    @Override
    public <A extends CompatArgumentType<A, T, P>, T, P extends ArgumentTypeProperties<A, T, P>> Object newArgumentTypePropertiesImpl(ArgumentTypeProperties<A, T, P> properties) {
        throw new UnsupportedOperationException("This operation is not supported on this version.");
    }

    @Override
    public <A extends CompatArgumentType<A, T, P>, T, P extends ArgumentTypeProperties<A, T, P>> Object newArgumentSerialiserImpl(ArgumentTypeSerialiser<A, T, P> serialiser) {
        return new LegacyCompatArgumentSerializer<>(serialiser);
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
    public <T> MappedRegistry<T> getBuiltInRegistry(String name) {
        return ReflectionHelper.cast(Registry.REGISTRY.get(new ResourceLocation(name)));
    }

    @Override
    public SoundEvent newSoundEvent(ResourceLocation resource) {
        return new SoundEvent(resource);
    }

    @Override
    public void setBaseSpawnerEntityId(BaseSpawner baseSpawner, EntityType<?> type, Level level, BlockPos pos) {
        baseSpawner.setEntityId(type);
    }

    @Override
    public Packet<ClientGamePacketListener> newClientboundPlayerInfoUpdatePacket(String action, ServerPlayer... players) {
        return new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.valueOf(action), players);
    }

    @Override
    public Packet<ClientGamePacketListener> newClientboundPlayerInfoRemovePacket(ServerPlayer... players) {
        return newClientboundPlayerInfoUpdatePacket("REMOVE_PLAYER", players);
    }

    @Override
    public Explosion explode(Level level, Entity entity, double x, double y, double z, float power, boolean fire, Explosion.BlockInteraction interaction) {
        return level.explode(entity, x, y, z, power, fire, interaction);
    }

    @Override
    public DamageSource getSuicideDamageSource(Entity cause) {
        return IMoreCommands.get().isServerOnly() ? DamageSource.OUT_OF_WORLD : ReflectionHelper.newInstance(getEntityDamageSourceCtor(), "suicide", cause);
    }

    @Override
    public void registerVersionSpecificCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        DamageCommand.register(dispatcher);
    }

    @Override
    public <V, T extends V> T register(Registry<V> registry, ResourceLocation resourceLocation, T object) {
        return Registry.register(registry, resourceLocation, object);
    }
}
