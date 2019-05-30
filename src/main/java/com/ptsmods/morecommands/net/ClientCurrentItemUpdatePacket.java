package com.ptsmods.morecommands.net;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientCurrentItemUpdatePacket extends AbstractPacket {

	private int currentItem;

	public ClientCurrentItemUpdatePacket() {}

	public ClientCurrentItemUpdatePacket(int currentItem) {
		this.currentItem = currentItem;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		currentItem = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(currentItem);
	}

	@Override
	public IMessage run(MessageContext ctx) throws Exception {
		ctx.getServerHandler().player.inventory.currentItem = currentItem;
		return null;
	}

}
