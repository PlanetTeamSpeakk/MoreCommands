package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Player.class)
public interface MixinPlayerEntityAccessor {

    @Accessor("DATA_PLAYER_MODE_CUSTOMISATION")
    static EntityDataAccessor<Byte> getDataPlayerModeCustomisation() {
        throw new AssertionError("This shouldn't happen.");
    }

    @Accessor("inventory")
    Inventory getInventory_();

    @Accessor("abilities")
    Abilities getAbilities_();
}
