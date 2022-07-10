package com.ptsmods.morecommands.mixin.client.accessor;

import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ChatComponent.class)
public interface MixinChatHudAccessor {
    @Accessor List<GuiMessage<Component>> getAllMessages();
    @Accessor List<GuiMessage<FormattedCharSequence>> getTrimmedMessages();
    @Accessor int getChatScrollbarPos();
    @Invoker void callRemoveById(int id);
}
