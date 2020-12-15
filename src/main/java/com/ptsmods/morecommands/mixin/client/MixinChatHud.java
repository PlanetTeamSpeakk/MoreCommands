package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.commands.client.SearchCommand;
import com.ptsmods.morecommands.miscellaneous.ChatHudLineWithContent;
import com.ptsmods.morecommands.miscellaneous.ClientOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Locale;

@Mixin(ChatHud.class)
public abstract class MixinChatHud {

    private int mc_id = 0;
    private static final DateTimeFormatter twentyfour = new DateTimeFormatterBuilder().appendPattern("HH").appendLiteral(':').appendPattern("mm").toFormatter(Locale.ENGLISH);
    private static final DateTimeFormatter twelve = new DateTimeFormatterBuilder().appendPattern("h").appendLiteral(':').appendPattern("mm").appendLiteral(' ').appendPattern("a").toFormatter(Locale.ENGLISH);
    private static final DateTimeFormatter twentyfourSec = new DateTimeFormatterBuilder().appendPattern("HH").appendLiteral(':').appendPattern("mm").appendLiteral(':').appendPattern("ss").toFormatter(Locale.ENGLISH);
    private static final DateTimeFormatter twelveSec = new DateTimeFormatterBuilder().appendPattern("h").appendLiteral(':').appendPattern("mm").appendLiteral(':').appendPattern("ss").appendLiteral(' ').appendPattern("a").toFormatter(Locale.ENGLISH);
    @Shadow @Final private List<ChatHudLine> messages;
    @Shadow @Final private List<ChatHudLine> visibleMessages;
    @Shadow @Final private MinecraftClient client;

    @Overwrite
    public void addMessage(Text message) {
        if (ClientOptions.Chat.ignoreEmptyMsgs && Formatting.strip(MoreCommands.textToString(message, null)).trim().isEmpty()) return;
        if (message instanceof LiteralText && "\u00A0".equals(message.asString())) message = new LiteralText("");
        addMessage(message, mc_id++); // Making sure not all ChatHudLines have an id of 0 which breaks the getText method in MixinChatScreen.
    }

    @Shadow abstract void addMessage(Text message, int messageId);

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud; addMessage(Lnet/minecraft/text/StringRenderable;IIZ)V"), method = "addMessage(Lnet/minecraft/text/Text;I)V")
    public void addMessage_addMessage(ChatHud thiz, StringRenderable stringRenderable, int messageId, int timestamp, boolean bl) {
        if (ClientOptions.Chat.showMsgTime && stringRenderable instanceof Text) {
            Text message = (Text) stringRenderable;
            stringRenderable = new LiteralText("[" + (ClientOptions.Chat.use12HourClock ? ClientOptions.Chat.showSeconds ? twelveSec : twelve : ClientOptions.Chat.showSeconds ? twentyfourSec : twentyfour).format(LocalDateTime.now()) +"] ").setStyle(MoreCommands.SS).append(message.shallowCopy().setStyle(message.getStyle().getColor() == null ? message.getStyle().withFormatting(Formatting.WHITE) : message.getStyle()));
        }
        addMessage(stringRenderable, messageId, timestamp, bl);
    }

    @Shadow abstract void addMessage(StringRenderable stringRenderable, int messageId, int timestamp, boolean bl);

    @Inject(at = @At("TAIL"), method = "addMessage(Lnet/minecraft/text/StringRenderable;IIZ)V")
    private void addMessage(StringRenderable stringRenderable, int messageId, int timestamp, boolean bl, CallbackInfo cbi) {
        if (!messages.isEmpty() && !SearchCommand.lines.containsKey(messages.get(0).getId())) SearchCommand.lines.put(messages.get(0).getId(), new ChatHudLineWithContent(messages.get(0).getCreationTick(), messages.get(0).getText(), messages.get(0).getId(), MoreCommands.textToString((Text) messages.get(0).getText(), null)));
    }

    // Without this redirect the while loop would have no end.
    @Redirect(at = @At(value = "INVOKE", target = "Ljava/util/List; size()I"), method = "addMessage(Lnet/minecraft/text/StringRenderable;IIZ)V")
    public int messagesSize(List<?> messages) {
        return ClientOptions.Chat.infiniteChat ? Math.min(messages.size(), 100) : messages.size();
    }

    @Redirect(at = @At(value = "INVOKE", target = "Ljava/util/List; remove(I)Ljava/lang/Object;"), method = "addMessage(Lnet/minecraft/text/StringRenderable;IIZ)V")
    public Object messagesRemove(List<?> messages, int index) {
        return ClientOptions.Chat.infiniteChat ? messages.get(messages.size() - 1) : messages.remove(index);
    }

}
