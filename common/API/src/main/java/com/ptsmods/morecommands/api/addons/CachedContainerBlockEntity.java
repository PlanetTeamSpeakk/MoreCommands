package com.ptsmods.morecommands.api.addons;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public interface CachedContainerBlockEntity {
    AtomicReference<BlockPos> WAITING = new AtomicReference<>();

    List<ItemStack> getCache();

    void setCache(List<ItemStack> cache);

    int contains(Predicate<ItemStack> predicate);
}
