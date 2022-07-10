package com.ptsmods.morecommands.compat;

import com.google.common.collect.ImmutableMap;
import com.mojang.authlib.GameProfile;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeProperties;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeSerialiser;
import com.ptsmods.morecommands.api.arguments.CompatArgumentType;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.api.util.text.EmptyTextBuilder;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.api.util.text.TextBuilder;
import com.ptsmods.morecommands.api.util.text.TranslatableTextBuilder;
import dev.architectury.registry.registries.DeferredRegister;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.Util;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.players.PlayerList;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.Motive;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import oshi.SystemInfo;

import java.util.Map;
import java.util.Random;
import java.util.stream.DoubleStream;

public class Compat16 implements Compat {
    private static Map<ResourceLocation, Object> blockTags = null;

    @Override
    public boolean isRemoved(Entity entity) {
        return entity.removed;
    }

    @Override
    public void setRemoved(Entity entity, int reason) {
        entity.removed = reason >= 0;
    }

    @Override
    public Inventory getInventory(Player player) {
        return player.inventory;
    }

    @Override
    public boolean isInBuildLimit(Level world, BlockPos pos) {
        return Level.isInWorldBounds(pos);
    }

    @Override
    public Component toText(Tag tag) {
        return tag.getPrettyDisplay();
    }

    @Override
    public ServerPlayer newServerPlayerEntity(MinecraftServer server, ServerLevel world, GameProfile profile) {
        ServerPlayerGameMode interactionManager = new ServerPlayerGameMode(world);
        ServerPlayer player = new ServerPlayer(server, world, profile, interactionManager);
        interactionManager.player = player;
        return player;
    }

    @Override
    public CompoundTag writeSpawnerLogicNbt(BaseSpawner logic, Level world, BlockPos pos, CompoundTag nbt) {
        return logic.save(nbt);
    }

    @Override
    public void readSpawnerLogicNbt(BaseSpawner logic, Level world, BlockPos pos, CompoundTag nbt) {
        logic.load(nbt);
    }

    @Override
    public void setSignEditor(SignBlockEntity sbe, Player player) {
        sbe.setAllowedPlayerEditor(player);
    }

    @Override
    public <E> Registry<E> getRegistry(RegistryAccess manager, ResourceKey<? extends Registry<E>> key) {
        return manager.registryOrThrow(key);
    }

    @Override
    public int getWorldHeight(BlockGetter world) {
        return world.getMaxBuildHeight();
    }

    @Override
    public LargeFireball newFireballEntity(Level world, LivingEntity owner, double velocityX, double velocityY, double velocityZ, int explosionPower) {
        LargeFireball fireball = new LargeFireball(world, owner, velocityX, velocityY, velocityZ);
        fireball.explosionPower = explosionPower;
        return fireball;
    }

    @Override
    public String getProcessorString() {
        return String.valueOf(new SystemInfo().getHardware().getProcessors()[0]);
    }

    @Override
    public <T> boolean registryContainsId(MappedRegistry<T> registry, ResourceLocation id) {
        return registry.get(id) != null; // containsId is client-only
    }

    @Override
    public void playerSetWorld(ServerPlayer player, ServerLevel world) {
        player.setLevel(world);
    }

    @Override
    public ClientboundPlayerInfoPacket newPlayerListS2CPacket(int action, ServerPlayer... players) {
        return new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.values()[action], players);
    }

    @Override
    public CompoundTag writeBENBT(BlockEntity be) {
        return be.save(new CompoundTag());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A extends CompatArgumentType<A, T, P>, T, P extends ArgumentTypeProperties<A, T, P>> void registerArgumentType
            (DeferredRegister<?> registry, String identifier, Class<A> clazz, ArgumentTypeSerialiser<A, T, P> serialiser) {
        ArgumentTypes.register(identifier, clazz, (ArgumentSerializer<A>) serialiser.toLegacyVanillaSerialiser());
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
    public BlockStateArgument createBlockStateArgumentType() {
        return BlockStateArgument.block();
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
    public Object getPaintingVariant(Painting painting) {
        return painting.motive;
    }

    @Override
    public void setPaintingVariant(Painting entity, Object variant) {
        entity.motive = (Motive) variant;
    }

    @Override
    public MutableComponent buildText(LiteralTextBuilder builder) {
        return PrivateCompat16.buildText(builder);
    }

    @Override
    public MutableComponent buildText(TranslatableTextBuilder builder) {
        return PrivateCompat16.buildText(builder);
    }

    @Override
    public MutableComponent buildText(EmptyTextBuilder builder) {
        return PrivateCompat16.buildText(builder);
    }

    @Override
    public TextBuilder<?> builderFromText(Component text) {
        return PrivateCompat16.builderFromText(text);
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
}
