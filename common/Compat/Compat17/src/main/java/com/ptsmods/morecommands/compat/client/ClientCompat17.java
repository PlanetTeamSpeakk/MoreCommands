package com.ptsmods.morecommands.compat.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

public class ClientCompat17 extends ClientCompat16 {

	@Override
	public void bufferBuilderBegin(BufferBuilder builder, int drawMode, VertexFormat format) {
		builder.begin(VertexFormat.DrawMode.values()[drawMode], format);
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
