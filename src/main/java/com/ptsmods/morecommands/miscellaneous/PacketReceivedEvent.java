package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;

@Cancelable
public class PacketReceivedEvent extends Event {

	private final Packet	packet;
	private final Side		side;

	public PacketReceivedEvent(Packet packet, Side side) {
		this.packet = packet;
		this.side = side;
	}

	public Packet getPacket() {
		return packet;
	}

	public Side getSide() {
		return side;
	}

}
