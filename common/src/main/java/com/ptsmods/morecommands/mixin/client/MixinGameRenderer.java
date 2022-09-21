package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.commands.server.elevated.ReachCommand;
import com.ptsmods.morecommands.mixin.common.accessor.MixinEntityAccessor;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    private static final @Unique ResourceLocation entityTargetPacketId = new ResourceLocation("morecommands:entity_target_update");
    @Shadow @Final private Minecraft minecraft;
    private @Unique Entity lastTargetedEntity = null;

    @ModifyVariable(at = @At(value = "STORE", ordinal = 0), method = "pick")
    public double updateTargetedEntity_d(double d) {
        return ReachCommand.getReach(Objects.requireNonNull(minecraft.player), false);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;pick(DFZ)Lnet/minecraft/world/phys/HitResult;"), method = "pick")
    public HitResult updateTargetedEntity_raycast(Entity entity, double reach, float tickDelta, boolean includeFluids) {
        return entity.pick(reach, tickDelta, ClientOptions.Tweaks.targetFluids.getValue() || includeFluids);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;hasFarPickRange()Z"), method = "pick")
    public boolean updateTargetedEntity_hasExtendedReach(MultiPlayerGameMode interactionManager) {
        return false;
    }

    @ModifyVariable(at = @At(value = "STORE", ordinal = 1), method = "pick")
    public boolean updateTargetedEntity_bl(boolean bl) {
        return false;
    }

    @Inject(at = @At("RETURN"), method = "pick")
    public void updateTargetedEntity(float tickDelta, CallbackInfo cbi) {
        try {
            if (!NetworkManager.canServerReceive(entityTargetPacketId) || minecraft.crosshairPickEntity == lastTargetedEntity) return;

            lastTargetedEntity = minecraft.crosshairPickEntity;
            NetworkManager.sendToServer(entityTargetPacketId, new FriendlyByteBuf(Unpooled.buffer()).writeVarInt(lastTargetedEntity == null ? -1 : ((MixinEntityAccessor) lastTargetedEntity).getId_()));
        } catch (IllegalStateException ignored) {} // Server is not yet ready, we're in a setup phase.
    }
}
