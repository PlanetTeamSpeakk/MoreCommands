package com.ptsmods.morecommands.mixin.client.accessor;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ChatHud.class)
public interface MixinChatHudAccessor {
    @Accessor List<ChatHudLine<Text>> getMessages();
    @Accessor List<ChatHudLine<OrderedText>> getVisibleMessages();
    @Accessor int getScrolledLines();
    @Invoker void callRemoveMessage(int id);
}