package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.Holder;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.api.util.extensions.ObjectExtensions;
import com.ptsmods.morecommands.api.util.text.TextBuilder;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.commands.server.elevated.SpeedCommand;
import lombok.experimental.ExtensionMethod;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@ExtensionMethod(ObjectExtensions.class)
@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity {
    @Inject(at = @At("RETURN"), method = "createPlayerAttributes()Lnet/minecraft/entity/attribute/DefaultAttributeContainer$Builder;", cancellable = true)
    private static void createPlayerAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cbi) {
        cbi.setReturnValue(cbi.getReturnValue().add(Holder.REACH_ATTRIBUTE).add(SpeedCommand.SpeedType.swimSpeedAttribute));
    }

    @Inject(at = @At("RETURN"), method = "getName()Lnet/minecraft/text/Text;", cancellable = true)
    public void getName(CallbackInfoReturnable<Text> cbi) {
        if (!MoreCommands.isCute(ReflectionHelper.cast(this))) return;
        TextBuilder<?> builder = Compat.get().builderFromText(cbi.getReturnValue());
        cbi.setReturnValue(builder.withStyle(style -> style.withFormatting(Formatting.LIGHT_PURPLE)).build());
    }

    @Inject(at = @At("HEAD"), method = "checkFallFlying()Z", cancellable = true)
    public void checkFallFlying(CallbackInfoReturnable<Boolean> cbi) {
        if (ReflectionHelper.<PlayerEntity>cast(this).getEntityWorld().isClient && ClientOptions.Tweaks.disableElytra.getValue()) cbi.setReturnValue(false);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/Team; decorateName(Lnet/minecraft/scoreboard/AbstractTeam;Lnet/minecraft/text/Text;)Lnet/minecraft/text/MutableText;"), method = "getDisplayName()Lnet/minecraft/text/Text;")
    public MutableText getDisplayName_modifyText(AbstractTeam team, Text name) {
        return Team.decorateName(team, MoreCommands.getNickname(ReflectionHelper.cast(this)).or(name));
    }
}
