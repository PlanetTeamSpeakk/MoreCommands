package com.ptsmods.morecommands.net;

import com.ptsmods.morecommands.miscellaneous.PacketReceivedEvent;
import com.ptsmods.morecommands.miscellaneous.PacketSendEvent;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.Packet;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;

public class NetHandler extends ChannelDuplexHandler {

	private final Side side;

	public NetHandler(Side side) {
		this.side = side;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object in) throws Exception {
		if (!(in instanceof Packet) || !MinecraftForge.EVENT_BUS.post(new PacketReceivedEvent((Packet) in, side))) super.channelRead(ctx, in); // Continue to next handler if event wasn't cancelled.
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object out, ChannelPromise promise) throws Exception {
		if (!(out instanceof Packet) || !MinecraftForge.EVENT_BUS.post(new PacketSendEvent((Packet) out, side))) super.write(ctx, out, promise);
	}

}
