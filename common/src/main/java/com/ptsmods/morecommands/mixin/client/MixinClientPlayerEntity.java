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
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSigner;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class MixinClientPlayerEntity {
    private @Unique boolean moveStopped = false;
    private @Unique boolean ignore = false;

    @Inject(at = @At("HEAD"), method = "chat(Ljava/lang/String;)V", cancellable = true)
    public void chat(String message, CallbackInfo cbi) {
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
            chat(message);
        }
    }

    @Inject(at = @At("HEAD"), method = "sendCommand", require = 0, cancellable = true)
    public void sendCommand(MessageSigner signer, String command, Component text, CallbackInfo cbi) {
        handleCommand(command, cbi);
    }

    private @Unique void handleCommand(String message, CallbackInfo cbi) {
        StringReader reader = new StringReader(message.startsWith("/") ? message.substring(1) : message);

        if (MoreCommandsClient.clientCommandDispatcher.getRoot().getChild(reader.getString().split(" ")[0]) != null) {
            cbi.cancel();
            if (MoreCommandsClient.isCommandDisabled(reader.getString())) {
                ClientCommand.sendError(ChatFormatting.RED + "That client command is disabled on this server.");
                return;
            }

            try {
                MoreCommandsClient.clientCommandDispatcher.execute(reader, ReflectionHelper.<LocalPlayer>cast(this).connection.getSuggestionsProvider());
            } catch (CommandSyntaxException e) {
                ClientCommand.sendMsg(LiteralTextBuilder.builder(e.getMessage()).withStyle(Style.EMPTY.applyFormat(ChatFormatting.RED)));
            } catch (Exception e) {
                ClientCommand.sendMsg(LiteralTextBuilder.builder("Unknown or incomplete command, see below for error.").withStyle(Style.EMPTY.applyFormat(ChatFormatting.RED)));
                MoreCommands.LOG.catching(e);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "moveTowardsClosestSpace", cancellable = true)
    protected void pushOutOfBlocks(double x, double z, CallbackInfo cbi) {
        if (!ClientOptions.Tweaks.doBlockPush.getValue()) cbi.cancel();
    }

    @Inject(at = @At("HEAD"), method = "aiStep")
    private void tickMovement(CallbackInfo cbi) {
        LocalPlayer thiz = ReflectionHelper.cast(this);
        if (!thiz.input.shiftKeyDown && !thiz.input.jumping) {
            if (!moveStopped && ClientOptions.Tweaks.immediateMoveStop.getValue()) {
                thiz.setDeltaMovement(thiz.getDeltaMovement().x(), Math.min(0d, thiz.getDeltaMovement().y()), thiz.getDeltaMovement().z());
                moveStopped = true; // Without this variable, you would be able to bhop by combining sprintAutoJump and immediateMoveStop and immediateMoveStop would also act as anti-kb.
            }
        } else moveStopped = false;
        if (ClientOptions.Cheats.sprintAutoJump.getValue() && MoreCommands.isSingleplayer() && thiz.isSprinting() &&
                (thiz.zza != 0 || thiz.xxa != 0) && thiz.isOnGround() && !thiz.isShiftKeyDown())
            thiz.jumpFromGround();
    }

    @Shadow public abstract void chat(String message);
}
