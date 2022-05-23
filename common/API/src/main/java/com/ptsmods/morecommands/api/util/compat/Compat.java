package com.ptsmods.morecommands.api.util.compat;

import com.mojang.authlib.GameProfile;
import com.ptsmods.morecommands.api.Holder;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeProperties;
import com.ptsmods.morecommands.api.arguments.ArgumentTypeSerialiser;
import com.ptsmods.morecommands.api.arguments.CompatArgumentType;
import com.ptsmods.morecommands.api.util.text.EmptyTextBuilder;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.api.util.text.TextBuilder;
import com.ptsmods.morecommands.api.util.text.TranslatableTextBuilder;
import dev.architectury.registry.registries.DeferredRegister;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.stream.DoubleStream;

public interface Compat {

    @SuppressWarnings("deprecation") // Not API
    static Compat get() {
        return Holder.getCompat();
    }

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

    PlayerListS2CPacket newPlayerListS2CPacket(int action, ServerPlayerEntity... players);

    NbtCompound writeBENBT(BlockEntity be);

    <A extends CompatArgumentType<A, T, P>, T, P extends ArgumentTypeProperties<A, T, P>> void registerArgumentType
            (DeferredRegister<?> registry, String identifier, Class<A> clazz, ArgumentTypeSerialiser<A, T, P> serialiser);

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

    Object getPaintingVariant(PaintingEntity painting);

    void setPaintingVariant(PaintingEntity entity, Object variant);

    // Text-related

    MutableText buildText(LiteralTextBuilder builder);

    MutableText buildText(TranslatableTextBuilder builder);

    MutableText buildText(EmptyTextBuilder builder);

    TextBuilder<?> builderFromText(Text text);

    void broadcast(PlayerManager playerManager, Pair<Integer, Identifier> type, Text message);

    void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack, boolean b);

    BlockPos getWorldSpawnPos(ServerWorld world);
}
