package com.ptsmods.morecommands.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class ExceptionPacket extends AbstractPacket {

	public Exception						e;			// E
	public Class<? extends AbstractPacket>	packetClass;

	/**
	 * Can be both sent to and from the client and the server.
	 *
	 * @param e The exception thrown while running
	 */
	public ExceptionPacket(Exception e, Class<? extends AbstractPacket> packetClass) {
		this.e = e;
		this.packetClass = packetClass;
	}

	public ExceptionPacket() {}

	@Override
	public void fromBytes(ByteBuf buf) {
		if (buf.readableBytes() != 0) try {
			packetClass = (Class<? extends AbstractPacket>) Class.forName(ByteBufUtils.readUTF8String(buf), true, ClassLoader.getSystemClassLoader());
			byte[] bytes = new byte[buf.readInt()];
			buf.readBytes(bytes);
			ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(bytes));
			e = (Exception) stream.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			// ironic
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		try {
			buf.writeInt(packetClass.getName().getBytes(StandardCharsets.UTF_8).length);
			ByteBufUtils.writeUTF8String(buf, packetClass.getName());
			// Exceptions implement Serializable, take notes, Mojang.
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			ObjectOutputStream objectStream = new ObjectOutputStream(stream);
			objectStream.writeObject(e);
			byte[] bytes = stream.toByteArray();
			buf.writeInt(bytes.length);
			buf.writeBytes(bytes);
		} catch (Exception e) {
			e.printStackTrace();
			// ironic
			buf.clear();
		}
	}

	@Override
	public IMessage run(MessageContext ctx) {
		new PacketExecutionException("An uncaught exception was thrown on the " + (ctx.side == Side.CLIENT ? "server" : "client") + " while handling a packet of class " + packetClass.getName() + ".", e).printStackTrace();
		return null;
	}

	public static final class PacketExecutionException extends Exception {
		private static final long serialVersionUID = 5611972702632690223L;

		public PacketExecutionException() {
			super();
		}

		public PacketExecutionException(String message) {
			super(message);
		}

		public PacketExecutionException(Throwable cause) {
			super(cause);
		}

		public PacketExecutionException(String message, Throwable cause) {
			super(message, cause);
		}

	}

}
