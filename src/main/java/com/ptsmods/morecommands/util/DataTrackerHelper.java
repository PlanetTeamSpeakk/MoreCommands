package com.ptsmods.morecommands.util;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.util.tuples.TriConsumer;
import com.ptsmods.morecommands.util.tuples.TriFunction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class DataTrackerHelper {
    private static final Map<Class<? extends LivingEntity>, List<DataTrackerEntry<?>>> dataEntries = new HashMap<>();
    public static final TrackedData<Boolean> MAY_FLY = registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN, false, "MayFly", NbtCompound::getBoolean, NbtCompound::putBoolean);
    public static final TrackedData<Boolean> INVULNERABLE = registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN, false, "Invulnerable", NbtCompound::getBoolean, NbtCompound::putBoolean);
    public static final TrackedData<Boolean> SUPERPICKAXE = registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN, false, "SuperPickaxe", NbtCompound::getBoolean, NbtCompound::putBoolean);
    public static final TrackedData<Boolean> VANISH = registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN, false, "Vanish", NbtCompound::getBoolean, NbtCompound::putBoolean);
    public static final TrackedData<Boolean> VANISH_TOGGLED = registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN, false, null, (BiFunction<NbtCompound, String, Boolean>) null, null);
    public static final TrackedData<Optional<BlockPos>> CHAIR = registerData(PlayerEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_POS, Optional.empty(), "Chair",
            (nbt, key) -> Optional.of(new BlockPos(nbt.getIntArray(key)[0], nbt.getIntArray(key)[1], nbt.getIntArray(key)[2])),
            (nbt, key, data) -> data.ifPresent(pos -> nbt.putIntArray(key, new int[] {pos.getX(), pos.getY(), pos.getZ()})));
    public static final TrackedData<NbtCompound> VAULTS = registerData(PlayerEntity.class, TrackedDataHandlerRegistry.TAG_COMPOUND, MoreCommands.wrapTag("Vaults", new NbtList()), "Vaults",
            (nbt, key) -> MoreCommands.wrapTag(key, nbt.getList(key, 9)), (nbt, key, data) -> nbt.put(key, data.get(key)));
    public static final TrackedData<Optional<Text>> NICKNAME = registerData(PlayerEntity.class, TrackedDataHandlerRegistry.OPTIONAL_TEXT_COMPONENT, Optional.empty(), "Nickname",
            (nbt, key) -> Optional.ofNullable(Text.Serializer.fromJson(nbt.getString(key))),
            (nbt, key, data) -> data.ifPresent(nickname -> nbt.putString(key, Text.Serializer.toJson(nickname))));
    public static final TrackedData<Optional<UUID>> SPEED_MODIFIER = registerData(PlayerEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID, Optional.of(UUID.randomUUID()), "SpeedModifier", (nbt, key, entity) -> {
                UUID id = nbt.getUuid(key);
                EntityAttributeInstance movementSpeedInstance = Objects.requireNonNull(entity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED));
                if (movementSpeedInstance.getModifier(id) == null)
                    movementSpeedInstance.addPersistentModifier(new EntityAttributeModifier(id, "MoreCommands Speed Modifier", 0, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
                return Optional.of(id);
            }, (nbt, key, data) -> nbt.putUuid(key, data.orElse(UUID.randomUUID())));
    public static final TrackedData<Boolean> JESUS = registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN, false, "Jesus", NbtCompound::getBoolean, NbtCompound::putBoolean);

    public static <T> TrackedData<T> registerData(Class<? extends LivingEntity> entityClass, TrackedDataHandler<T> dataHandler, T defaultValue, @Nullable String tagKey,
                                                  @Nullable BiFunction<NbtCompound, String, T> reader, @Nullable TriConsumer<NbtCompound, String, T> writer) {
        TrackedData<T> data = DataTracker.registerData(entityClass, dataHandler);
        dataEntries.computeIfAbsent(entityClass, c -> new ArrayList<>()).add(new DataTrackerEntry<>(data, defaultValue, tagKey, reader, writer));
        return data;
    }

    public static <T, E extends LivingEntity> TrackedData<T> registerData(Class<E> entityClass, TrackedDataHandler<T> dataHandler, T defaultValue, @Nullable String tagKey,
                                                                          @Nullable TriFunction<NbtCompound, String, E, T> reader, @Nullable TriConsumer<NbtCompound, String, T> writer) {
        TrackedData<T> data = DataTracker.registerData(entityClass, dataHandler);
        dataEntries.computeIfAbsent(entityClass, c -> new ArrayList<>()).add(new DataTrackerEntry<>(data, defaultValue, tagKey, reader, writer));
        return data;
    }

    public static List<DataTrackerEntry<?>> getDataEntries(Class<? extends LivingEntity> clazz) {
        return dataEntries.entrySet().stream()
                .filter(entry -> entry.getKey().isAssignableFrom(clazz))
                .flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.toList());
    }

    public static void init() {} // Initialise the class

    public static class DataTrackerEntry<T> {
        private final TrackedData<T> data;
        private final T defaultValue;
        private final String tagKey;
        private final BiFunction<NbtCompound, String, T> reader;
        private final TriFunction<NbtCompound, String, LivingEntity, T> entityReader;
        private final TriConsumer<NbtCompound, String, T> writer;

        public DataTrackerEntry(TrackedData<T> data, T defaultValue, String tagKey, BiFunction<NbtCompound, String, T> reader, TriConsumer<NbtCompound, String, T> writer) {
            this.data = data;
            this.defaultValue = defaultValue;
            this.tagKey = tagKey;
            this.reader = reader;
            this.entityReader = null;
            this.writer = writer;
        }

        @SuppressWarnings("unchecked")
        public <E extends LivingEntity> DataTrackerEntry(TrackedData<T> data, T defaultValue, String tagKey, TriFunction<NbtCompound, String, E, T> reader, TriConsumer<NbtCompound, String, T> writer) {
            this.data = data;
            this.defaultValue = defaultValue;
            this.tagKey = tagKey;
            this.reader = null;
            this.entityReader = (nbt, key, entity) -> reader.apply(nbt, key, (E) entity);
            this.writer = writer;
        }

        public TrackedData<T> getData() {
            return data;
        }

        public T getDefaultValue() {
            return defaultValue;
        }

        public String getTagKey() {
            return tagKey;
        }

        public T read(NbtCompound nbt, LivingEntity entity) {
            return reader == null ? entityReader.apply(nbt, getTagKey(), entity) : reader.apply(nbt, getTagKey());
        }

        public void write(NbtCompound nbt, T value) {
            writer.accept(nbt, getTagKey(), value);
        }
    }
}
