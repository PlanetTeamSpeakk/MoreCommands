package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.util.DataTrackerHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

	@Group(name = "canWalkOnFluid", min = 1, max = 1)
	@Inject(at = @At("HEAD"), method = "method_26319(Lnet/minecraft/class_3611;)Z", remap = false, cancellable = true)
	public void canWalkOnFluid(Fluid fluid, CallbackInfoReturnable<Boolean> cbi) {
		if (canWalkOnFluid()) cbi.setReturnValue(true);
	}

	@Group(name = "canWalkOnFluid", min = 1, max = 1)
	@Inject(at = @At("HEAD"), method = "canWalkOnFluid", cancellable = true)
	public void canWalkOnFluid(FluidState fluidState, CallbackInfoReturnable<Boolean> cbi) {
		if (canWalkOnFluid()) cbi.setReturnValue(true);
	}

	private boolean canWalkOnFluid() {
		return ReflectionHelper.<LivingEntity>cast(this) instanceof PlayerEntity && ReflectionHelper.<LivingEntity>cast(this).getDataTracker().get(DataTrackerHelper.JESUS);
	}

	// Data related mixins
	@SuppressWarnings("rawtypes")
	@Inject(at = @At("RETURN"), method = "initDataTracker")
	public void initDataTracker(CallbackInfo cbi) {
		DataTracker tracker = ReflectionHelper.<LivingEntity>cast(this).getDataTracker();
		DataTrackerHelper.getDataEntries(ReflectionHelper.<LivingEntity>cast(this).getClass()).forEach(entry ->
				tracker.startTracking(((DataTrackerHelper.DataTrackerEntry) entry).getData(), entry.getDefaultValue()));
	}

	@Inject(at = @At("RETURN"), method = {"readCustomDataFromNbt", "method_5749"}, remap = false, require = 1)
	public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo cbi) {
		if (DataTrackerHelper.getDataEntries(ReflectionHelper.<LivingEntity>cast(this).getClass()).isEmpty()) return;

		DataTracker tracker = ReflectionHelper.<LivingEntity>cast(this).getDataTracker();
		NbtCompound dataNbt = nbt.contains("MoreCommandsData") ? nbt.getCompound("MoreCommandsData") : nbt;

		DataTrackerHelper.getDataEntries(ReflectionHelper.<LivingEntity>cast(this).getClass()).stream()
				.filter(entry -> entry.getTagKey() != null)
				.forEach(entry -> {
					if (dataNbt.contains(entry.getTagKey())) tracker.set(((DataTrackerHelper.DataTrackerEntry) entry).getData(), entry.read(dataNbt, ReflectionHelper.cast(this)));
				});
	}

	@Inject(at = @At("RETURN"), method = {"writeCustomDataToNbt", "method_5652"}, remap = false, require = 1)
	public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo cbi) {
		if (DataTrackerHelper.getDataEntries(ReflectionHelper.<LivingEntity>cast(this).getClass()).isEmpty()) return;

		DataTracker tracker = ReflectionHelper.<LivingEntity>cast(this).getDataTracker();
		NbtCompound dataNbt = new NbtCompound();

		DataTrackerHelper.getDataEntries(ReflectionHelper.<LivingEntity>cast(this).getClass()).stream()
				.filter(entry -> entry.getTagKey() != null)
				.forEach(entry -> ((DataTrackerHelper.DataTrackerEntry) entry).write(dataNbt, tracker.get(entry.getData())));

		nbt.put("MoreCommandsData", dataNbt);
	}
}
