package com.ptsmods.morecommands.mixin.reach;

import com.ptsmods.morecommands.commands.elevated.ReachCommand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AbstractContainerMenu.class)
public class MixinAbstractContainerMenu {
    @ModifyConstant(method = {"method_17696", "m_38913_"}, require = 1, remap = false, constant = @Constant(doubleValue = 64.0D))
    private static double method_17696_maxReach(double reach, Block block, Player player, Level world, BlockPos pos) {
        return ReachCommand.getReach(player, true);
    }
}
