package com.ptsmods.morecommands.net;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageHandler implements IMessageHandler<IMessage, IMessage> {

	public MessageHandler() {}

	@Override
	public IMessage onMessage(IMessage message, MessageContext ctx) {
		try {
			if (message instanceof AbstractPacket) return ((AbstractPacket) message).run(ctx);
			else return null;
		} catch (Exception e) {
			e.printStackTrace();
			return new ExceptionPacket(e, (Class<? extends AbstractPacket>) message.getClass());
		}
	}

}
