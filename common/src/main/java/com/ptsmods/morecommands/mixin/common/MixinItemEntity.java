package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity extends Entity {
    @Shadow private UUID thrower;

    public MixinItemEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(at = @At("RETURN"), method = "fireImmune", cancellable = true)
    public void isFireImmune(CallbackInfoReturnable<Boolean> cbi) {
        if (cbi.getReturnValueZ()) return;

        Entity thrower = this.thrower == null ? null : level.getEntitiesOfClass(Entity.class, level.getWorldBorder().getCollisionShape().bounds(),
                e -> this.thrower.equals(Compat.get().getUUID(e))).stream().findAny().orElse(null);

        if ((thrower == null ? !level.getGameRules().getBoolean(MoreGameRules.get().doItemsFireDamageRule())
                : MoreGameRules.get().checkBooleanWithPerm(level.getGameRules(), MoreGameRules.get().doItemsFireDamageRule(), thrower)))
            cbi.setReturnValue(true);
    }
}
