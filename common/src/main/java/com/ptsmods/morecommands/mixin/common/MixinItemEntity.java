package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity extends Entity {
    @Shadow private UUID thrower;

    public MixinItemEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(at = @At("RETURN"), method = "isFireImmune()Z")
    public boolean isFireImmune(CallbackInfoReturnable<Boolean> cbi) {
        Entity thrower = this.thrower == null ? null : world.getEntitiesByClass(Entity.class, world.getWorldBorder().asVoxelShape().getBoundingBox(),
                e -> this.thrower.equals(e.getUuid())).stream().findAny().orElse(null);

        return cbi.getReturnValueZ() || (thrower == null ? !world.getGameRules().getBoolean(MoreGameRules.get().doItemsFireDamageRule())
                : MoreGameRules.get().checkBooleanWithPerm(world.getGameRules(), MoreGameRules.get().doItemsFireDamageRule(), thrower));
    }
}
