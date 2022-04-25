package com.ptsmods.morecommands.compat;

import com.ptsmods.morecommands.api.text.LiteralTextBuilder;
import com.ptsmods.morecommands.api.text.TextBuilder;
import com.ptsmods.morecommands.api.text.TranslatableTextBuilder;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.Arrays;
import java.util.stream.Collectors;

// Purely exists for when Java does weird stuff saying it can't find classes that were
// deleted when loading Compat16 as a result of loading a more recent Compat that
// extends it.
class PrivateCompat16 {
	public static MutableText buildText(LiteralTextBuilder builder) {
		return buildText(new LiteralText(builder.getLiteral()), builder);
	}

	public static MutableText buildText(TranslatableTextBuilder builder) {
		return buildText(builder.getArgs().length == 0 ? new TranslatableText(builder.getKey()) : new TranslatableText(builder.getKey(), Arrays.stream(builder.getArgs())
				.map(o -> o instanceof TextBuilder ? ((TextBuilder<?>) o).build() : o)
				.toArray(Object[]::new)), builder);
	}

	private static MutableText buildText(MutableText text, TextBuilder<?> builder) {
		text.setStyle(builder.getStyle());
		builder.getChildren().forEach(child -> text.append(child.build()));
		return text;
	}

	public static TextBuilder<?> builderFromText(Text text) {
		TextBuilder<?> builder;
		if (text instanceof LiteralText)
			builder = LiteralTextBuilder.builder(((LiteralText) text).getRawString());
		else if (text instanceof TranslatableText)
			builder = TranslatableTextBuilder.builder(((TranslatableText) text).getKey(), Arrays.stream(((TranslatableText) text).getArgs())
					.map(o -> o instanceof Text ? builderFromText((Text) o) : o)
					.toArray(Object[]::new));
		else throw new IllegalArgumentException("Given text was neither literal nor translatable.");

		return builder
				.withStyle(text.getStyle())
				.withChildren(text.getSiblings().stream()
						.map(PrivateCompat16::builderFromText)
						.collect(Collectors.toList()));
	}
}
