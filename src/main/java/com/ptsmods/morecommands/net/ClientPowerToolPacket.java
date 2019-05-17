package com.ptsmods.morecommands.net;

import com.ptsmods.morecommands.miscellaneous.Reference;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientPowerToolPacket extends AbstractPacket {

	public ClientPowerToolPacket() {}

	@Override
	public void fromBytes(ByteBuf buf) {}

	@Override
	public void toBytes(ByteBuf buf) {}

	@Override
	public IMessage run(MessageContext ctx) throws Exception {
		Reference.powerToolServerCommand(ctx.getServerHandler().player, EnumHand.MAIN_HAND, null);
		return null;
	}

}
