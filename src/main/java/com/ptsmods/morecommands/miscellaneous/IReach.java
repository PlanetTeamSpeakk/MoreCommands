package com.ptsmods.morecommands.miscellaneous;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayerMP;

public interface IReach {

	public float get();

	public void set(float reach);

	public void set(@Nullable EntityPlayerMP player, float reach);

}
