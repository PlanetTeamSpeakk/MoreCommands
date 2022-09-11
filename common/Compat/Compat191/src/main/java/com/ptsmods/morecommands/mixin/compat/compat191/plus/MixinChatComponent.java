package com.ptsmods.morecommands.mixin.compat.compat191.plus;

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
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
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
    @Shadow @Final private List<GuiMessage> allMessages;

    @Shadow protected abstract void refreshTrimmedMessage();

    @Inject(at = @At("HEAD"), method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V", cancellable = true)
    public void addMessage(Component message, MessageSignature signature, int addedTime, GuiMessageTag tag, boolean bl, CallbackInfo cbi) {
        if (ClientOption.getBoolean("ignoreEmptyMsgs") && Objects.requireNonNull(IMoreCommands.get()
                .textToString(message, null, false)).trim().isEmpty()) cbi.cancel();
    }

    @Inject(at = @At("TAIL"), method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V")
    private void addMessagePost(Component component, MessageSignature messageSignature, int i, GuiMessageTag guiMessageTag, boolean bl, CallbackInfo ci) {
        GuiMessageAddon msg = allMessages.isEmpty() ? null : ReflectionHelper.cast(allMessages.get(0));
        MessageHistory.putMessage(msg);
    }

    @ModifyArgs(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;" +
            "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V"),
            method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V")
    public void addMessage_addMessage(Args args) {
        TextBuilder<?> builder = Compat.get().builderFromText(args.get(0));
        if (builder instanceof LiteralTextBuilder && "\u00A0".equals(((LiteralTextBuilder) builder).getLiteral())) {
            args.set(0, EmptyTextBuilder.empty());
            args.set(1, MessageSignature.EMPTY);
        } else if (ClientOption.getBoolean("showMsgTime"))
            args.set(0, EmptyTextBuilder.builder()
                    .append(LiteralTextBuilder.builder("[" + (
                            ClientOption.getBoolean("use12HourClock") ? ClientOption.getBoolean("showSeconds") ? twelveSec : twelve :
                                    ClientOption.getBoolean("showSeconds") ? twentyfourSec : twentyfour)
                                    .format(LocalDateTime.now()) + "] ")
                            .withStyle(style -> style.withColor(IMoreCommands.get().getSecondaryFormatting())))
                    .append(builder)
                    .build());
    }

    // Without this redirect the while loop would have no end.
    @Redirect(at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"),
            method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V")
    public int messagesSize(List<?> messages) {
        return ClientOption.getBoolean("infiniteChat") ? Math.min(messages.size(), 100) : messages.size();
    }

    @Redirect(at = @At(value = "INVOKE", target = "Ljava/util/List;remove(I)Ljava/lang/Object;"),
            method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V")
    public Object messagesRemove(List<?> messages, int index) {
        return ClientOption.getBoolean("infiniteChat") ? messages.get(messages.size() - 1) : messages.remove(index);
    }

    @Override
    public void mc$removeById(int id) {
        allMessages.removeIf(msg -> ((GuiMessageAddon) (Object) msg).mc$getId() == id);
        refreshTrimmedMessage();
    }
}
