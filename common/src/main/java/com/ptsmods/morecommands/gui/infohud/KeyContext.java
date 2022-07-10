package com.ptsmods.morecommands.gui.infohud;

import com.mojang.datafixers.util.Either;
import lombok.Data;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import java.util.Objects;

@Data
public class KeyContext {
    private final Minecraft client;
    private final Either<BlockHitResult, EntityHitResult> hit;

    @NonNull
    public ClientLevel getWorld() {
        return Objects.requireNonNull(getClient().level);
    }

    @NonNull
    public LocalPlayer getPlayer() {
        return Objects.requireNonNull(getClient().player);
    }

    @NonNull
    public MultiPlayerGameMode getInteractionManager() {
        return Objects.requireNonNull(getClient().gameMode);
    }
}
