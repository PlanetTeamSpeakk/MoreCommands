package com.ptsmods.morecommands.compat;

import com.ptsmods.morecommands.api.util.text.EmptyTextBuilder;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.api.util.text.TextBuilder;
import com.ptsmods.morecommands.api.util.text.TranslatableTextBuilder;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

// Purely exists for when Java does weird stuff saying it can't find classes that were
// deleted when loading Compat16 as a result of loading a more recent Compat that
// extends it.
class PrivateCompat16 {
    public static MutableComponent buildText(LiteralTextBuilder builder) {
        return buildText(new TextComponent(builder.getLiteral()), builder);
    }

    public static MutableComponent buildText(TranslatableTextBuilder builder) {
        return buildText(Objects.requireNonNull(builder.getArgs().length == 0 ? new TranslatableComponent(builder.getKey()) : new TranslatableComponent(builder.getKey(), Arrays.stream(builder.getArgs())
                        .map(o -> o instanceof TextBuilder ? ((TextBuilder<?>) o).build() : o)
                        .toArray(Object[]::new))), builder);
    }

    public static MutableComponent buildText(EmptyTextBuilder builder) {
        return buildText(TextComponent.EMPTY.copy(), builder);
    }

    private static MutableComponent buildText(MutableComponent text, TextBuilder<?> builder) {
        text.setStyle(builder.getStyle());
        builder.getChildren().forEach(child -> text.append(child.build()));
        return text;
    }

    public static TextBuilder<?> builderFromText(Component text) {
        TextBuilder<?> builder;
        if (text instanceof TextComponent)
            builder = LiteralTextBuilder.builder(((TextComponent) text).getText());
        else if (text instanceof TranslatableComponent)
            builder = TranslatableTextBuilder.builder(((TranslatableComponent) text).getKey(), Arrays.stream(((TranslatableComponent) text).getArgs())
                    .map(o -> o instanceof Component ? builderFromText((Component) o) : o)
                    .toArray(Object[]::new));
        else throw new IllegalArgumentException("Given text was neither literal nor translatable.");

        return builder
                .withStyle(text.getStyle())
                .withChildren(text.getSiblings().stream()
                        .map(PrivateCompat16::builderFromText)
                        .collect(Collectors.toList()));
    }
}
