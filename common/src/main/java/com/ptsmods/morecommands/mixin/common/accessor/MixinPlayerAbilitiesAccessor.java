package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.entity.player.PlayerAbilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerAbilities.class)
public interface MixinPlayerAbilitiesAccessor {
    @Accessor("walkSpeed") void setWalkSpeed_(float speed);
    @Accessor("flySpeed") void setFlySpeed_(float speed);
}
