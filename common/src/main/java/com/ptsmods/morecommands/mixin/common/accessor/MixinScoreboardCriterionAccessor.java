package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(ObjectiveCriteria.class)
public interface MixinScoreboardCriterionAccessor {

    @Accessor("CRITERIA_CACHE")
    static Map<String, ObjectiveCriteria> getCriteria() {
        throw new AssertionError("This shouldn't happen.");
    }
    @Invoker("<init>")
    static ObjectiveCriteria newInstance(String name, boolean readOnly, ObjectiveCriteria.RenderType renderType) {
        throw new AssertionError("This shouldn't happen.");
    }
}
