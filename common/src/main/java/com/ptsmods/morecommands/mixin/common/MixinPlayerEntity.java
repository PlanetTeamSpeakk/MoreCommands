package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.api.util.extensions.ObjectExtensions;
import com.ptsmods.morecommands.api.util.text.TextBuilder;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import lombok.experimental.ExtensionMethod;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@ExtensionMethod(ObjectExtensions.class)
@Mixin(Player.class)
public abstract class MixinPlayerEntity {

    @Inject(at = @At("RETURN"), method = "getName", cancellable = true)
    public void getName(CallbackInfoReturnable<Component> cbi) {
        if (!MoreCommands.INSTANCE.isCute(ReflectionHelper.cast(this))) return;
        TextBuilder<?> builder = Compat.get().builderFromText(cbi.getReturnValue());
        cbi.setReturnValue(builder.withStyle(style -> style.applyFormat(ChatFormatting.LIGHT_PURPLE)).build());
    }

    @Inject(at = @At("HEAD"), method = "tryToStartFallFlying", cancellable = true)
    public void checkFallFlying(CallbackInfoReturnable<Boolean> cbi) {
        if (ReflectionHelper.<Player>cast(this).getCommandSenderWorld().isClientSide && ClientOptions.Tweaks.disableElytra.getValue()) cbi.setReturnValue(false);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/scores/PlayerTeam;formatNameForTeam(Lnet/minecraft/world/scores/Team;Lnet/minecraft/network/chat/Component;)Lnet/minecraft/network/chat/MutableComponent;"), method = "getDisplayName")
    public MutableComponent getDisplayName_modifyText(Team team, Component name) {
        return PlayerTeam.formatNameForTeam(team, MoreCommands.getNickname(ReflectionHelper.cast(this)).or(name));
    }
}
