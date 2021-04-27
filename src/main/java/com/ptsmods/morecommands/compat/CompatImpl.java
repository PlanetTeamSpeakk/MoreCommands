package com.ptsmods.morecommands.compat;

import com.mojang.authlib.GameProfile;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.mixin.compat.MixinEntityAccessor;
import com.ptsmods.morecommands.mixin.compat.MixinPlayerEntityAccessor;
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

class CompatImpl implements Compat {
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
    public char gameMsgCharAt(ServerPlayNetworkHandler thiz, String string, int index, ServerPlayerEntity player, MinecraftServer server) {
        char ch = string.charAt(index);
        if (!string.startsWith("/") && ch == '\u00A7' && (thiz.player.getServerWorld().getGameRules().getBoolean(MoreCommands.doChatColoursRule) || player.hasPermissionLevel(server.getOpPermissionLevel())))
            ch = '&';
        return ch;
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
    public void bufferBuilderBegin(BufferBuilder builder, int drawMode, VertexFormat format) {
        (Compat.is16() ? Compat16.instance : Compat17Plus.instance).bufferBuilderBegin(builder, drawMode, format);
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
        return (Compat.is16() ? Compat16.instance : Compat17Plus.instance).writeSpawnerLogicNbt(logic, world, pos, nbt);
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
    public int getEntityId(Entity entity) {
        return ((MixinEntityAccessor) entity).getEntityId_();
    }

    @Override
    public <E> Registry<E> getRegistry(DynamicRegistryManager manager, RegistryKey<? extends Registry<E>> key) {
        return (Compat.is16() ? Compat16.instance : Compat17Plus.instance).getRegistry(manager, key);
    }
}
