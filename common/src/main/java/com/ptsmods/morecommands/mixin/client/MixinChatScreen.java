package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.commands.client.SearchCommand;
import com.ptsmods.morecommands.miscellaneous.CopySound;
import com.ptsmods.morecommands.mixin.client.accessor.MixinChatHudAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.client.util.Clipboard;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
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
    private static final Clipboard clipboard = new Clipboard();
    @Unique private static boolean colourPickerOpen = false;
    @Shadow protected TextFieldWidget chatField;

    @Inject(at = @At("TAIL"), method = "init()V")
    private void init(CallbackInfo cbi) {
        Screen thiz = ReflectionHelper.cast(this);
        MoreCommandsClient.addColourPicker(thiz, thiz.width - 117, 5, false, colourPickerOpen, chatField::write, b -> colourPickerOpen = b);
    }

    @Inject(at = @At("TAIL"), method = "mouseClicked(DDI)Z", cancellable = true)
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cbi) {
        boolean b = cbi.getReturnValueZ();
        if (!b) {
            ChatHud chatHud = MinecraftClient.getInstance().inGameHud.getChatHud();
            ChatHudLine<Text> line = getLine(chatHud, mouseX, mouseY);
            if (line != null)
                if (button == 0 && ClientOptions.Chat.chatMsgCopy.getValue()) {
                    // Copies a message's content when you click on it in the chat.
                    Text t = line.getText();
                    if (t != null) {
                        String s = IMoreCommands.get().textToString(t, null, Screen.hasControlDown());
                        clipboard.setClipboard(MinecraftClient.getInstance().getWindow().getHandle(), Screen.hasControlDown() ? s.replaceAll("\u00a7", "&") : MoreCommands.stripFormattings(s));
                        MinecraftClient.getInstance().getSoundManager().play(new CopySound());
                        b = true;
                    }
                } else if (button == 1 && ClientOptions.Chat.chatMsgRemove.getValue()) {
                    SearchCommand.lines.remove(line.getId());
                    ((MixinChatHudAccessor) chatHud).callRemoveMessage(line.getId());
                    b = true;
                }
        }
        cbi.setReturnValue(b);
    }

    // Just the same as ChatHud#getText, but actually returns a ChatHudLine object rather than a Style object.
    @Unique
    public ChatHudLine<Text> getLine(ChatHud hud, double x, double y) {
        MinecraftClient client = MinecraftClient.getInstance();
        List<ChatHudLine<OrderedText>> visibleMessages = ((MixinChatHudAccessor) hud).getVisibleMessages();
        List<ChatHudLine<Text>> messages = ((MixinChatHudAccessor) hud).getMessages();
        int scrolledLines = ((MixinChatHudAccessor) hud).getScrolledLines();
        if (visibleMessages != null && client.currentScreen instanceof ChatScreen && !client.options.hudHidden && ClientCompat.get().getChatVisibility(client.options) != ChatVisibility.HIDDEN) {
            double d = x - 2.0D;
            double e = (double) client.getWindow().getScaledHeight() - y - 40.0D;
            d = MathHelper.floor(d / hud.getChatScale());
            e = MathHelper.floor(e / (hud.getChatScale() * (ClientCompat.get().getChatLineSpacing(client.options) + 1.0D)));
            if (d >= 0.0D && e >= 0.0D) {
                int i = Math.min(hud.getVisibleLineCount(), visibleMessages.size());
                if (d <= (double) MathHelper.floor((double) hud.getWidth() / hud.getChatScale()))
                    if (e < (double) (9 * i + i)) {
                        int j = (int)(e / 9.0D + (double) scrolledLines);
                        if (j >= 0 && j < visibleMessages.size()) {
                            ChatHudLine<OrderedText> chatHudLine = visibleMessages.get(j);
                            for (ChatHudLine<Text> line : messages)
                                if (line.getId() == chatHudLine.getId())
                                    return line;
                        }
                    }
            }
        }
        return null;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;handleTextClick(Lnet/minecraft/text/Style;)Z"), method = "mouseClicked(DDI)Z")
    private boolean mouseClicked_handleTextClick(ChatScreen thiz, Style style, double mouseX, double mouseY, int button) {
        if (style != null && style.getClickEvent() != null && style.getClickEvent().getAction() == SearchCommand.SCROLL_ACTION) {
            ChatHud chat = MinecraftClient.getInstance().inGameHud.getChatHud();
            int scrolled = ((MixinChatHudAccessor) chat).getScrolledLines();
            int id = Integer.parseInt(style.getClickEvent().getValue());
            List<ChatHudLine<OrderedText>> messages = ((MixinChatHudAccessor) chat).getVisibleMessages();
            int index = -1;
            for (int i = 0; i < messages.size(); i++)
                if (messages.get(i).getId() == id) {
                    index = i;
                    break;
                }
            if (index >= 0) chat.scroll(index - scrolled - chat.getVisibleLineCount() + (SearchCommand.lines.containsKey(id) ? MinecraftClient.getInstance().textRenderer.getTextHandler().wrapLines(SearchCommand.lines.get(id).getText(), chat.getWidth(), Style.EMPTY).size() : 0));
            return true;
        }
        return thiz.handleTextClick(style);
    }
}
