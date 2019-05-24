package com.ptsmods.morecommands.miscellaneous;

import javax.annotation.Nullable;

import com.ptsmods.morecommands.net.ServerCapabilitiesUpdatePacket;

import net.minecraft.entity.player.EntityPlayerMP;

public class Reach implements IReach {

	private float reach = 5;

	public Reach() {}

	@Override
	public float get() {
		return reach;
	}

	@Override
	public void set(float reach) {
		set(null, reach);
	}

	@Override
	public void set(@Nullable EntityPlayerMP player, float reach) {
		this.reach = reach < 1 ? 1F : reach;
		if (player != null) Reference.netWrapper.sendTo(new ServerCapabilitiesUpdatePacket(reach), player);
	}

}
