package com.ptsmods.morecommands.mixin.reach;

import com.ptsmods.morecommands.commands.elevated.ReachCommand;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Container.class)
public interface MixinContainer {
    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;stillValidBlockEntity(Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/player/Player;I)Z"),
            method = "stillValidBlockEntity(Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/player/Player;)Z", index = 2)
    private static int stillValidBlockEntity(BlockEntity be, Player player, int reach) {
        return (int) Math.ceil(ReachCommand.getReach(player, false));
    }
}
