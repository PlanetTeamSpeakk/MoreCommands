package com.ptsmods.morecommands.mixin.compat;

import net.minecraft.scoreboard.ScoreboardCriterion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ScoreboardCriterion.class)
public interface MixinScoreboardCriterionAccessor {
    @Accessor("CRITERIA")
    static Map<String, ScoreboardCriterion> getCriteria() {
        throw new AssertionError("This shouldn't happen.");
    }
}
