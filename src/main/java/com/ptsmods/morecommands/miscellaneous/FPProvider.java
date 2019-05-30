package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class FPProvider implements ICapabilitySerializable<NBTBase> {

	@CapabilityInject(FP.class)
	public static final Capability<FP>	fpCap		= null;
	private FP							instance	= fpCap.getDefaultInstance();

	public FPProvider() {}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == fpCap;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return capability == fpCap ? fpCap.<T>cast(instance) : null;
	}

	@Override
	public NBTBase serializeNBT() {
		return fpCap.getStorage().writeNBT(fpCap, instance, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt) {
		fpCap.getStorage().readNBT(fpCap, instance, null, nbt);
	}

}
