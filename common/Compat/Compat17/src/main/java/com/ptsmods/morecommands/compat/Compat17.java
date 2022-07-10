package com.ptsmods.morecommands.compat;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SignBlockEntity;
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
    public Inventory getInventory(Player player) {
        return player.getInventory();
    }

    @Override
    public boolean isInBuildLimit(Level world, BlockPos pos) {
        return world.isInWorldBounds(pos);
    }

    @Override
    public Component toText(Tag tag) {
        return NbtUtils.toPrettyComponent(tag);
    }

    @Override
    public ServerPlayer newServerPlayerEntity(MinecraftServer server, ServerLevel world, GameProfile profile) {
        return new ServerPlayer(server, world, profile);
    }

    @Override
    public CompoundTag writeSpawnerLogicNbt(BaseSpawner logic, Level world, BlockPos pos, CompoundTag nbt) {
        return logic.save(world, pos, nbt);
    }

    @Override
    public void readSpawnerLogicNbt(BaseSpawner logic, Level world, BlockPos pos, CompoundTag nbt) {
        logic.load(world, pos, nbt);
    }

    @Override
    public void setSignEditor(SignBlockEntity sbe, Player player) {
        sbe.setAllowedPlayerEditor(player.getUUID());
    }

    @Override
    public <E> Registry<E> getRegistry(RegistryAccess manager, ResourceKey<? extends Registry<E>> key) {
        return manager.registryOrThrow(key); // Signature changed, method now returns a Registry rather than a MutableRegistry.
    }

    @Override
    public int getWorldHeight(BlockGetter world) {
        return world.getHeight();
    }

    @Override
    public LargeFireball newFireballEntity(Level world, LivingEntity owner, double velocityX, double velocityY, double velocityZ, int explosionPower) {
        return new LargeFireball(world, owner, velocityX, velocityY, velocityZ, explosionPower);
    }

    @Override
    public String getProcessorString() {
        return String.valueOf(new SystemInfo().getHardware().getProcessor());
    }

    @Override
    public void playerSetWorld(ServerPlayer player, ServerLevel world) {
        player.setLevel(world);
    }

    @Override
    public ClientboundPlayerInfoPacket newPlayerListS2CPacket(int action, ServerPlayer... players) {
        return new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.values()[action], players);
    }
}
