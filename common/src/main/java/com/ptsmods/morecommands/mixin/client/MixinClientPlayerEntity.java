package com.ptsmods.morecommands.mixin.client;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.callbacks.ChatMessageSendEvent;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.encryption.ChatMessageSigner;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity {
    private @Unique boolean moveStopped = false;
    private @Unique boolean ignore = false;

    @Inject(at = @At("HEAD"), method = "sendChatMessage(Ljava/lang/String;)V", cancellable = true)
    public void sendChatMessage(String message, CallbackInfo cbi) {
        if (ignore) {
            ignore = false;
            return;
        }

        String oldMessage = message;
        message = ChatMessageSendEvent.EVENT.invoker().onMessageSend(message);
        if (message == null || message.isEmpty()) {
            cbi.cancel();
            return;
        }
        if (message.startsWith("/")) // For legacy reasons
            handleCommand(message, cbi);

        if (!message.equals(oldMessage)) {
            cbi.cancel();

            ignore = true;
            sendChatMessage(message);
        }
    }

    @Inject(at = @At("HEAD"), method = "sendCommand(Lnet/minecraft/network/encryption/ChatMessageSigner;Ljava/lang/String;Lnet/minecraft/text/Text;)V", require = 0, cancellable = true)
    public void sendCommand(ChatMessageSigner signer, String command, Text text, CallbackInfo cbi) {
        handleCommand(command, cbi);
    }

    private @Unique void handleCommand(String message, CallbackInfo cbi) {
        StringReader reader = new StringReader(message.startsWith("/") ? message.substring(1) : message);

        if (MoreCommandsClient.clientCommandDispatcher.getRoot().getChild(reader.getString().split(" ")[0]) != null) {
            cbi.cancel();
            if (MoreCommandsClient.isCommandDisabled(reader.getString())) {
                ClientCommand.sendError(Formatting.RED + "That client command is disabled on this server.");
                return;
            }

            try {
                MoreCommandsClient.clientCommandDispatcher.execute(reader, ReflectionHelper.<ClientPlayerEntity>cast(this).networkHandler.getCommandSource());
            } catch (CommandSyntaxException e) {
                ClientCommand.sendMsg(LiteralTextBuilder.builder(e.getMessage()).withStyle(Style.EMPTY.withFormatting(Formatting.RED)));
            } catch (Exception e) {
                ClientCommand.sendMsg(LiteralTextBuilder.builder("Unknown or incomplete command, see below for error.").withStyle(Style.EMPTY.withFormatting(Formatting.RED)));
                MoreCommands.LOG.catching(e);
            }
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

    @Shadow public abstract void sendChatMessage(String message);
}
