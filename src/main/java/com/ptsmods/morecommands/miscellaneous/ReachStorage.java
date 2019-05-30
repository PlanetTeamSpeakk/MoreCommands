package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class ReachStorage implements IStorage<IReach> {

	public ReachStorage() {}

	@Override
	public NBTBase writeNBT(Capability<IReach> capability, IReach instance, EnumFacing side) {
		return new NBTTagFloat(instance.get());
	}

	@Override
	public void readNBT(Capability<IReach> capability, IReach instance, EnumFacing side, NBTBase nbt) {
		instance.set(null, ((NBTPrimitive) nbt).getFloat());
	}

}
