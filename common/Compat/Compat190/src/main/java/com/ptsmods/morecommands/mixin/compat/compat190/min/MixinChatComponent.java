package com.ptsmods.morecommands.mixin.compat.compat190.min;

import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.MessageHistory;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.addons.ChatComponentAddon;
import com.ptsmods.morecommands.api.addons.GuiMessageAddon;
import com.ptsmods.morecommands.api.clientoptions.ClientOption;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.api.util.text.EmptyTextBuilder;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.api.util.text.TextBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Mixin(ChatComponent.class)
public abstract class MixinChatComponent implements ChatComponentAddon {
    @Unique
    private int id = 0;
    @Unique private static final DateTimeFormatter twentyfour = new DateTimeFormatterBuilder()
            .appendPattern("HH")
            .appendLiteral(':')
            .appendPattern("mm")
            .toFormatter(Locale.ENGLISH);
    @Unique private static final DateTimeFormatter twelve = new DateTimeFormatterBuilder()
            .appendPattern("h")
            .appendLiteral(':')
            .appendPattern("mm")
            .appendLiteral(' ')
            .appendPattern("a")
            .toFormatter(Locale.ENGLISH);
    @Unique private static final DateTimeFormatter twentyfourSec = new DateTimeFormatterBuilder()
            .appendPattern("HH")
            .appendLiteral(':')
            .appendPattern("mm")
            .appendLiteral(':')
            .appendPattern("ss")
            .toFormatter(Locale.ENGLISH);
    @Unique private static final DateTimeFormatter twelveSec = new DateTimeFormatterBuilder()
            .appendPattern("h")
            .appendLiteral(':')
            .appendPattern("mm")
            .appendLiteral(':')
            .appendPattern("ss")
            .appendLiteral(' ')
            .appendPattern("a")
            .toFormatter(Locale.ENGLISH);
    @Shadow @Final private List<GuiMessage<Component>> allMessages;

    @Shadow protected abstract void removeById(int i);

    @Inject(at = @At("HEAD"), method = "addMessage(Lnet/minecraft/network/chat/Component;)V", cancellable = true)
    public void addMessage(Component message, CallbackInfo cbi) {
        if (ClientOption.getBoolean("ignoreEmptyMsgs") && Objects.requireNonNull(IMoreCommands.get()
                .textToString(message, null, false)).trim().isEmpty())
            cbi.cancel();
    }

    @ModifyArgs(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;addMessage(Lnet/minecraft/network/chat/Component;I)V"), method = "addMessage(Lnet/minecraft/network/chat/Component;)V")
    public void addMessage_addMessage(Args args) {
        TextBuilder<?> builder = Compat.get().builderFromText(args.get(0));
        if (builder instanceof LiteralTextBuilder && "\u00A0".equals(((LiteralTextBuilder) builder).getLiteral())) args.set(0, EmptyTextBuilder.empty());
        args.set(1, id++);
    }

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;addMessage(Lnet/minecraft/network/chat/Component;IIZ)V"), method = "addMessage(Lnet/minecraft/network/chat/Component;I)V")
    public Component addMessage_addMessage_text(Component text) {
        return ClientOption.getBoolean("showMsgTime") && text != null ?
                LiteralTextBuilder.builder("[" + (
                                ClientOption.getBoolean("use12HourClock") ? ClientOption.getBoolean("showSeconds") ?
                                        twelveSec : twelve : ClientOption.getBoolean("showSeconds") ? twentyfourSec : twentyfour)
                                .format(LocalDateTime.now()) + "] ")
                        .withStyle(style -> style.withColor(IMoreCommands.get().getSecondaryFormatting()))
                        .append(Compat.get().builderFromText(text)
                                .withStyle(style -> style.getColor() == null ? style.applyFormat(ChatFormatting.WHITE) : style))
                        .build() : text;
    }

    @Inject(at = @At("TAIL"), method = "addMessage(Lnet/minecraft/network/chat/Component;IIZ)V")
    private void addMessage(Component stringVisitable, int messageId, int timestamp, boolean bl, CallbackInfo cbi) {
        GuiMessageAddon msg = allMessages.isEmpty() ? null : ReflectionHelper.cast(allMessages.get(0));
        MessageHistory.putMessage(msg);
    }

    // Without this redirect the while loop would have no end.
    @Redirect(at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", remap = false), method = "addMessage(Lnet/minecraft/network/chat/Component;IIZ)V")
    public int messagesSize(List<?> messages) {
        return ClientOption.getBoolean("infiniteChat") ? Math.min(messages.size(), 100) : messages.size();
    }

    @Redirect(at = @At(value = "INVOKE", target = "Ljava/util/List;remove(I)Ljava/lang/Object;", remap = false), method = "addMessage(Lnet/minecraft/network/chat/Component;IIZ)V")
    public Object messagesRemove(List<?> messages, int index) {
        return ClientOption.getBoolean("infiniteChat") ? messages.get(messages.size() - 1) : messages.remove(index);
    }

    @Override
    public void mc$removeById(int id) {
        removeById(id);
    }
}
