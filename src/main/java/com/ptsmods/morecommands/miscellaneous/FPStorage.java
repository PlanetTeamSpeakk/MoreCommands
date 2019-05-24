package com.ptsmods.morecommands.miscellaneous;

import com.ptsmods.morecommands.miscellaneous.Reference.LogType;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class FPStorage implements IStorage<FP> {

	public FPStorage() {}

	@Override
	public NBTBase writeNBT(Capability<FP> capability, FP instance, EnumFacing side) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("isFake", instance.isFake);
		tag.setString("name", instance.name);
		return tag;
	}

	@Override
	public void readNBT(Capability<FP> capability, FP instance, EnumFacing side, NBTBase nbt) {
		if (nbt instanceof NBTTagCompound) {
			NBTTagCompound tag = (NBTTagCompound) nbt;
			instance.isFake = tag.getBoolean("isFake");
			instance.name = tag.getString("name");
		} else Reference.print(LogType.ERROR, "The NBT data appears to be tampered with as the given data is not an instance of NBTTagCompound.");
	}

}
