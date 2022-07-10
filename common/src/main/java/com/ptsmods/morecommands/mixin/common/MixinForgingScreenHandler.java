package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.commands.server.elevated.ReachCommand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ItemCombinerMenu.class)
public class MixinForgingScreenHandler {
    @ModifyConstant(method = "method_24924", constant = @Constant(doubleValue = 64.0D))
    public double method_24924_maxReach(double d, Player player, Level world, BlockPos pos) {
        return ReachCommand.getReach(player, true);
    }
}
