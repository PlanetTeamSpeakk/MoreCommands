package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerEntity.class)
public interface MixinPlayerEntityAccessor {

    @Accessor("PLAYER_MODEL_PARTS")
    static TrackedData<Byte> getPlayerModelParts() {
        throw new AssertionError("This shouldn't happen.");
    }

    @Accessor("inventory")
    PlayerInventory getInventory_();

    @Accessor("abilities")
    PlayerAbilities getAbilities_();
}
