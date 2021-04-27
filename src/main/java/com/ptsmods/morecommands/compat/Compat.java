package com.ptsmods.morecommands.compat;

import com.mojang.authlib.GameProfile;
import com.ptsmods.morecommands.EarlyRiser;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;

public interface Compat {
    static Compat getCompat() {
        return CompatImpl.instance;
    }

    static String getMinecraftVersion() {
        return EarlyRiser.version.release_target;
    }

    static int getIVer() {
        return Integer.parseInt(getMinecraftVersion().split("\\.")[1]);
    }

    static boolean is16() {
        return getIVer() == 16;
    }

    static boolean is17() {
        return getIVer() == 17;
    }

    boolean isRemoved(Entity entity);

    void setRemoved(Entity entity, int reason);

    PlayerInventory getInventory(PlayerEntity player);

    char gameMsgCharAt(ServerPlayNetworkHandler thiz, String string, int index, ServerPlayerEntity player, MinecraftServer server);

    boolean isInBuildLimit(World world, BlockPos pos);

    Text toText(NbtElement tag);

    void bufferBuilderBegin(BufferBuilder builder, int drawMode, VertexFormat format);

    PlayerAbilities getAbilities(PlayerEntity player);

    ServerPlayerEntity newServerPlayerEntity(MinecraftServer server, ServerWorld world, GameProfile profile);

    NbtCompound writeSpawnerLogicNbt(MobSpawnerLogic logic, World world, BlockPos pos, NbtCompound nbt);

    void readSpawnerLogicNbt(MobSpawnerLogic logic, World world, BlockPos pos, NbtCompound nbt);

    void setSignEditor(SignBlockEntity sbe, PlayerEntity player);

    int getEntityId(Entity entity);

    <E> Registry<E> getRegistry(DynamicRegistryManager manager, RegistryKey<? extends Registry<E>> key);
}
