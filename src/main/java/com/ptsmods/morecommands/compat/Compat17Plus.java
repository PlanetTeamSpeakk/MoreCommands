package com.ptsmods.morecommands.compat;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;

class Compat17Plus implements Compat {
    static final Compat17Plus instance;

    static {
        instance = Compat.is16() ? null : new Compat17Plus();
    }

    private Compat17Plus() {} // Private constructor

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
    public char gameMsgCharAt(ServerPlayNetworkHandler thiz, String string, int index, ServerPlayerEntity player, MinecraftServer server) {
        return Compat.getCompat().gameMsgCharAt(thiz, string, index, player, server);
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
    public void bufferBuilderBegin(BufferBuilder builder, int drawMode, VertexFormat format) {
        builder.begin(VertexFormat.DrawMode.values()[drawMode], format);
    }

    @Override
    public PlayerAbilities getAbilities(PlayerEntity player) {
        return player.getAbilities();
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
    public int getEntityId(Entity entity) {
        return Compat.getCompat().getEntityId(entity);
    }

    @Override
    public <E> Registry<E> getRegistry(DynamicRegistryManager manager, RegistryKey<? extends Registry<E>> key) {
        return manager.get(key);
    }
}