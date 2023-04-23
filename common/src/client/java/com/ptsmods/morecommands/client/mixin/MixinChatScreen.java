package com.ptsmods.morecommands.client.mixin;

import com.mojang.blaze3d.platform.ClipboardManager;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.client.MoreCommandsClient;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.MessageHistory;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.addons.ChatComponentAddon;
import com.ptsmods.morecommands.api.addons.ChatScreenAddon;
import com.ptsmods.morecommands.api.addons.GuiMessageAddon;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
        if (b) return;

        ChatComponent chatHud = Minecraft.getInstance().gui.getChat();
        GuiMessageAddon line = ((ChatScreenAddon) this).mc$getLine(chatHud, mouseX, mouseY);
        if (line == null) return;

        if (button == 0 && ClientOptions.Chat.chatMsgCopy.getValue()) {
            // Copies a message's content when you click on it in the chat.
            Component t = line.mc$getRichContent();
            if (t != null) {
                String s = IMoreCommands.get().textToString(t, null, Screen.hasControlDown());
                clipboard.setClipboard(Minecraft.getInstance().getWindow().getWindow(), Screen.hasControlDown() ? s.replaceAll("\u00a7", "&") : MoreCommands.stripFormattings(s));
                Minecraft.getInstance().getSoundManager().play(ClientCompat.get().newCopySound());
                b = true;
            }
        } else if (button == 1 && ClientOptions.Chat.chatMsgRemove.getValue()) {
            MessageHistory.removeMessage(line.mc$getId());
            ((ChatComponentAddon) chatHud).mc$removeById(line.mc$getId());
            b = true;
        }
        cbi.setReturnValue(b);
    }
}
