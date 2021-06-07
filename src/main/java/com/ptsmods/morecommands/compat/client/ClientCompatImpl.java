package com.ptsmods.morecommands.compat.client;

import com.ptsmods.morecommands.compat.Compat;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

import java.util.List;

class ClientCompatImpl extends AbstractClientCompat implements ClientCompat {
    static final ClientCompatImpl instance = new ClientCompatImpl();

    @Override
    public void bufferBuilderBegin(BufferBuilder builder, int drawMode, VertexFormat format) {
        (Compat.is16() ? ClientCompat16.instance : ClientCompat17Plus.instance).bufferBuilderBegin(builder, drawMode, format);
    }

    @Override
    public void clearScreen(Screen screen) {
        (Compat.is16() ? ClientCompat16.instance : ClientCompat17Plus.instance).clearScreen(screen);
    }

    @Override
    public <T extends ClickableWidget> T addButton(Screen screen, T button) {
        return (Compat.is16() ? ClientCompat16.instance : ClientCompat17Plus.instance).addButton(screen, button);
    }

    @Override
    public List<ClickableWidget> getButtons(Screen screen) {
        return (Compat.is16() ? ClientCompat16.instance : ClientCompat17Plus.instance).getButtons(screen);
    }

    @Override
    public int getFrameCount(Sprite sprite) {
        return (Compat.is16() ? ClientCompat16.instance : ClientCompat17Plus.instance).getFrameCount(sprite);
    }

    @Override
    public void bindTexture(Identifier id) {
        (Compat.is16() ? ClientCompat16.instance : ClientCompat17Plus.instance).bindTexture(id);
    }
}
