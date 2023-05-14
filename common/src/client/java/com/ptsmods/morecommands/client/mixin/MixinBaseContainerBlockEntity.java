package com.ptsmods.morecommands.client.mixin;

import com.google.common.collect.ImmutableList;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.addons.CachedContainerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

@Mixin(BaseContainerBlockEntity.class)
public class MixinBaseContainerBlockEntity implements CachedContainerBlockEntity {
    private @Unique List<ItemStack> cache;

    @Override
    public List<ItemStack> getCache() {
        return cache;
    }

    @Override
    public void setCache(List<ItemStack> cache) {
        this.cache = cache == null ? null : ImmutableList.copyOf(cache);
    }

    @Override
    public int contains(Predicate<ItemStack> predicate) {
        // On singleplayer, just acquire the server-side version of this block entity from the integrated server and check that.
        IntegratedServer integrated = Minecraft.getInstance().getSingleplayerServer();
        if (integrated == null) return getCache() == null ? 0 : getCache().stream().anyMatch(predicate) ? 2 : 1;

        return integrated.getPlayerList().getPlayers().stream()
                .filter(p -> p.getUUID().equals(Objects.requireNonNull(Minecraft.getInstance().player, "player").getUUID()))
                .findFirst()
                .map(p -> {
                    AtomicReference<BlockEntity> be = new AtomicReference<>();
                    // getBlockEntity only returns null when not on the right thread.
                    p.server.executeBlocking(() -> be.setPlain(p.level.getBlockEntity(ReflectionHelper.<BlockEntity>cast(this).getBlockPos())));
                    return ((BaseContainerBlockEntity) Objects.requireNonNull(be.get(), "block entity")).hasAnyMatching(predicate);
                })
                .orElseGet(() -> getCache() != null && getCache().stream().anyMatch(predicate)) ? 2 : 1;
    }
}
