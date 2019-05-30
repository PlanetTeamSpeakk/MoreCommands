package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class ReachProvider implements ICapabilitySerializable<NBTBase> {

	@CapabilityInject(IReach.class)
	public static final Capability<IReach>	reachCap	= null;
	private IReach							instance	= reachCap.getDefaultInstance();

	public ReachProvider() {}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == reachCap;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return capability == reachCap ? reachCap.<T>cast(instance) : null;
	}

	@Override
	public NBTBase serializeNBT() {
		return reachCap.getStorage().writeNBT(reachCap, instance, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt) {
		reachCap.getStorage().readNBT(reachCap, instance, null, nbt);
	}

}
