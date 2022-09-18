package com.ptsmods.morecommands.mixin.compat.compat190.min;

import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ChatComponent.class)
public interface MixinChatComponentAccessor {
    @Accessor List<GuiMessage<Component>> getAllMessages();
    @Accessor List<GuiMessage<FormattedCharSequence>> getTrimmedMessages();
    @Accessor int getChatScrollbarPos();
}
