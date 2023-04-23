package com.ptsmods.morecommands.mixin.reach;

import com.ptsmods.morecommands.commands.elevated.ReachCommand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BrewingStandBlockEntity.class)
public class MixinBrewingStandBlockEntity {
    @ModifyConstant(method = "stillValid", constant = @Constant(doubleValue = 64.0D))
    public double canPlayerUse_maxReach(double d, Player player) {
        return ReachCommand.getReach(player, true);
    }
}