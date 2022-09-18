package com.ptsmods.morecommands.mixin.compat.compat190.min;

import com.ptsmods.morecommands.api.Holder;
import com.ptsmods.morecommands.api.MessageHistory;
import com.ptsmods.morecommands.api.addons.ChatScreenAddon;
import com.ptsmods.morecommands.api.addons.GuiMessageAddon;
import com.ptsmods.morecommands.api.addons.GuiMessageLineAddon;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.ChatVisiblity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.NoSuchElementException;

@Mixin(ChatScreen.class)
public class MixinChatScreen implements ChatScreenAddon {
    @Override
    public GuiMessageAddon mc$getLine(ChatComponent hud, double x, double y) {
        Minecraft client = Minecraft.getInstance();
        List<GuiMessage<FormattedCharSequence>> visibleMessages = ((MixinChatComponentAccessor) hud).getTrimmedMessages();
        List<GuiMessage<Component>> messages = ((MixinChatComponentAccessor) hud).getAllMessages();
        int scrolledLines = ((MixinChatComponentAccessor) hud).getChatScrollbarPos();

        if (visibleMessages == null || !(client.screen instanceof ChatScreen) || client.options.hideGui ||
                ClientCompat.get().getChatVisibility(client.options) == ChatVisiblity.HIDDEN) return null;

        double f = x - 2.0;
        double g = (double) client.getWindow().getGuiScaledHeight() - y - 40.0;
        f = Mth.floor(f / hud.getScale());
        g = Mth.floor(g / (hud.getScale() * (client.options.chatLineSpacing().get() + 1.0)));

        if (f < 0.0 || g < 0.0) return null;

        int i = Math.min(hud.getLinesPerPage(), visibleMessages.size());
        if (!(f <= (double) Mth.floor((double) hud.getWidth() / hud.getScale())) || !(g < (double) (9 * i + i))) return null;

        int j = (int) (g / 9.0 + (double) scrolledLines);
        if (j < 0 || j >= visibleMessages.size()) return null;

        GuiMessage<FormattedCharSequence> guiMessage = visibleMessages.get(j);
        return messages.stream()
                .map(msg -> (GuiMessageAddon) msg)
                .filter(msg -> msg.mc$getId() == ((GuiMessageAddon) guiMessage).mc$getId())
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/ChatScreen;handleComponentClicked(Lnet/minecraft/network/chat/Style;)Z"), method = "mouseClicked(DDI)Z")
    private boolean mouseClicked_handleTextClick(ChatScreen thiz, Style style, double mouseX, double mouseY, int button) {
        if (style != null && style.getClickEvent() != null && style.getClickEvent().getAction() == Holder.getScrollAction()) {
            ChatComponent chat = Minecraft.getInstance().gui.getChat();
            int scrolled = ((MixinChatComponentAccessor) chat).getChatScrollbarPos();
            int id = Integer.parseInt(style.getClickEvent().getValue());
            List<GuiMessage<FormattedCharSequence>> messages = ((MixinChatComponentAccessor) chat).getTrimmedMessages();
            int index = -1;
            for (int i = 0; i < messages.size(); i++)
                if (((GuiMessageLineAddon) messages.get(i)).mc$getParentId() == id) {
                    index = i;
                    break;
                }

            if (index >= 0) chat.scrollChat(index - scrolled - chat.getLinesPerPage() + (MessageHistory.contains(id) ?
                    Minecraft.getInstance().font.getSplitter().splitLines(MessageHistory.getMessage(id).mc$getRichContent(), chat.getWidth(), Style.EMPTY).size() : 0));
            return true;
        }
        return thiz.handleComponentClicked(style);
    }
}
