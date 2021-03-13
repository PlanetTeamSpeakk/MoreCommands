package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.scoreboard.ScoreboardCriterion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ScoreboardCriterion.class)
public interface MixinScoreboardCriterionAccessor {
    @Invoker("<init>")
    static ScoreboardCriterion newInstance(String name, boolean readOnly, ScoreboardCriterion.RenderType renderType) {
        throw new AssertionError("This shouldn't happen.");
    }
}
