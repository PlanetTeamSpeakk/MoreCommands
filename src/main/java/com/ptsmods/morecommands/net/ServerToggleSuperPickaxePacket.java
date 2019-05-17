package com.ptsmods.morecommands.net;

import com.ptsmods.morecommands.miscellaneous.Reference;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ServerToggleSuperPickaxePacket extends AbstractPacket {

	public boolean flag;

	public ServerToggleSuperPickaxePacket() {}

	public ServerToggleSuperPickaxePacket(boolean flag) {
		this.flag = flag;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		flag = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(flag);
	}

	@Override
	public IMessage run(MessageContext ctx) {
		Reference.superPickaxeEnabled = flag;
		return null;
	}

}
