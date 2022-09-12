package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.util.DataTrackerHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    // Data related mixins
    @SuppressWarnings("rawtypes")
    @Inject(at = @At("RETURN"), method = "defineSynchedData")
    public void initDataTracker(CallbackInfo cbi) {
        SynchedEntityData tracker = ReflectionHelper.<LivingEntity>cast(this).getEntityData();
        DataTrackerHelper.getDataEntries(ReflectionHelper.<LivingEntity>cast(this).getClass()).forEach(entry ->
                tracker.define(((DataTrackerHelper.DataTrackerEntry) entry).getData(), entry.getDefaultValue()));
    }

    @Inject(at = @At("RETURN"), method = "readAdditionalSaveData")
    public void readCustomDataFromNbt(CompoundTag nbt, CallbackInfo cbi) {
        if (DataTrackerHelper.getDataEntries(ReflectionHelper.<LivingEntity>cast(this).getClass()).isEmpty()) return;

        SynchedEntityData tracker = ReflectionHelper.<LivingEntity>cast(this).getEntityData();
        CompoundTag dataNbt = nbt.contains("MoreCommandsData") ? nbt.getCompound("MoreCommandsData") : nbt;

        DataTrackerHelper.getDataEntries(ReflectionHelper.<LivingEntity>cast(this).getClass()).stream()
                .filter(entry -> entry.getTagKey() != null)
                .forEach(entry -> {
                    if (dataNbt.contains(entry.getTagKey())) tracker.set(((DataTrackerHelper.DataTrackerEntry) entry).getData(), entry.read(dataNbt, ReflectionHelper.cast(this)));
                });
    }

    @Inject(at = @At("RETURN"), method = "addAdditionalSaveData")
    public void writeCustomDataToNbt(CompoundTag nbt, CallbackInfo cbi) {
        if (DataTrackerHelper.getDataEntries(ReflectionHelper.<LivingEntity>cast(this).getClass()).isEmpty()) return;

        SynchedEntityData tracker = ReflectionHelper.<LivingEntity>cast(this).getEntityData();
        CompoundTag dataNbt = new CompoundTag();

        DataTrackerHelper.getDataEntries(ReflectionHelper.<LivingEntity>cast(this).getClass()).stream()
                .filter(entry -> entry.getTagKey() != null)
                .forEach(entry -> ((DataTrackerHelper.DataTrackerEntry) entry).write(dataNbt, tracker.get(entry.getData())));

        nbt.put("MoreCommandsData", dataNbt);
    }
}
