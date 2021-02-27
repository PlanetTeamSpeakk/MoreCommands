package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public class MixinItemEntity {

    @Inject(at = @At("RETURN"), method = "isFireImmune()Z")
    public boolean isFireImmune(CallbackInfoReturnable<Boolean> cbi) {
        return !ReflectionHelper.<ItemEntity>cast(this).getEntityWorld().getGameRules().getBoolean(MoreCommands.doItemsFireDamageRule) || cbi.getReturnValueZ();
    }

}
