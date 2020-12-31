package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity {

    private int mc_lastPing = -1;

    @Overwrite
    public Text getPlayerListName() {
        return ReflectionHelper.<ServerPlayerEntity>cast(this).getDisplayName();
    }

    @Inject(at = @At("HEAD"), method = "playerTick()V")
    public void playerTick(CallbackInfo cbi) {
        ServerPlayerEntity thiz = ReflectionHelper.cast(this);
        if (thiz.pingMilliseconds != mc_lastPing) {
            mc_lastPing = thiz.pingMilliseconds;
            updateScores(MoreCommands.LATENCY, mc_lastPing);
        }
    }

    @Shadow
    private void updateScores(ScoreboardCriterion criterion, int score) {
        throw new AssertionError("This should not happen.");
    }

}
