package com.ptsmods.morecommands.net;

import com.ptsmods.morecommands.miscellaneous.ReachProvider;
import com.ptsmods.morecommands.miscellaneous.Reference;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ServerCapabilitiesUpdatePacket extends AbstractPacket {

	private float reach;

	public ServerCapabilitiesUpdatePacket() {}

	public ServerCapabilitiesUpdatePacket(float reach) {
		this.reach = reach;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		reach = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeFloat(reach);
	}

	@Override
	public IMessage run(MessageContext context) {
		Runnable run = () -> {
			if (Minecraft.getMinecraft().player.getEntityAttribute(EntityPlayer.REACH_DISTANCE) != null) Minecraft.getMinecraft().player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(reach);
			if (Minecraft.getMinecraft().player.getCapability(ReachProvider.reachCap, null) != null) Minecraft.getMinecraft().player.getCapability(ReachProvider.reachCap, null).set(null, reach);
		};
		if (Minecraft.getMinecraft() == null || Minecraft.getMinecraft().player == null) Reference.execute(() -> {
			while (Minecraft.getMinecraft() == null || Minecraft.getMinecraft().player == null)
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			Minecraft.getMinecraft().addScheduledTask(run);
		});
		else run.run();
		return null;
	}

}
