package com.ptsmods.morecommands.mixin.common;

import com.google.common.base.MoreObjects;
import com.ptsmods.morecommands.MoreCommands;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import java.util.ArrayList;
import java.util.List;

@Mixin(ServerCommandSource.class)
public abstract class MixinServerCommandSource {

	@Shadow @Final private CommandOutput output;
	@Shadow @Final private boolean silent;

	@Overwrite
	public void sendFeedback(Text message, boolean broadcastToOps) {
		if (message instanceof MutableText && (message.getStyle() == null || message.getStyle().getColor() == null)) ((MutableText) message).setStyle(MoreObjects.firstNonNull(message.getStyle(), Style.EMPTY).withFormatting(MoreCommands.DF));
		if (output.shouldReceiveFeedback() && !silent) output.sendSystemMessage(message, Util.NIL_UUID);
		if (broadcastToOps && this.output.shouldBroadcastConsoleToOps() && !silent) {
			MutableText msg = message.copy();
			msg.setStyle((msg.getStyle() == null ? Style.EMPTY : msg.getStyle()).withColor((Formatting) null));
			if (msg instanceof LiteralText) {
				MutableText msg0 = msg;
				msg = new LiteralText(Formatting.strip(((LiteralText) msg).getRawString())).setStyle(msg.getStyle());
				msg.getSiblings().addAll(msg0.getSiblings());
			}
			mc_cleanChildren(msg);
			this.sendToOps(msg);
		}
	}

	@Shadow
	abstract void sendToOps(Text message);

	private void mc_cleanChildren(MutableText text) {
		if (!text.getSiblings().isEmpty()) {
			List<Text> children = new ArrayList<>();
			for (Text child : text.getSiblings())
				if (child instanceof LiteralText) {
					MutableText child0 = new LiteralText(Formatting.strip(((LiteralText) child).getRawString())).setStyle(child.getStyle());
					mc_cleanChildren((MutableText) child);
					child0.getSiblings().addAll(child.getSiblings());
					children.add(child0);
				} else {
					MutableText child0 = child.shallowCopy();
					mc_cleanChildren(child0);
					children.add(child0);
				}
			text.getSiblings().clear();
			text.getSiblings().addAll(children);
		}
	}

}
