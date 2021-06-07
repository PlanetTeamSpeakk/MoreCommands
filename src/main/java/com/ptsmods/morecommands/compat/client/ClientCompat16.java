package com.ptsmods.morecommands.compat.client;

import com.ptsmods.morecommands.compat.Compat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

import java.util.List;

// Supposedly ReflectASM is much faster than Java reflection: https://github.com/EsotericSoftware/reflectasm#performance
// Can't use invoker and accessor mixins for methods or fields that don't exist in 1.17, it seems.
class ClientCompat16 extends AbstractClientCompat implements ClientCompat {
    static final ClientCompat16 instance;

    static {
        instance = Compat.is16() ? new ClientCompat16() : null;
    }

    private ClientCompat16() {} // Private constructor

    @Override
    public void bufferBuilderBegin(BufferBuilder builder, int drawMode, VertexFormat format) {
        getMA(BufferBuilder.class).invoke(builder, getMI(BufferBuilder.class, "method_1328", int.class, VertexFormat.class), drawMode, format);
    }

    @Override
    public void clearScreen(Screen screen) {
        ((List<?>) getFA(Screen.class).get(screen, getFI(Screen.class, "field_22791"))).clear();
        ((List<?>) getFA(Screen.class).get(screen, getFI(Screen.class, "field_22786"))).clear();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ClickableWidget> T addButton(Screen screen, T button) {
        return (T) getMA(Screen.class).invoke(screen, getMI(Screen.class, "method_25411", ClickableWidget.class), button);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ClickableWidget> getButtons(Screen screen) {
        return (List<ClickableWidget>) getFA(Screen.class).get(screen, getFI(Screen.class, "field_22791"));
    }

    @Override
    public int getFrameCount(Sprite sprite) {
        return (int) getMA(Sprite.class).invoke(sprite, getMI(Sprite.class, "method_4592"));
    }

    @Override
    public void bindTexture(Identifier id) {
//        getMA(TextureManager.class).invoke(MinecraftClient.getInstance().getTextureManager(), getMI(TextureManager.class, "method_22813"), id);
        // This might get removed from Minecraft at some point as everything's moving to Blaze3D,
        // but seeing as it still exists in 1.17 and it's faster to invoke this directly than to
        // use reflection, even if ASM reflection, we'll keep this here and just leave the code
        // above commented until needed.
        MinecraftClient.getInstance().getTextureManager().bindTexture(id);
    }
}
