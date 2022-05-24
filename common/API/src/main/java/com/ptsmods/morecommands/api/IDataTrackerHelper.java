package com.ptsmods.morecommands.api;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;
import java.util.UUID;

public interface IDataTrackerHelper {
    @SuppressWarnings("deprecation") // Holder not API
    static IDataTrackerHelper get() {
        return Holder.getDataTrackerHelper();
    }

    TrackedData<Boolean> mayFly();
    TrackedData<Boolean> invulnerable();
    TrackedData<Boolean> superpickaxe();
    TrackedData<Boolean> vanish();
    TrackedData<Boolean> vanishToggled();
    TrackedData<Optional<BlockPos>> chair();
    TrackedData<NbtCompound> vaults();
    TrackedData<Optional<Text>> nickname();
    TrackedData<Optional<UUID>> speedModifier();
    TrackedData<Boolean> jesus();
}
