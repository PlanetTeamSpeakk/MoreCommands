package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.commands.server.elevated.ReachCommand;
import com.ptsmods.morecommands.commands.server.elevated.SpeedCommand;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
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

	@Inject(at = @At("HEAD"), method = "initDataTracker()V")
	public void initDataTracker(CallbackInfo cbi) {
		DataTracker tracker = ReflectionHelper.<PlayerEntity>cast(this).getDataTracker();
		tracker.startTracking(MoreCommands.MAY_FLY, false);
		tracker.startTracking(MoreCommands.INVULNERABLE, false);
		tracker.startTracking(MoreCommands.SUPERPICKAXE, false);
		tracker.startTracking(MoreCommands.VANISH, false);
		tracker.startTracking(MoreCommands.CHAIR, Optional.empty());
		tracker.startTracking(MoreCommands.VAULTS, MoreCommands.wrapTag("Vaults", new NbtList()));
		tracker.startTracking(MoreCommands.NICKNAME, Optional.empty());
		tracker.startTracking(MoreCommands.SPEED_MODIFIER, Optional.of(UUID.randomUUID()));

		// Variables that aren't saved.
		tracker.startTracking(MoreCommands.VANISH_TOGGLED, false);
	}

	@Inject(at = @At("TAIL"), method = {"readCustomDataFromNbt", "method_5749"}, remap = false, require = 1)
	public void readCustomDataFromTag(NbtCompound tag, CallbackInfo cbi) {
		DataTracker dataTracker = ReflectionHelper.<PlayerEntity>cast(this).getDataTracker();
		if (tag.contains("MayFly", 1)) dataTracker.set(MoreCommands.MAY_FLY, tag.getBoolean("MayFly"));
		if (tag.contains("Invulnerable", 1)) dataTracker.set(MoreCommands.INVULNERABLE, tag.getBoolean("Invulnerable"));
		if (tag.contains("SuperPickaxe", 1)) dataTracker.set(MoreCommands.SUPERPICKAXE, tag.getBoolean("SuperPickaxe"));
		if (tag.contains("Vanish", 1)) dataTracker.set(MoreCommands.VANISH, tag.getBoolean("Vanish"));
		if (tag.contains("Chair", 11)) dataTracker.set(MoreCommands.CHAIR, Optional.of(new BlockPos(tag.getIntArray("Chair")[0], tag.getIntArray("Chair")[1], tag.getIntArray("Chair")[2])));
		if (tag.contains("Vaults", 9)) dataTracker.set(MoreCommands.VAULTS, MoreCommands.wrapTag("Vaults", tag.getList("Vaults", 9)));
		if (tag.contains("Nickname",8)) dataTracker.set(MoreCommands.NICKNAME, Optional.ofNullable(Text.Serializer.fromJson(tag.getString("Nickname"))));
		if (tag.contains("SpeedModifier", 11)) dataTracker.set(MoreCommands.SPEED_MODIFIER, Optional.of(UUID.randomUUID()));
		UUID speedModId = dataTracker.get(MoreCommands.SPEED_MODIFIER).orElseThrow(() -> new AssertionError("This shouldn't happen."));
		PlayerEntity thiz = ReflectionHelper.cast(this);
		if (Objects.requireNonNull(thiz.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).getModifier(speedModId) == null) Objects.requireNonNull(thiz.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).addPersistentModifier(new EntityAttributeModifier(speedModId, "MoreCommands Speed Modifier", 0, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
	}

	@Inject(at = @At("TAIL"), method = {"writeCustomDataToNbt", "method_5652"}, remap = false, require = 1)
	public void writeCustomDataToTag(NbtCompound tag, CallbackInfo cbi) {
		DataTracker dataTracker = ReflectionHelper.<PlayerEntity>cast(this).getDataTracker();
		tag.putBoolean("MayFly", dataTracker.get(MoreCommands.MAY_FLY));
		tag.putBoolean("Invulnerable", dataTracker.get(MoreCommands.INVULNERABLE));
		tag.putBoolean("SuperPickaxe", dataTracker.get(MoreCommands.SUPERPICKAXE));
		tag.putBoolean("Vanish", dataTracker.get(MoreCommands.VANISH));
		dataTracker.get(MoreCommands.CHAIR).ifPresent(pos -> tag.putIntArray("Chair", new int[] {pos.getX(), pos.getY(), pos.getZ()}));
		tag.put("Vaults", dataTracker.get(MoreCommands.VAULTS).getList("Vaults", 9));
		dataTracker.get(MoreCommands.NICKNAME).ifPresent(nick -> tag.putString("Nickname", Text.Serializer.toJson(nick)));
		tag.putUuid("SpeedModifier", dataTracker.get(MoreCommands.SPEED_MODIFIER).orElse(UUID.randomUUID()));
	}

	@Inject(at = @At("RETURN"), method = "getName()Lnet/minecraft/text/Text;")
	public Text getName(CallbackInfoReturnable<Text> cbi) {
		LiteralText t = (LiteralText) cbi.getReturnValue();
		if (MoreCommands.isCute(ReflectionHelper.cast(this))) t.setStyle(t.getStyle().withFormatting(Formatting.LIGHT_PURPLE));
		return t;
	}

	@Inject(at = @At("HEAD"), method = "checkFallFlying()Z", cancellable = true)
	public boolean checkFallFlying(CallbackInfoReturnable<Boolean> cbi) {
		if (ReflectionHelper.<PlayerEntity>cast(this).getEntityWorld().isClient && ClientOptions.Tweaks.disableElytra.getValue()) cbi.setReturnValue(false);
		return cbi.getReturnValue() != null && cbi.getReturnValue();
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/Team; decorateName(Lnet/minecraft/scoreboard/AbstractTeam;Lnet/minecraft/text/Text;)Lnet/minecraft/text/MutableText;"), method = "getDisplayName()Lnet/minecraft/text/Text;")
	public MutableText getDisplayName_modifyText(AbstractTeam team, Text name) {
		return Team.decorateName(team, ReflectionHelper.<PlayerEntity>cast(this).getDataTracker().get(MoreCommands.NICKNAME).orElse(name));
	}

}
