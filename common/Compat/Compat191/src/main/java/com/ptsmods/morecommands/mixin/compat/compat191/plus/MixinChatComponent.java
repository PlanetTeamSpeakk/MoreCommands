package com.ptsmods.morecommands.mixin.compat.compat191.plus;

import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.MessageHistory;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.addons.ChatComponentAddon;
import com.ptsmods.morecommands.api.addons.GuiMessageAddon;
import com.ptsmods.morecommands.api.addons.GuiMessageLineAddon;
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
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;

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
    @Shadow @Final private List<GuiMessage.Line> trimmedMessages;
    private @Unique int wrappedSize = 0;

    @Shadow protected abstract void addMessage(Component par1, MessageSignature par2, int par3, GuiMessageTag par4, boolean par5);

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

    // @ModifyArgs does not work on Forge as it causes a ClassNotFoundError regarding the synthetic Args$1 class.
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;addMessage" +
            "(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V"),
            method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V")
    public void addMessage_addMessage(ChatComponent chatComponent, Component message, MessageSignature signature, int timeAdded, GuiMessageTag tag, boolean b) {
        TextBuilder<?> builder = Compat.get().builderFromText(message);
        if (builder instanceof LiteralTextBuilder && "\u00A0".equals(((LiteralTextBuilder) builder).getLiteral()))
            addMessage(EmptyTextBuilder.empty(), MessageSignature.EMPTY, timeAdded, tag, b);
        else if (ClientOption.getBoolean("showMsgTime"))
            addMessage(EmptyTextBuilder.builder()
                    .append(LiteralTextBuilder.builder("[" + (
                                    ClientOption.getBoolean("use12HourClock") ? ClientOption.getBoolean("showSeconds") ? twelveSec : twelve :
                                            ClientOption.getBoolean("showSeconds") ? twentyfourSec : twentyfour)
                                    .format(LocalDateTime.now()) + "] ")
                            .withStyle(style -> style.withColor(IMoreCommands.get().getSecondaryFormatting())))
                    .append(builder)
                    .build(), signature, timeAdded, tag, b);
        else addMessage(message, signature, timeAdded, tag, b);
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

    // Capturing locals does not seem to work as it gives an error at an opcode, so we do it like this instead.
    @ModifyVariable(at = @At("STORE"), method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V")
    public List<?> addMessage_wrapComponents(List<?> wrapped) {
        wrappedSize = wrapped.size();
        return wrapped;
    }

    @Inject(at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", shift = At.Shift.AFTER, ordinal = 1),
            method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V")
    public void afterMessageAdd(Component component, MessageSignature messageSignature, int i, GuiMessageTag guiMessageTag, boolean bl, CallbackInfo cbi) {
        GuiMessage message = allMessages.get(0);
        int id = ((GuiMessageAddon) (Object) message).mc$getId();

        // Set the id of the message the trimmed messages came from so the message can be removed later.
        for (int i1 = 0; i1 < wrappedSize; i1++)
            ((GuiMessageLineAddon) (Object) trimmedMessages.get(i1)).mc$setParentId(id);
    }

    /**
     * @author PlanetTeamSpeak
     * @reason Passing true as the boolean parameter to addMessage breaks the id system
     */
    @Overwrite
    private void refreshTrimmedMessage() {
        this.trimmedMessages.clear();
        List<GuiMessage> messages = new ArrayList<>(allMessages);
        Collections.reverse(messages);
        allMessages.clear();

        for (GuiMessage message : messages)
            addMessage(message.content(), message.headerSignature(), message.addedTime(), message.tag(), false);
    }

    @Override
    public void mc$removeById(int id) {
        allMessages.removeIf(msg -> ((GuiMessageAddon) (Object) msg).mc$getId() == id);
        refreshTrimmedMessage();
    }
}
