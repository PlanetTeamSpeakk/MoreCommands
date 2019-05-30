package com.ptsmods.morecommands.net;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class AbstractPacket implements IMessage {

	@Override
	public abstract void fromBytes(ByteBuf buf);

	@Override
	public abstract void toBytes(ByteBuf buf);

	public abstract IMessage run(MessageContext ctx) throws Exception;

}
