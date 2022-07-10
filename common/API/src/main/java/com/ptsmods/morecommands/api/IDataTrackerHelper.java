package com.ptsmods.morecommands.api;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;

public interface IDataTrackerHelper {
    @SuppressWarnings("deprecation") // Holder not API
    static IDataTrackerHelper get() {
        return Holder.getDataTrackerHelper();
    }

    EntityDataAccessor<Boolean> mayFly();
    EntityDataAccessor<Boolean> invulnerable();
    EntityDataAccessor<Boolean> superpickaxe();
    EntityDataAccessor<Boolean> vanish();
    EntityDataAccessor<Boolean> vanishToggled();
    EntityDataAccessor<Optional<BlockPos>> chair();
    EntityDataAccessor<CompoundTag> vaults();
    EntityDataAccessor<Optional<Component>> nickname();
    EntityDataAccessor<Optional<UUID>> speedModifier();
    EntityDataAccessor<Boolean> jesus();
}
