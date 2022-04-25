package com.ptsmods.morecommands.compat;

import com.google.common.collect.ImmutableMap;
import com.mojang.authlib.GameProfile;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeProperties;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeSerialiser;
import com.ptsmods.morecommands.api.arguments.CompatArgumentType;
import com.ptsmods.morecommands.api.compat.Compat;
import com.ptsmods.morecommands.api.text.LiteralTextBuilder;
import com.ptsmods.morecommands.api.text.TextBuilder;
import com.ptsmods.morecommands.api.text.TranslatableTextBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.BlockView;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import oshi.SystemInfo;

import java.util.Map;
import java.util.Random;
import java.util.stream.DoubleStream;

public class Compat16 implements Compat {
	private static Map<Identifier, Object> blockTags = null;

	@Override
	public boolean isRemoved(Entity entity) {
		return entity.removed;
	}

	@Override
	public void setRemoved(Entity entity, int reason) {
		entity.removed = reason >= 0;
	}

	@Override
	public PlayerInventory getInventory(PlayerEntity player) {
		return player.inventory;
	}

	@Override
	public boolean isInBuildLimit(World world, BlockPos pos) {
		return World.isInBuildLimit(pos);
	}

	@Override
	public Text toText(NbtElement tag) {
		return tag.toText();
	}

	@Override
	public ServerPlayerEntity newServerPlayerEntity(MinecraftServer server, ServerWorld world, GameProfile profile) {
		ServerPlayerInteractionManager interactionManager = new ServerPlayerInteractionManager(world);
		ServerPlayerEntity player = new ServerPlayerEntity(server, world, profile, interactionManager);
		interactionManager.player = player;
		return player;
	}

	@Override
	public NbtCompound writeSpawnerLogicNbt(MobSpawnerLogic logic, World world, BlockPos pos, NbtCompound nbt) {
		return logic.toTag(nbt);
	}

	@Override
	public void readSpawnerLogicNbt(MobSpawnerLogic logic, World world, BlockPos pos, NbtCompound nbt) {
		logic.fromTag(nbt);
	}

	@Override
	public void setSignEditor(SignBlockEntity sbe, PlayerEntity player) {
		sbe.setEditor(player);
	}

	@Override
	public <E> Registry<E> getRegistry(DynamicRegistryManager manager, RegistryKey<? extends Registry<E>> key) {
		return manager.get(key);
	}

	@Override
	public int getWorldHeight(BlockView world) {
		return world.getHeight();
	}

	@Override
	public FireballEntity newFireballEntity(World world, LivingEntity owner, double velocityX, double velocityY, double velocityZ, int explosionPower) {
		FireballEntity fireball = new FireballEntity(world, owner, velocityX, velocityY, velocityZ);
		fireball.explosionPower = explosionPower;
		return fireball;
	}

	@Override
	public String getProcessorString() {
		return String.valueOf(new SystemInfo().getHardware().getProcessors()[0]);
	}

	@Override
	public <T> boolean registryContainsId(SimpleRegistry<T> registry, Identifier id) {
		return registry.get(id) != null; // containsId is client-only
	}

	@Override
	public void playerSetWorld(ServerPlayerEntity player, ServerWorld world) {
		player.setWorld(world);
	}

	@Override
	public NbtCompound writeBENBT(BlockEntity be) {
		return be.writeNbt(new NbtCompound());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <A extends CompatArgumentType<A, T, P>, T, P extends ArgumentTypeProperties<A, T, P>> void registerArgumentType(String identifier, Class<A> clazz, ArgumentTypeSerialiser<A, T, P> serialiser) {
		ArgumentTypes.register(identifier, clazz, (ArgumentSerializer<A>) serialiser.toLegacyVanillaSerialiser());
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean tagContains(Object tag, Object obj) {
		return ((Tag<Object>) tag).contains(obj);
	}

	@Override
	public Biome getBiome(World world, BlockPos pos) {
		return world.getBiome(pos);
	}

	@Override
	public BlockStateArgumentType createBlockStateArgumentType() {
		return BlockStateArgumentType.blockState();
	}

	@Override
	public Direction randomDirection() {
		return Direction.random(new Random());
	}

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public Map<Identifier, Object> getBlockTags() {
		return blockTags == null ? blockTags = BlockTags.getTagGroup().getTags().entrySet().stream()
				.map(entry -> new Pair<>(entry.getKey(), (Object) entry.getValue()))
				.collect(ImmutableMap.toImmutableMap(Pair::getLeft, Pair::getRight)) : blockTags;
	}

	@Override
	public DoubleStream doubleStream(DoubleList doubles) {
		return doubles.stream().mapToDouble(d -> d);
	}

	@Override
	public Object getPaintingVariant(PaintingEntity painting) {
		return painting.motive;
	}

	@Override
	public void setPaintingVariant(PaintingEntity entity, Object variant) {
		entity.motive = (PaintingMotive) variant;
	}

	@Override
	public MutableText buildText(LiteralTextBuilder builder) {
		return PrivateCompat16.buildText(builder);
	}

	@Override
	public MutableText buildText(TranslatableTextBuilder builder) {
		return PrivateCompat16.buildText(builder);
	}

	@Override
	public TextBuilder<?> builderFromText(Text text) {
		return PrivateCompat16.builderFromText(text);
	}
}
