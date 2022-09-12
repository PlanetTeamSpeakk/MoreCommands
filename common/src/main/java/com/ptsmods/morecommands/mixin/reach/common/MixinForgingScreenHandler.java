package com.ptsmods.morecommands.mixin.reach.common;

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
    @ModifyConstant(method = {"method_24924", "m_39783_"}, constant = @Constant(doubleValue = 64.0D), remap = false, require = 1)
    public double method_24924_maxReach(double d, Player player, Level world, BlockPos pos) {
        return ReachCommand.getReach(player, true);
    }
}