package com.ptsmods.morecommands.client.reachmixin;

import com.ptsmods.morecommands.commands.elevated.ReachCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Objects;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Shadow @Final private Minecraft minecraft;

    @ModifyVariable(at = @At(value = "STORE", ordinal = 0), method = "pick")
    public double updateTargetedEntity_maxReach(double maxReach) {
        return ReachCommand.getReach(Objects.requireNonNull(minecraft.player), false);
    }
}
