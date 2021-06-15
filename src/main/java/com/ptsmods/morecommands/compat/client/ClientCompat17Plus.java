package com.ptsmods.morecommands.compat.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.ptsmods.morecommands.compat.Compat;
import com.ptsmods.morecommands.mixin.compat.compat17plus.MixinScreenAccessor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.stream.Collectors;

class ClientCompat17Plus extends AbstractClientCompat implements ClientCompat {
    static final ClientCompat17Plus instance;

    static {
        instance = Compat.is16() ? null : new ClientCompat17Plus();
    }

    private ClientCompat17Plus() {} // Private constructor

    @Override
    public void bufferBuilderBegin(BufferBuilder builder, int drawMode, VertexFormat format) {
        builder.begin(VertexFormat.DrawMode.values()[drawMode], format);
    }
    
    @Override
    public void clearScreen(Screen screen) {
        ((MixinScreenAccessor) screen).callClear();
    }

    @Override
    public <T extends ClickableWidget> T addButton(Screen screen, T button) {
        return (T) ((MixinScreenAccessor) screen).callAddDrawableChild(button);
    }

    @Override
    public List<ClickableWidget> getButtons(Screen screen) {
        return ((MixinScreenAccessor) screen).getDrawables().stream().filter(drawable -> drawable instanceof ClickableWidget).map(drawable -> (ClickableWidget) drawable).collect(Collectors.toList());
    }

    @Override
    public int getFrameCount(Sprite sprite) {
        return sprite.getDistinctFrameCount().max().orElse(1);
    }

    @Override
    public void bindTexture(Identifier id) {
        RenderSystem.setShaderTexture(0, id);
    }
}
