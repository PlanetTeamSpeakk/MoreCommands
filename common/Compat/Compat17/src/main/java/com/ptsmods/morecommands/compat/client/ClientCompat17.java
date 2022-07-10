package com.ptsmods.morecommands.compat.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public class ClientCompat17 extends ClientCompat16 {

    @Override
    public void bufferBuilderBegin(BufferBuilder builder, int drawMode, VertexFormat format) {
        builder.begin(VertexFormat.Mode.values()[drawMode], format);
    }

    @Override
    public int getFrameCount(TextureAtlasSprite sprite) {
        return sprite.getUniqueFrames().max().orElse(1);
    }

    @Override
    public void bindTexture(ResourceLocation id) {
        RenderSystem.setShaderTexture(0, id);
    }
}
