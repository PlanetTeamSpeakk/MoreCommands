package com.ptsmods.morecommands.mixin.reach;

import com.ptsmods.morecommands.commands.elevated.ReachCommand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Item.class)
public class MixinItem {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"), method = "getPlayerPOVHitResult")
    private static Vec3 raycast_add(Vec3 parent, double x, double y, double z, Level world, Player player, ClipContext.Fluid fluidHandling) {
        double reach = ReachCommand.getReach(player, false);
        return parent.add(x/5 * reach, y/5 * reach, z/5 * reach);
    }
}
