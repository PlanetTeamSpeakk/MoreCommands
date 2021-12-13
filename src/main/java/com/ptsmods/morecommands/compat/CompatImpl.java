package com.ptsmods.morecommands.compat;

import com.mojang.authlib.GameProfile;
import com.ptsmods.morecommands.mixin.compat.MixinPlayerEntityAccessor;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
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

class CompatImpl extends AbstractCompat {
    static final CompatImpl instance = new CompatImpl();

    @Override
    public boolean isRemoved(Entity entity) {
        return (Compat.is16() ? Compat16.instance : Compat17Plus.instance).isRemoved(entity);
    }

    @Override
    public void setRemoved(Entity entity, int reason) {
        (Compat.is16() ? Compat16.instance : Compat17Plus.instance).setRemoved(entity, reason);
    }

    @Override
    public PlayerInventory getInventory(PlayerEntity player) {
        return ((MixinPlayerEntityAccessor) player).getInventory_();
    }

    @Override
    public boolean isInBuildLimit(World world, BlockPos pos) {
        return (Compat.is16() ? Compat16.instance : Compat17Plus.instance).isInBuildLimit(world, pos); // Method is static on 1.16
    }

    @Override
    public Text toText(NbtElement tag) {
        return (Compat.is16() ? Compat16.instance : Compat17Plus.instance).toText(tag);
    }

    @Override
    public PlayerAbilities getAbilities(PlayerEntity player) {
        return ((MixinPlayerEntityAccessor) player).getAbilities_();
    }

    @Override
    public ServerPlayerEntity newServerPlayerEntity(MinecraftServer server, ServerWorld world, GameProfile profile) {
        return (Compat.is16() ? Compat16.instance : Compat17Plus.instance).newServerPlayerEntity(server, world, profile);
    }

    @Override
    public NbtCompound writeSpawnerLogicNbt(MobSpawnerLogic logic, World world, BlockPos pos, NbtCompound nbt) {
        return (Compat.is16() ? Compat16.instance : Compat.is17() ? Compat17Plus.instance : Compat18Plus.instance).writeSpawnerLogicNbt(logic, world, pos, nbt);
    }

    @Override
    public void readSpawnerLogicNbt(MobSpawnerLogic logic, World world, BlockPos pos, NbtCompound nbt) {
        (Compat.is16() ? Compat16.instance : Compat17Plus.instance).readSpawnerLogicNbt(logic, world, pos, nbt);
    }

    @Override
    public void setSignEditor(SignBlockEntity sbe, PlayerEntity player) {
        (Compat.is16() ? Compat16.instance : Compat17Plus.instance).setSignEditor(sbe, player);
    }

    @Override
    public <E> Registry<E> getRegistry(DynamicRegistryManager manager, RegistryKey<? extends Registry<E>> key) {
        return (Compat.is16() ? Compat16.instance : Compat17Plus.instance).getRegistry(manager, key);
    }

    @Override
    public int getWorldHeight(BlockView world) {
        return (Compat.is16() ? Compat16.instance : Compat17Plus.instance).getWorldHeight(world);
    }

    @Override
    public FireballEntity newFireballEntity(World world, LivingEntity owner, double velocityX, double velocityY, double velocityZ, int explosionPower) {
        return (Compat.is16() ? Compat16.instance : Compat17Plus.instance).newFireballEntity(world, owner, velocityX, velocityY, velocityZ, explosionPower);
    }

    @Override
    public String getProcessorString() {
        return (Compat.is16() ? Compat16.instance : Compat17Plus.instance).getProcessorString();
    }

    @Override
    public void playerSetWorld(ServerPlayerEntity player, ServerWorld world) {
        (Compat.is16() ? Compat16.instance : Compat17Plus.instance).playerSetWorld(player, world);
    }
}
