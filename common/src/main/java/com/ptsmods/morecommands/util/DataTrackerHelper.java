package com.ptsmods.morecommands.util;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.Holder;
import com.ptsmods.morecommands.api.IDataTrackerHelper;
import com.ptsmods.morecommands.util.tuples.TriConsumer;
import com.ptsmods.morecommands.util.tuples.TriFunction;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public enum DataTrackerHelper implements IDataTrackerHelper {
    INSTANCE;

    private final EntityDataAccessor<Boolean> MAY_FLY = registerData(Player.class, EntityDataSerializers.BOOLEAN, false, "MayFly", CompoundTag::getBoolean, CompoundTag::putBoolean);
    private final EntityDataAccessor<Boolean> INVULNERABLE = registerData(Player.class, EntityDataSerializers.BOOLEAN, false, "Invulnerable", CompoundTag::getBoolean, CompoundTag::putBoolean);
    private final EntityDataAccessor<Boolean> SUPERPICKAXE = registerData(Player.class, EntityDataSerializers.BOOLEAN, false, "SuperPickaxe", CompoundTag::getBoolean, CompoundTag::putBoolean);
    private final EntityDataAccessor<Boolean> VANISH = registerData(Player.class, EntityDataSerializers.BOOLEAN, false, "Vanish", CompoundTag::getBoolean, CompoundTag::putBoolean);
    private final EntityDataAccessor<Boolean> VANISH_TOGGLED = registerData(Player.class, EntityDataSerializers.BOOLEAN, false, null, (BiFunction<CompoundTag, String, Boolean>) null, null);
    private final EntityDataAccessor<Optional<BlockPos>> CHAIR = registerData(Player.class, EntityDataSerializers.OPTIONAL_BLOCK_POS, Optional.empty(), "Chair",
            (nbt, key) -> Optional.of(new BlockPos(nbt.getIntArray(key)[0], nbt.getIntArray(key)[1], nbt.getIntArray(key)[2])),
            (nbt, key, data) -> data.ifPresent(pos -> nbt.putIntArray(key, new int[] {pos.getX(), pos.getY(), pos.getZ()})));
    private final EntityDataAccessor<CompoundTag> VAULTS = registerData(Player.class, EntityDataSerializers.COMPOUND_TAG, MoreCommands.wrapTag("Vaults", new ListTag()), "Vaults",
            (nbt, key) -> MoreCommands.wrapTag(key, nbt.getList(key, 9)), (nbt, key, data) -> nbt.put(key, data.get(key)));
    private final EntityDataAccessor<Optional<Component>> NICKNAME = registerData(Player.class, EntityDataSerializers.OPTIONAL_COMPONENT, Optional.empty(), "Nickname",
            (nbt, key) -> Optional.ofNullable(Component.Serializer.fromJson(nbt.getString(key))),
            (nbt, key, data) -> data.ifPresent(nickname -> nbt.putString(key, Component.Serializer.toJson(nickname))));
    private final EntityDataAccessor<Optional<UUID>> SPEED_MODIFIER = registerData(Player.class, EntityDataSerializers.OPTIONAL_UUID, Optional.of(UUID.randomUUID()), "SpeedModifier", (nbt, key, entity) -> {
                UUID id = nbt.getUUID(key);
                AttributeInstance movementSpeedInstance = Objects.requireNonNull(entity.getAttribute(Attributes.MOVEMENT_SPEED));
                if (movementSpeedInstance.getModifier(id) == null)
                    movementSpeedInstance.addPermanentModifier(new AttributeModifier(id, "MoreCommands Speed Modifier", 0, AttributeModifier.Operation.MULTIPLY_TOTAL));
                return Optional.of(id);
            }, (nbt, key, data) -> nbt.putUUID(key, data.orElse(UUID.randomUUID())));
    private final EntityDataAccessor<Boolean> JESUS = registerData(Player.class, EntityDataSerializers.BOOLEAN, false, "Jesus", CompoundTag::getBoolean, CompoundTag::putBoolean);

    public static <T> EntityDataAccessor<T> registerData(Class<? extends LivingEntity> entityClass, EntityDataSerializer<T> dataHandler, T defaultValue, @Nullable String tagKey,
                                                  @Nullable BiFunction<CompoundTag, String, T> reader, @Nullable TriConsumer<CompoundTag, String, T> writer) {
        EntityDataAccessor<T> data = SynchedEntityData.defineId(entityClass, dataHandler);
        EntriesHolder.dataEntries.computeIfAbsent(entityClass, c -> new ArrayList<>()).add(new DataTrackerEntry<>(data, defaultValue, tagKey, reader, writer));
        return data;
    }

    public static <T, E extends LivingEntity> EntityDataAccessor<T> registerData(Class<E> entityClass, EntityDataSerializer<T> dataHandler, T defaultValue, @Nullable String tagKey,
                                                                          @Nullable TriFunction<CompoundTag, String, E, T> reader, @Nullable TriConsumer<CompoundTag, String, T> writer) {
        EntityDataAccessor<T> data = SynchedEntityData.defineId(entityClass, dataHandler);
        EntriesHolder.dataEntries.computeIfAbsent(entityClass, c -> new ArrayList<>()).add(new DataTrackerEntry<>(data, defaultValue, tagKey, reader, writer));
        return data;
    }

    public static List<DataTrackerEntry<?>> getDataEntries(Class<? extends LivingEntity> clazz) {
        return EntriesHolder.dataEntries.entrySet().stream()
                .filter(entry -> entry.getKey().isAssignableFrom(clazz))
                .flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.toList());
    }

    public static void init() {
        Holder.setDataTrackerHelper(INSTANCE);
    }

    @Override
    public EntityDataAccessor<Boolean> mayFly() {
        return MAY_FLY;
    }

    @Override
    public EntityDataAccessor<Boolean> invulnerable() {
        return INVULNERABLE;
    }

    @Override
    public EntityDataAccessor<Boolean> superpickaxe() {
        return SUPERPICKAXE;
    }

    @Override
    public EntityDataAccessor<Boolean> vanish() {
        return VANISH;
    }

    @Override
    public EntityDataAccessor<Boolean> vanishToggled() {
        return VANISH_TOGGLED;
    }

    @Override
    public EntityDataAccessor<Optional<BlockPos>> chair() {
        return CHAIR;
    }

    @Override
    public EntityDataAccessor<CompoundTag> vaults() {
        return VAULTS;
    }

    @Override
    public EntityDataAccessor<Optional<Component>> nickname() {
        return NICKNAME;
    }

    @Override
    public EntityDataAccessor<Optional<UUID>> speedModifier() {
        return SPEED_MODIFIER;
    }

    @Override
    public EntityDataAccessor<Boolean> jesus() {
        return JESUS;
    }

    public static class DataTrackerEntry<T> {
        @Getter
        private final EntityDataAccessor<T> data;
        @Getter
        private final T defaultValue;
        @Getter
        private final String tagKey;
        private final BiFunction<CompoundTag, String, T> reader;
        private final TriFunction<CompoundTag, String, LivingEntity, T> entityReader;
        private final TriConsumer<CompoundTag, String, T> writer;

        public DataTrackerEntry(EntityDataAccessor<T> data, T defaultValue, String tagKey, BiFunction<CompoundTag, String, T> reader, TriConsumer<CompoundTag, String, T> writer) {
            this.data = data;
            this.defaultValue = defaultValue;
            this.tagKey = tagKey;
            this.reader = reader;
            this.entityReader = null;
            this.writer = writer;
        }

        @SuppressWarnings("unchecked")
        public <E extends LivingEntity> DataTrackerEntry(EntityDataAccessor<T> data, T defaultValue, String tagKey, TriFunction<CompoundTag, String, E, T> reader, TriConsumer<CompoundTag, String, T> writer) {
            this.data = data;
            this.defaultValue = defaultValue;
            this.tagKey = tagKey;
            this.reader = null;
            this.entityReader = (nbt, key, entity) -> reader.apply(nbt, key, (E) entity);
            this.writer = writer;
        }

        public T read(CompoundTag nbt, LivingEntity entity) {
            return reader == null ? entityReader.apply(nbt, getTagKey(), entity) : reader.apply(nbt, getTagKey());
        }

        public void write(CompoundTag nbt, T value) {
            writer.accept(nbt, getTagKey(), value);
        }
    }

    @UtilityClass
    private static class EntriesHolder {
        private final Map<Class<? extends LivingEntity>, List<DataTrackerEntry<?>>> dataEntries = new HashMap<>();
    }
}
