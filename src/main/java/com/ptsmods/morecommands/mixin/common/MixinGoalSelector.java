package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(GoalSelector.class)
public class MixinGoalSelector {
    @Inject(at = @At("HEAD"), method = "tick", cancellable = true)
    public void tick(CallbackInfo cbi) {
        if (!Objects.requireNonNull(MoreCommands.serverInstance.getWorld(World.OVERWORLD)).getGameRules().getBoolean(MoreGameRules.doGoalsRule)) cbi.cancel();
    }
}
