package com.ptsmods.morecommands.client.gui.infohud;

import com.mojang.datafixers.util.Either;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import java.util.Objects;

public record KeyContext(Minecraft client, Either<BlockHitResult, EntityHitResult> hit) {
    @NonNull
    public ClientLevel getWorld() {
        return Objects.requireNonNull(client().level);
    }

    @NonNull
    public LocalPlayer getPlayer() {
        return Objects.requireNonNull(client().player);
    }

    @NonNull
    public MultiPlayerGameMode getInteractionManager() {
        return Objects.requireNonNull(client().gameMode);
    }
}
