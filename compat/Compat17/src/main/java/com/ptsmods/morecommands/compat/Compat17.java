package com.ptsmods.morecommands.compat;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.BlockView;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;
import oshi.SystemInfo;

public class Compat17 extends Compat16 {

	@Override
	public boolean isRemoved(Entity entity) {
		return entity.isRemoved();
	}

	@Override
	public void setRemoved(Entity entity, int reason) {
		entity.setRemoved(reason < 0 ? null : Entity.RemovalReason.values()[reason]);
	}

	@Override
	public PlayerInventory getInventory(PlayerEntity player) {
		return player.getInventory();
	}

	@Override
	public boolean isInBuildLimit(World world, BlockPos pos) {
		return world.isInBuildLimit(pos);
	}

	@Override
	public Text toText(NbtElement tag) {
		return NbtHelper.toPrettyPrintedText(tag);
	}

	@Override
	public ServerPlayerEntity newServerPlayerEntity(MinecraftServer server, ServerWorld world, GameProfile profile) {
		return new ServerPlayerEntity(server, world, profile);
	}

	@Override
	public NbtCompound writeSpawnerLogicNbt(MobSpawnerLogic logic, World world, BlockPos pos, NbtCompound nbt) {
		return logic.writeNbt(world, pos, nbt);
	}

	@Override
	public void readSpawnerLogicNbt(MobSpawnerLogic logic, World world, BlockPos pos, NbtCompound nbt) {
		logic.readNbt(world, pos, nbt);
	}

	@Override
	public void setSignEditor(SignBlockEntity sbe, PlayerEntity player) {
		sbe.setEditor(player.getUuid());
	}

	@Override
	public <E> Registry<E> getRegistry(DynamicRegistryManager manager, RegistryKey<? extends Registry<E>> key) {
		return manager.get(key); // Signature changed, method now returns a Registry rather than a MutableRegistry.
	}

	@Override
	public int getWorldHeight(BlockView world) {
		return world.getHeight();
	}

	@Override
	public FireballEntity newFireballEntity(World world, LivingEntity owner, double velocityX, double velocityY, double velocityZ, int explosionPower) {
		return new FireballEntity(world, owner, velocityX, velocityY, velocityZ, explosionPower);
	}

	@Override
	public String getProcessorString() {
		return String.valueOf(new SystemInfo().getHardware().getProcessor());
	}

	@Override
	public void playerSetWorld(ServerPlayerEntity player, ServerWorld world) {
		player.setWorld(world);
	}
}
