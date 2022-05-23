package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.commands.server.elevated.ReachCommand;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ScreenHandler.class)
public class MixinScreenHandler {
    @ModifyConstant(method = "method_17696", constant = @Constant(doubleValue = 64.0D))
    private static double method_17696_maxReach(double maxReach, Block block, PlayerEntity player, World world, BlockPos pos) {
        return ReachCommand.getReach(player, true);
    }
}
