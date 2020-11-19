package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.commands.server.elevated.ReachCommand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Item.class)
public class MixinItem {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d; add(DDD)Lnet/minecraft/util/math/Vec3d;"), method = "rayTrace(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/RayTraceContext$FluidHandling;)Lnet/minecraft/util/hit/BlockHitResult;")
    private static Vec3d rayTrace_add(Vec3d parent, double x, double y, double z, World world, PlayerEntity player, RayTraceContext.FluidHandling fluidHandling) {
        double reach = ReachCommand.getReach(player, false);
        return parent.add(x/5 * reach, y/5 * reach, z/5 * reach);
    }

}
