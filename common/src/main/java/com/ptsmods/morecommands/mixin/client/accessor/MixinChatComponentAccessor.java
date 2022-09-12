package com.ptsmods.morecommands.mixin.client.accessor;

import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ChatComponent.class)
public interface MixinChatComponentAccessor {
    @Accessor List<GuiMessage> getAllMessages();
    @Accessor List<GuiMessage.Line> getTrimmedMessages();
    @Accessor int getChatScrollbarPos();
}
