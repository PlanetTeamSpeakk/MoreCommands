package com.ptsmods.morecommands.mixin.client;

import com.mojang.blaze3d.platform.ClipboardManager;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.commands.client.SearchCommand;
import com.ptsmods.morecommands.miscellaneous.CopySound;
import com.ptsmods.morecommands.mixin.client.accessor.MixinChatHudAccessor;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.ChatVisiblity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ChatScreen.class)
public class MixinChatScreen {
    private static final ClipboardManager clipboard = new ClipboardManager();
    @Unique private static boolean colourPickerOpen = false;
    @Shadow protected EditBox input;

    @Inject(at = @At("TAIL"), method = "init()V")
    private void init(CallbackInfo cbi) {
        Screen thiz = ReflectionHelper.cast(this);
        MoreCommandsClient.addColourPicker(thiz, thiz.width - 117, 5, false, colourPickerOpen, input::insertText, b -> colourPickerOpen = b);
    }

    @Inject(at = @At("TAIL"), method = "mouseClicked(DDI)Z", cancellable = true)
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cbi) {
        boolean b = cbi.getReturnValueZ();
        if (!b) {
            ChatComponent chatHud = Minecraft.getInstance().gui.getChat();
            GuiMessage<Component> line = getLine(chatHud, mouseX, mouseY);
            if (line != null)
                if (button == 0 && ClientOptions.Chat.chatMsgCopy.getValue()) {
                    // Copies a message's content when you click on it in the chat.
                    Component t = line.getMessage();
                    if (t != null) {
                        String s = IMoreCommands.get().textToString(t, null, Screen.hasControlDown());
                        clipboard.setClipboard(Minecraft.getInstance().getWindow().getWindow(), Screen.hasControlDown() ? s.replaceAll("\u00a7", "&") : MoreCommands.stripFormattings(s));
                        Minecraft.getInstance().getSoundManager().play(new CopySound());
                        b = true;
                    }
                } else if (button == 1 && ClientOptions.Chat.chatMsgRemove.getValue()) {
                    SearchCommand.lines.remove(line.getId());
                    ((MixinChatHudAccessor) chatHud).callRemoveById(line.getId());
                    b = true;
                }
        }
        cbi.setReturnValue(b);
    }

    // Just the same as ChatHud#getText, but actually returns a ChatHudLine object rather than a Style object.
    @Unique
    public GuiMessage<Component> getLine(ChatComponent hud, double x, double y) {
        Minecraft client = Minecraft.getInstance();
        List<GuiMessage<FormattedCharSequence>> visibleMessages = ((MixinChatHudAccessor) hud).getTrimmedMessages();
        List<GuiMessage<Component>> messages = ((MixinChatHudAccessor) hud).getAllMessages();
        int scrolledLines = ((MixinChatHudAccessor) hud).getChatScrollbarPos();
        if (visibleMessages != null && client.screen instanceof ChatScreen && !client.options.hideGui && ClientCompat.get().getChatVisibility(client.options) != ChatVisiblity.HIDDEN) {
            double d = x - 2.0D;
            double e = (double) client.getWindow().getGuiScaledHeight() - y - 40.0D;
            d = Mth.floor(d / hud.getScale());
            e = Mth.floor(e / (hud.getScale() * (ClientCompat.get().getChatLineSpacing(client.options) + 1.0D)));
            if (d >= 0.0D && e >= 0.0D) {
                int i = Math.min(hud.getLinesPerPage(), visibleMessages.size());
                if (d <= (double) Mth.floor((double) hud.getWidth() / hud.getScale()))
                    if (e < (double) (9 * i + i)) {
                        int j = (int)(e / 9.0D + (double) scrolledLines);
                        if (j >= 0 && j < visibleMessages.size()) {
                            GuiMessage<FormattedCharSequence> chatHudLine = visibleMessages.get(j);
                            for (GuiMessage<Component> line : messages)
                                if (line.getId() == chatHudLine.getId())
                                    return line;
                        }
                    }
            }
        }
        return null;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/ChatScreen;handleComponentClicked(Lnet/minecraft/network/chat/Style;)Z"), method = "mouseClicked(DDI)Z")
    private boolean mouseClicked_handleTextClick(ChatScreen thiz, Style style, double mouseX, double mouseY, int button) {
        if (style != null && style.getClickEvent() != null && style.getClickEvent().getAction() == SearchCommand.SCROLL_ACTION) {
            ChatComponent chat = Minecraft.getInstance().gui.getChat();
            int scrolled = ((MixinChatHudAccessor) chat).getChatScrollbarPos();
            int id = Integer.parseInt(style.getClickEvent().getValue());
            List<GuiMessage<FormattedCharSequence>> messages = ((MixinChatHudAccessor) chat).getTrimmedMessages();
            int index = -1;
            for (int i = 0; i < messages.size(); i++)
                if (messages.get(i).getId() == id) {
                    index = i;
                    break;
                }
            if (index >= 0) chat.scrollChat(index - scrolled - chat.getLinesPerPage() + (SearchCommand.lines.containsKey(id) ? Minecraft.getInstance().font.getSplitter().splitLines(SearchCommand.lines.get(id).getMessage(), chat.getWidth(), Style.EMPTY).size() : 0));
            return true;
        }
        return thiz.handleComponentClicked(style);
    }
}
