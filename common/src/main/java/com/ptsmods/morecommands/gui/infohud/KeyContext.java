package com.ptsmods.morecommands.gui.infohud;

import com.mojang.datafixers.util.Either;
import lombok.Data;
import lombok.NonNull;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;

import java.util.Objects;

@Data
public class KeyContext {
    private final MinecraftClient client;
    private final Either<BlockHitResult, EntityHitResult> hit;

    @NonNull
    public ClientWorld getWorld() {
        return Objects.requireNonNull(getClient().world);
    }

    @NonNull
    public ClientPlayerEntity getPlayer() {
        return Objects.requireNonNull(getClient().player);
    }

    @NonNull
    public ClientPlayerInteractionManager getInteractionManager() {
        return Objects.requireNonNull(getClient().interactionManager);
    }
}
