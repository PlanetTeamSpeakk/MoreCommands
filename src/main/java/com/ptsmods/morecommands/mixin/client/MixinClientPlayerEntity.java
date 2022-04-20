package com.ptsmods.morecommands.mixin.client;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.callbacks.ChatMessageSendCallback;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.api.ReflectionHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {
	@Unique private boolean moveStopped = false;

	@Inject(at = @At("HEAD"), method = "sendChatMessage(Ljava/lang/String;)V", cancellable = true)
	public void sendChatMessage(String message, CallbackInfo cbi) {
		String oldMessage = message;
		message = ChatMessageSendCallback.EVENT.invoker().onMessageSend(message);
		if (message == null || message.isEmpty()) {
			cbi.cancel();
			return;
		}
		if (message.startsWith("/")) {
			StringReader reader = new StringReader(message);
			reader.skip();
			if (MoreCommandsClient.clientCommandDispatcher.getRoot().getChild(message.substring(1).split(" ")[0]) != null) {
				cbi.cancel();
				if (MoreCommandsClient.isCommandDisabled(message)) ClientCommand.sendMsg(Formatting.RED + "That client command is disabled on this server.");
				else
					try {
						MoreCommandsClient.clientCommandDispatcher.execute(reader, ReflectionHelper.<ClientPlayerEntity>cast(this).networkHandler.getCommandSource());
					} catch (CommandSyntaxException e) {
						ClientCommand.sendMsg(new LiteralText(e.getMessage()).setStyle(Style.EMPTY.withFormatting(Formatting.RED)));
					} catch (Exception e) {
						ClientCommand.sendMsg(new LiteralText("Unknown or incomplete command, see below for error.").setStyle(Style.EMPTY.withFormatting(Formatting.RED)));
						MoreCommands.LOG.catching(e);
					}
				return;
			}
		}
		if (!message.equals(oldMessage)) {
			cbi.cancel();
			ReflectionHelper.<ClientPlayerEntity>cast(this).networkHandler.sendPacket(new ChatMessageC2SPacket(message));
		}
	}

	@Inject(at = @At("HEAD"), method = "pushOutOfBlocks(DD)V", cancellable = true)
	protected void pushOutOfBlocks(double x, double z, CallbackInfo cbi) {
		if (!ClientOptions.Tweaks.doBlockPush.getValue()) cbi.cancel();
	}

	@Inject(at = @At("HEAD"), method = "tickMovement()V")
	private void tickMovement(CallbackInfo cbi) {
		ClientPlayerEntity thiz = ReflectionHelper.cast(this);
		if (!thiz.input.sneaking && !thiz.input.jumping) {
			if (!moveStopped && ClientOptions.Tweaks.immediateMoveStop.getValue()) {
				thiz.setVelocity(thiz.getVelocity().getX(), Math.min(0d, thiz.getVelocity().getY()), thiz.getVelocity().getZ());
				moveStopped = true; // Without this variable, you would be able to bhop by combining sprintAutoJump and immediateMoveStop and immediateMoveStop would also act as anti-kb.
			}
		} else moveStopped = false;
		if (ClientOptions.Cheats.sprintAutoJump.getValue() && MoreCommands.isSingleplayer() && thiz.isSprinting() &&
				(thiz.forwardSpeed != 0 || thiz.sidewaysSpeed != 0) && thiz.isOnGround() && !thiz.isSneaking())
			thiz.jump();
	}
}
