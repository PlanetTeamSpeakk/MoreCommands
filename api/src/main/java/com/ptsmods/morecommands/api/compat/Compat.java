package com.ptsmods.morecommands.api.compat;

import com.mojang.authlib.GameProfile;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeProperties;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeSerialiser;
import com.ptsmods.morecommands.api.arguments.CompatArgumentType;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
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

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.DoubleStream;

public interface Compat {

    boolean isRemoved(Entity entity);

    void setRemoved(Entity entity, int reason);

    PlayerInventory getInventory(PlayerEntity player);

    boolean isInBuildLimit(World world, BlockPos pos);

    Text toText(NbtElement tag);

    ServerPlayerEntity newServerPlayerEntity(MinecraftServer server, ServerWorld world, GameProfile profile);

    NbtCompound writeSpawnerLogicNbt(MobSpawnerLogic logic, World world, BlockPos pos, NbtCompound nbt);

    void readSpawnerLogicNbt(MobSpawnerLogic logic, World world, BlockPos pos, NbtCompound nbt);

    void setSignEditor(SignBlockEntity sbe, PlayerEntity player);

    <E> Registry<E> getRegistry(DynamicRegistryManager manager, RegistryKey<? extends Registry<E>> key);

    int getWorldHeight(BlockView world);

    FireballEntity newFireballEntity(World world, LivingEntity owner, double velocityX, double velocityY, double velocityZ, int explosionPower);

    String getProcessorString();

    <T> boolean registryContainsId(SimpleRegistry<T> registry, Identifier id);

    void playerSetWorld(ServerPlayerEntity player, ServerWorld world);

    default PlayerListS2CPacket newPlayerListS2CPacket(int action, ServerPlayerEntity... players) {
        Class<?> actionClass = Arrays.stream(PlayerListS2CPacket.class.getClasses())
                .filter(c -> c.getEnumConstants() != null)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not find Action inner class of PlayerListS2CPacket class."));
        return ReflectionHelper.newInstance(ReflectionHelper.getCtor(PlayerListS2CPacket.class, actionClass, ServerPlayerEntity[].class), actionClass.getEnumConstants()[action], players);
    }

    NbtCompound writeBENBT(BlockEntity be);

    <A extends CompatArgumentType<A, T, P>, T, P extends ArgumentTypeProperties<A, T, P>> void registerArgumentType
            (String identifier, Class<A> clazz, ArgumentTypeSerialiser<A, T, P> serialiser);

    boolean tagContains(Object tag, Object obj);

    default boolean tagContains(Identifier identifier, Object obj) {
        return tagContains(getBlockTags().get(identifier), obj);
    }

    Biome getBiome(World world, BlockPos pos);

    BlockStateArgumentType createBlockStateArgumentType();

    Direction randomDirection();

    default <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    Map<Identifier, Object> getBlockTags();

    DoubleStream doubleStream(DoubleList doubles);
}
