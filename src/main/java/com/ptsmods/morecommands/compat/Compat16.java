package com.ptsmods.morecommands.compat;

import com.esotericsoftware.reflectasm.FieldAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.mojang.authlib.GameProfile;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.mixin.compat.compat16.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
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
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.BlockView;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

// Supposedly ReflectASM is much faster than Java reflection: https://github.com/EsotericSoftware/reflectasm#performance
// Can't use invoker and accessor mixins for methods or fields that don't exist in 1.17, it seems.
class Compat16 extends AbstractCompat {
    static final Compat16 instance;
    private static final Map<Class<?>, Constructor<?>> constructorMap = new Object2ObjectOpenHashMap<>();
    private static final Map<Class<?>, MethodAccess> methodAccessMap = new Object2ObjectOpenHashMap<>();
    private static final Map<Class<?>, FieldAccess> fieldAccessMap = new Object2ObjectOpenHashMap<>();
    private static final Map<Class<?>, Map<Pair<String, Class<?>[]>, Integer>> methodIndices = new Object2ObjectOpenHashMap<>();
    private static final Map<Class<?>, Map<String, Integer>> fieldIndices = new Object2ObjectOpenHashMap<>();

    static {
        instance = Compat.is16() ? new Compat16() : null;
    }

    private Compat16() {} // Private constructor

    @SuppressWarnings("unchecked")
    private <T> Constructor<T> getCon(Class<T> clazz, Class<?>... classes) {
        // ConstructorAccess from ReflectASM only supports no-arg constructors.
        return (Constructor<T>) constructorMap.computeIfAbsent(clazz, clazz0 -> {
            try {
                return clazz0.getConstructor(classes);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private <T> T invokeCon(Constructor<T> con, Object... params) {
        try {
            return con.newInstance(params);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            MoreCommands.log.error("Couldn't invoke constructor for " + con.getDeclaringClass().getName() + " class.", e);
            return null;
        }
    }

    private MethodAccess getMA(Class<?> clazz) {
        return methodAccessMap.computeIfAbsent(clazz, MethodAccess::get);
    }

    private FieldAccess getFA(Class<?> clazz) {
        return fieldAccessMap.computeIfAbsent(clazz, FieldAccess::get);
    }

    private int getMI(Class<?> clazz, String name, Class<?>... classes) {
        Pair<String, Class<?>[]> pair = Pair.of(name, classes);
        return methodIndices.computeIfAbsent(clazz, clazz0 -> new Object2ObjectOpenHashMap<>()).computeIfAbsent(pair, pair0 -> getMA(clazz).getIndex(name, classes));
    }

    private int getFI(Class<?> clazz, String name) {
        return fieldIndices.computeIfAbsent(clazz, clazz0 -> new Object2ObjectOpenHashMap<>()).computeIfAbsent(name, name0 -> getFA(clazz).getIndex(name0));
    }

    @Override
    public boolean isRemoved(Entity entity) {
        return getFA(Entity.class).getBoolean(entity, getFI(Entity.class, "field_5988"));
    }

    @Override
    public void setRemoved(Entity entity, int reason) {
        getFA(Entity.class).setBoolean(entity, getFI(Entity.class, "field_5988"), reason >= 0);
    }

    @Override
    public PlayerInventory getInventory(PlayerEntity player) {
        return (PlayerInventory) getFA(PlayerEntity.class).get(player, getFI(PlayerEntity.class, "field_7514"));
    }

    @Override
    public boolean isInBuildLimit(World world, BlockPos pos) {
        return (boolean) getMA(World.class).invoke(world, getMI(World.class, "method_24794", BlockPos.class), pos);
    }

    @Override
    public Text toText(NbtElement tag) {
        return (Text) getMA(tag.getClass()).invoke(tag, getMI(tag.getClass(), "method_10715"), tag);
    }

    @Override
    public void bufferBuilderBegin(BufferBuilder builder, int drawMode, VertexFormat format) {
        getMA(BufferBuilder.class).invoke(builder, getMI(BufferBuilder.class, "method_1328", int.class, VertexFormat.class), drawMode, format);
    }

    @Override
    public PlayerAbilities getAbilities(PlayerEntity player) {
        return (PlayerAbilities) getFA(PlayerEntity.class).get(player, getFI(PlayerEntity.class, "field_7503"));
    }

    @Override
    public ServerPlayerEntity newServerPlayerEntity(MinecraftServer server, ServerWorld world, GameProfile profile) {
        ServerPlayerInteractionManager interactionManager = invokeCon(getCon(ServerPlayerInteractionManager.class, ServerWorld.class), world);
        ServerPlayerEntity player = invokeCon(getCon(ServerPlayerEntity.class, MinecraftServer.class, ServerWorld.class, GameProfile.class, ServerPlayerInteractionManager.class), server, world, profile, interactionManager);
        getFA(ServerPlayerInteractionManager.class).set(interactionManager, getFI(ServerPlayerInteractionManager.class, "field_14008"), player);
        return player;
    }

    @Override
    public NbtCompound writeSpawnerLogicNbt(MobSpawnerLogic logic, World world, BlockPos pos, NbtCompound nbt) {
        return (NbtCompound) getMA(MobSpawnerLogic.class).invoke(logic, getMI(MobSpawnerLogic.class, "method_8272", NbtCompound.class));
    }

    @Override
    public void readSpawnerLogicNbt(MobSpawnerLogic logic, World world, BlockPos pos, NbtCompound nbt) {
        getMA(MobSpawnerLogic.class).invoke(logic, getMI(MobSpawnerLogic.class, "method_8280", NbtCompound.class));
    }

    @Override
    public void setSignEditor(SignBlockEntity sbe, PlayerEntity player) {
        getMA(SignBlockEntity.class).invoke(sbe, getMI(SignBlockEntity.class, "method_11306", PlayerEntity.class), player);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> Registry<E> getRegistry(DynamicRegistryManager manager, RegistryKey<? extends Registry<E>> key) {
        return (Registry<E>) getMA(DynamicRegistryManager.class).invoke(manager, getMI(DynamicRegistryManager.class, "method_30530", RegistryKey.class), key);
    }

    @Override
    public int getWorldHeight(BlockView world) {
        return (int) getMA(world.getClass()).invoke(world, getMI(world.getClass(), "method_8322"));
    }
}
