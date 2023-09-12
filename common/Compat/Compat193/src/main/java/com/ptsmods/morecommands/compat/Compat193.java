package com.ptsmods.morecommands.compat;

import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.ReflectionHelper;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.EnumSet;

public class Compat193 extends Compat192 {

    @Override
    public <T> MappedRegistry<T> getBuiltInRegistry(String name) {
        return ReflectionHelper.cast(BuiltInRegistries.REGISTRY.get(new ResourceLocation(name)));
    }

    @Override
    public SoundEvent newSoundEvent(ResourceLocation resource) {
        return SoundEvent.createVariableRangeEvent(resource);
    }

    @Override
    public void setBaseSpawnerEntityId(BaseSpawner baseSpawner, EntityType<?> type, Level level, BlockPos pos) {
        baseSpawner.setEntityId(type, level, RandomSource.create(), pos);
    }

    @Override
    public Packet<ClientGamePacketListener> newClientboundPlayerInfoUpdatePacket(String action, ServerPlayer... players) {
        return new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.valueOf(action)),
                Arrays.asList(players));
    }

    @Override
    public Packet<ClientGamePacketListener> newClientboundPlayerInfoRemovePacket(ServerPlayer... players) {
        return new ClientboundPlayerInfoRemovePacket(Arrays.stream(players).map(ServerPlayer::getUUID).toList());
    }

    @Override
    public Object newCommandBuildContext() {
        return CommandBuildContext.configurable(IMoreCommands.get().getServer() == null ?
                RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY) : IMoreCommands.get().getServer().registryAccess(),
                FeatureFlags.DEFAULT_FLAGS);
    }

    @Override
    public Explosion explode(Level level, Entity entity, double x, double y, double z, float power, boolean fire, Explosion.BlockInteraction interaction) {
        return level.explode(entity, x, y, z, power, fire, switch (interaction) {
            case KEEP -> Level.ExplosionInteraction.NONE;
            case DESTROY, DESTROY_WITH_DECAY -> Level.ExplosionInteraction.TNT;
        });
    }

    @Override
    public <V, T extends V> T register(Registry<V> registry, ResourceLocation resourceLocation, T object) {
        // Registry is an interface now, meaning the OP-code of this call is different.
        return Registry.register(registry, resourceLocation, object);
    }

    @Override
    public <E> ResourceLocation getKeyFromRegistry(Registry<? super E> registry, E object) {
        return registry.getKey(object);
    }
}
