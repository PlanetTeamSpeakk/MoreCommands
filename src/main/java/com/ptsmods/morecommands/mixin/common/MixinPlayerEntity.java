package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.commands.server.elevated.ReachCommand;
import com.ptsmods.morecommands.commands.server.elevated.SpeedCommand;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.util.DataTrackerHelper;
import com.ptsmods.morecommands.util.ReflectionHelper;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity {
	@Inject(at = @At("RETURN"), method = "createPlayerAttributes()Lnet/minecraft/entity/attribute/DefaultAttributeContainer$Builder;")
	private static DefaultAttributeContainer.Builder createPlayerAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cbi) {
		return cbi.getReturnValue().add(ReachCommand.reachAttribute).add(SpeedCommand.SpeedType.swimSpeedAttribute);
	}

	@Inject(at = @At("RETURN"), method = "getName()Lnet/minecraft/text/Text;", cancellable = true)
	public void getName(CallbackInfoReturnable<Text> cbi) {
		LiteralText t = (LiteralText) cbi.getReturnValue();
		if (MoreCommands.isCute(ReflectionHelper.cast(this))) cbi.setReturnValue(t.setStyle(t.getStyle().withFormatting(Formatting.LIGHT_PURPLE)));
	}

	@Inject(at = @At("HEAD"), method = "checkFallFlying()Z", cancellable = true)
	public void checkFallFlying(CallbackInfoReturnable<Boolean> cbi) {
		if (ReflectionHelper.<PlayerEntity>cast(this).getEntityWorld().isClient && ClientOptions.Tweaks.disableElytra.getValue()) cbi.setReturnValue(false);
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/Team; decorateName(Lnet/minecraft/scoreboard/AbstractTeam;Lnet/minecraft/text/Text;)Lnet/minecraft/text/MutableText;"), method = "getDisplayName()Lnet/minecraft/text/Text;")
	public MutableText getDisplayName_modifyText(AbstractTeam team, Text name) {
		return Team.decorateName(team, ReflectionHelper.<PlayerEntity>cast(this).getDataTracker().get(DataTrackerHelper.NICKNAME).orElse(name));
	}
}
