package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.miscellaneous.CustomPrimedTnt;
import net.minecraft.world.entity.item.PrimedTnt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PrimedTnt.class)
public class MixinPrimedTnt {

    @ModifyConstant(method = "explode", constant = @Constant(floatValue = 4f), expect = 2)
    private float explode(float power) {
        PrimedTnt thiz = ReflectionHelper.cast(this);
        return thiz instanceof CustomPrimedTnt cpt ? cpt.getPower() : power;
    }
}
