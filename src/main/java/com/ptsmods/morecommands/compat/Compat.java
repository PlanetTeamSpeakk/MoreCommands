package com.ptsmods.morecommands.compat;

import com.mojang.authlib.GameProfile;
import com.ptsmods.morecommands.EarlyRiser;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.BlockView;
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
        return EarlyRiser.version.getIVer();
    }

    static boolean is16() {
        return getIVer() == 16;
    }

    static boolean is17() {
        return getIVer() == 17;
    }

    static boolean is18() {
        return getIVer() == 18;
    }

    boolean isRemoved(Entity entity);

    void setRemoved(Entity entity, int reason);

    PlayerInventory getInventory(PlayerEntity player);

    char gameMsgCharAt(ServerPlayNetworkHandler thiz, String string, int index, ServerPlayerEntity player, MinecraftServer server);

    boolean isInBuildLimit(World world, BlockPos pos);

    Text toText(NbtElement tag);

    PlayerAbilities getAbilities(PlayerEntity player);

    ServerPlayerEntity newServerPlayerEntity(MinecraftServer server, ServerWorld world, GameProfile profile);

    NbtCompound writeSpawnerLogicNbt(MobSpawnerLogic logic, World world, BlockPos pos, NbtCompound nbt);

    void readSpawnerLogicNbt(MobSpawnerLogic logic, World world, BlockPos pos, NbtCompound nbt);

    void setSignEditor(SignBlockEntity sbe, PlayerEntity player);

    int getEntityId(Entity entity);

    <E> Registry<E> getRegistry(DynamicRegistryManager manager, RegistryKey<? extends Registry<E>> key);

    int getWorldHeight(BlockView world);

    float getEntityYaw(Entity entity);

    float getEntityPitch(Entity entity);

    void setEntityYaw(Entity entity, float yaw);

    void setEntityPitch(Entity entity, float pitch);

    void putCriterion(String name, ScoreboardCriterion criterion);

    FireballEntity newFireballEntity(World world, LivingEntity owner, double velocityX, double velocityY, double velocityZ, int explosionPower);

    String getProcessorString();

    <T> boolean registryContainsId(SimpleRegistry<T> registry, Identifier id);

    void playerSetWorld(ServerPlayerEntity player, ServerWorld world);

    PlayerListS2CPacket newPlayerListS2CPacket(int action, ServerPlayerEntity... players);

    NbtCompound writeBENBT(BlockEntity be);
}
