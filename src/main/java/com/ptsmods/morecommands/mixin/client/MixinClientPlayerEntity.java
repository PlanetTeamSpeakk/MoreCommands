package com.ptsmods.morecommands.mixin.client;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.callbacks.ChatMessageSendCallback;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {

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
                try {
                    MoreCommandsClient.clientCommandDispatcher.execute(reader, MinecraftClient.getInstance().player.networkHandler.getCommandSource());
                } catch (CommandSyntaxException e) {
                    ClientCommand.sendMsg(new LiteralText(e.getMessage()));
                } catch (Exception e) {
                    LiteralText msg = new LiteralText("Unknown or incomplete command, see below for error.");
                    msg.setStyle(Style.EMPTY.withFormatting(Formatting.RED));
                    ClientCommand.sendMsg(msg);
                    MoreCommands.log.catching(e);
                }
                return;
            }
        }
        if (!message.equals(oldMessage)) {
            cbi.cancel();
            MoreCommands.<ClientPlayerEntity>cast(this).networkHandler.sendPacket(new ChatMessageC2SPacket(message));
        }
    }

}
