package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.ChatFormatting;
import net.minecraft.client.GuiMessage;

public class ChatHudLineWithContent<T> extends GuiMessage<T> {

    private String content = null;
    private String contentStripped = null;

    public ChatHudLineWithContent(int creationTick, T t, int id, String content) {
        super(creationTick, t, id);
        setContent(content);
    }

    public void setContent(String content) {
        this.content = content;
        contentStripped = ChatFormatting.stripFormatting(content).toLowerCase();
    }

    public String getContent() {
        return content;
    }

    public String getContentStripped() {
        return contentStripped;
    }

}
