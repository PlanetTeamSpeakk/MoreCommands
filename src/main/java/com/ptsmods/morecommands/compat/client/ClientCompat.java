package com.ptsmods.morecommands.compat.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

import java.util.List;

public interface ClientCompat {
    static ClientCompat getCompat() {
        return ClientCompatImpl.instance;
    }

    void bufferBuilderBegin(BufferBuilder builder, int drawMode, VertexFormat format);

    void clearScreen(Screen screen);

    <T extends ClickableWidget> T addButton(Screen screen, T button);

    List<ClickableWidget> getButtons(Screen screen);

    int getFrameCount(Sprite sprite);

    void bindTexture(Identifier id);
}
