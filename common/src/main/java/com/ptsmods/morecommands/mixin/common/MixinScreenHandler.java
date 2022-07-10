package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.commands.server.elevated.ReachCommand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AbstractContainerMenu.class)
public class MixinScreenHandler {
    @ModifyConstant(method = "method_17696", constant = @Constant(doubleValue = 64.0D))
    private static double method_17696_maxReach(double maxReach, Block block, Player player, Level world, BlockPos pos) {
        return ReachCommand.getReach(player, true);
    }
}
