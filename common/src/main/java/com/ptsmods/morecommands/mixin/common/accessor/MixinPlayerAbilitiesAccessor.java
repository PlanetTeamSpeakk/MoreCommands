package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.world.entity.player.Abilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Abilities.class)
public interface MixinPlayerAbilitiesAccessor {
    @Accessor("walkingSpeed") void setWalkingSpeed_(float speed);
    @Accessor("flyingSpeed") void setFlyingSpeed_(float speed);
}
