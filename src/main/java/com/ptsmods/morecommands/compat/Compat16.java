package com.ptsmods.morecommands.compat;

import com.mojang.authlib.GameProfile;
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
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

// Supposedly ReflectASM is much faster than Java reflection: https://github.com/EsotericSoftware/reflectasm#performance
// Can't use invoker and accessor mixins for methods or fields that don't exist in 1.17, it seems.
class Compat16 extends AbstractCompat {
    static final Compat16 instance;

    static {
        instance = Compat.is16() ? new Compat16() : null;
    }

    private Compat16() {} // Private constructor

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

    @Override
    public FireballEntity newFireballEntity(World world, LivingEntity owner, double velocityX, double velocityY, double velocityZ, int explosionPower) {
        FireballEntity fireball = invokeCon(getCon(FireballEntity.class, World.class, LivingEntity.class, double.class, double.class, double.class), world, owner, velocityX, velocityY, velocityZ);
        getFA(FireballEntity.class).set(fireball, getFI(FireballEntity.class, "field_7624"), explosionPower);
        return fireball;
    }

    @Override
    public String getProcessorString() {
        HardwareAbstractionLayer hardware = new SystemInfo().getHardware();
        return String.valueOf(((Object[]) getMA(hardware.getClass()).invoke(hardware, getMI(hardware.getClass(), "getProcessors")))[0]);
    }
}
