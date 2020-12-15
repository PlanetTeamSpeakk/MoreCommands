package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.commands.server.elevated.ReachCommand;
import com.ptsmods.morecommands.miscellaneous.ClientOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Shadow @Final private MinecraftClient client;

    //@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager; getReachDistance()F"), method = "updateTargetedEntity(F)V")
    @ModifyVariable(at = @At(value = "STORE", ordinal = 0), method = "updateTargetedEntity(F)V")
    public double updateTargetedEntity_d(double d) {
        return ReachCommand.getReach(client.player, false);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity; rayTrace(DFZ)Lnet/minecraft/util/hit/HitResult;"), method = "updateTargetedEntity(F)V")
    public HitResult updateTargetedEntity_rayTrace(Entity entity, double reach, float tickDelta, boolean includeFluids) {
        return entity.rayTrace(reach, tickDelta, ClientOptions.Tweaks.targetFluids || includeFluids);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager; hasExtendedReach()Z"), method = "updateTargetedEntity(F)V")
    public boolean updateTargetedEntity_hasExtendedReach(ClientPlayerInteractionManager interactionManager) {
        return false;
    }

    @ModifyVariable(at = @At(value = "STORE", ordinal = 1), method = "updateTargetedEntity(F)V")
    public boolean updateTargetedEntity_bl(boolean bl) {
        return false;
    }

}
