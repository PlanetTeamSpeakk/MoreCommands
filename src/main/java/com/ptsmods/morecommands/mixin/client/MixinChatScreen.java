package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.commands.client.SearchCommand;
import com.ptsmods.morecommands.miscellaneous.ClientOptions;
import com.ptsmods.morecommands.miscellaneous.CopySound;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.client.util.Clipboard;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

import static com.ptsmods.morecommands.MoreCommands.log;

@Mixin(ChatScreen.class)
public class MixinChatScreen {

    private static final Clipboard clipboard = new Clipboard();
    private static final Field mc_visibleMessagesField = ReflectionHelper.getYarnField(ChatHud.class, "visibleMessages", "field_2064");
    private static final Field mc_messagesField = ReflectionHelper.getYarnField(ChatHud.class, "messages", "field_2061");
    private static final Field mc_scrolledLinesField = ReflectionHelper.getYarnField(ChatHud.class, "scrolledLines", "field_2066");

    @Inject(at = @At("TAIL"), method = "mouseClicked(DDI)Z")
    public boolean mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cbi) {
        boolean b = cbi.getReturnValue();
        if (!b) {
            ChatHud chatHud = MinecraftClient.getInstance().inGameHud.getChatHud();
            ChatHudLine line = mc_getLine(chatHud, mouseX, mouseY);
            if (line != null)
                if (button == 0 && ClientOptions.Chat.chatMsgCopy) {
                    // Copies a message's content when you click on it in the chat.
                    Text t = (Text) line.getText();
                    if (t != null) {
                        try {
                            String s = MoreCommands.textToString(t, null, true);
                            clipboard.setClipboard(MinecraftClient.getInstance().getWindow().getHandle(), Screen.hasControlDown() ? s.replaceAll("\u00a7", "&") : Objects.requireNonNull(Formatting.strip(s)));
                            MinecraftClient.getInstance().getSoundManager().play(new CopySound());
                        } catch (Exception e) {
                            MoreCommands.log.catching(e);
                        }
                    }
                } else if (button == 1 && ClientOptions.Chat.chatMsgRemove) {
                    SearchCommand.lines.remove(line.getId());
                    chatHud.removeMessage(line.getId());
                }
        }
        return b;
    }

    // Just the same as ChatHud#getText, but actually returns a ChatHudLine object rather than a Style object.
    public ChatHudLine mc_getLine(ChatHud hud, double x, double y) {
        MinecraftClient client = MinecraftClient.getInstance();
        List<ChatHudLine> visibleMessages = null;
        List<ChatHudLine> messages = null;
        int scrolledLines = -1;
        try {
            visibleMessages = (List<ChatHudLine>) mc_visibleMessagesField.get(hud);
            messages = (List<ChatHudLine>) mc_messagesField.get(hud);
            scrolledLines = mc_scrolledLinesField.getInt(hud);
        } catch (IllegalAccessException e) {
            MoreCommands.log.catching(e);
        }
        if (visibleMessages != null && client.currentScreen instanceof ChatScreen && !client.options.hudHidden && client.options.chatVisibility != ChatVisibility.HIDDEN) {
            double d = x - 2.0D;
            double e = (double) client.getWindow().getScaledHeight() - y - 40.0D;
            d = MathHelper.floor(d / hud.getChatScale());
            e = MathHelper.floor(e / (hud.getChatScale() * (client.options.chatLineSpacing + 1.0D)));
            if (d >= 0.0D && e >= 0.0D) {
                int i = Math.min(hud.getVisibleLineCount(), visibleMessages.size());
                if (d <= (double) MathHelper.floor((double) hud.getWidth() / hud.getChatScale())) {
                    client.textRenderer.getClass();
                    if (e < (double) (9 * i + i)) {
                        client.textRenderer.getClass();
                        int j = (int)(e / 9.0D + (double) scrolledLines);
                        if (j >= 0 && j < visibleMessages.size()) {
                            ChatHudLine chatHudLine = visibleMessages.get(j);
                            for (ChatHudLine line : messages)
                                if (line.getId() == chatHudLine.getId())
                                    return line;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen; handleTextClick(Lnet/minecraft/text/Style;)Z"), method = "mouseClicked(DDI)Z")
    private boolean mouseClicked_handleTextClick(ChatScreen thiz, Style style, double mouseX, double mouseY, int button) {
        if (style != null && style.getClickEvent() != null && style.getClickEvent().getAction() == SearchCommand.SCROLL_ACTION) {
            ChatHud chat = MinecraftClient.getInstance().inGameHud.getChatHud();
            int scrolled;
            try {
                scrolled = mc_scrolledLinesField.getInt(chat);
            } catch (IllegalAccessException e) {
                log.catching(e);
                return false;
            }
            int id = Integer.parseInt(style.getClickEvent().getValue());
            List<ChatHudLine> messages;
            try {
                messages = (List<ChatHudLine>) mc_visibleMessagesField.get(chat);
            } catch (IllegalAccessException e) {
                log.catching(e);
                return false;
            }
            int index = -1;
            for (int i = 0; i < messages.size(); i++)
                if (messages.get(i).getId() == id) {
                    index = i;
                    break;
                }
            if (index >= 0) chat.scroll(index - scrolled - chat.getVisibleLineCount() + MinecraftClient.getInstance().textRenderer.getTextHandler().wrapLines(messages.get(index).getText(), chat.getWidth(), Style.EMPTY).size());
            return true;
        }
        return thiz.handleTextClick(style);
    }

}
