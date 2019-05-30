package com.ptsmods.morecommands.net;

import com.ptsmods.morecommands.miscellaneous.PacketReceivedEvent;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Packet;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;

public class NetHandler extends ChannelDuplexHandler {

	private final Side side;

	public NetHandler(Side side) {
		this.side = side;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object in) {
		if (!(in instanceof Packet) || !MinecraftForge.EVENT_BUS.post(new PacketReceivedEvent((Packet) in, side))) ctx.fireChannelRead(in); // Continue to next handler if event wasn't cancelled.
	}

}
