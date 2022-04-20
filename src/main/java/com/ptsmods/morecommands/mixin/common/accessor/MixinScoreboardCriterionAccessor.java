package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.scoreboard.ScoreboardCriterion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(ScoreboardCriterion.class)
public interface MixinScoreboardCriterionAccessor {

	@Accessor("CRITERIA")
	static Map<String, ScoreboardCriterion> getCriteria() {
		throw new AssertionError("This shouldn't happen.");
	}
	@Invoker("<init>")
	static ScoreboardCriterion newInstance(String name, boolean readOnly, ScoreboardCriterion.RenderType renderType) {
		throw new AssertionError("This shouldn't happen.");
	}
}
