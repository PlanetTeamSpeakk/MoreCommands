package com.ptsmods.morecommands.api.util.compat;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.DoubleStream;

public interface Compat {

    @SuppressWarnings("deprecation") // Not API
    static Compat get() {
        return Holder.getCompat();
    }

    boolean isRemoved(Entity entity);

    void setRemoved(Entity entity, int reason);

    Inventory getInventory(Player player);

    boolean isInBuildLimit(Level world, BlockPos pos);

    Component toText(Tag tag);

    ServerPlayer newServerPlayerEntity(MinecraftServer server, ServerLevel world, GameProfile profile);

    CompoundTag writeSpawnerLogicNbt(BaseSpawner logic, Level world, BlockPos pos, CompoundTag nbt);

    void readSpawnerLogicNbt(BaseSpawner logic, Level world, BlockPos pos, CompoundTag nbt);

    void setSignEditor(SignBlockEntity sbe, Player player);

    <E> Registry<E> getRegistry(RegistryAccess manager, ResourceKey<? extends Registry<E>> key);

    int getWorldHeight(BlockGetter world);

    LargeFireball newFireballEntity(Level world, LivingEntity owner, double velocityX, double velocityY, double velocityZ, int explosionPower);

    String getProcessorString();

    <T> boolean registryContainsId(MappedRegistry<T> registry, ResourceLocation id);

    void playerSetWorld(ServerPlayer player, ServerLevel world);

    ClientboundPlayerInfoPacket newPlayerListS2CPacket(int action, ServerPlayer... players);

    CompoundTag writeBENBT(BlockEntity be);

    <A extends CompatArgumentType<A, T, P>, T, P extends ArgumentTypeProperties<A, T, P>> void registerArgumentType
            (DeferredRegister<?> registry, String identifier, Class<A> clazz, ArgumentTypeSerialiser<A, T, P> serialiser);

    boolean tagContains(Object tag, Object obj);

    default boolean tagContains(ResourceLocation identifier, Object obj) {
        return tagContains(getBlockTags().get(identifier), obj);
    }

    Biome getBiome(Level world, BlockPos pos);

    BlockStateArgument createBlockStateArgumentType();

    Direction randomDirection();

    default <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    Map<ResourceLocation, Object> getBlockTags();

    DoubleStream doubleStream(DoubleList doubles);

    Object getPaintingVariant(Painting painting);

    void setPaintingVariant(Painting entity, Object variant);

    // Text-related

    MutableComponent buildText(LiteralTextBuilder builder);

    MutableComponent buildText(TranslatableTextBuilder builder);

    MutableComponent buildText(EmptyTextBuilder builder);

    TextBuilder<?> builderFromText(Component text);

    void broadcast(PlayerList playerManager, Tuple<Integer, ResourceLocation> type, Component message);

    void onStacksDropped(BlockState state, ServerLevel world, BlockPos pos, ItemStack stack, boolean b);

    BlockPos getWorldSpawnPos(ServerLevel world);

    void registerCommandRegistrationEventListener(BiConsumer<CommandDispatcher<CommandSourceStack>, Commands.CommandSelection> listener);

    int performCommand(Commands commands, CommandSourceStack source, String command);
}
