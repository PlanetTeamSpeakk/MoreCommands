package com.ptsmods.morecommands.api.util.text;

import com.google.common.collect.ImmutableList;
import com.ptsmods.morecommands.api.util.extensions.ObjectExtensions;
import lombok.experimental.ExtensionMethod;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.UnaryOperator;

// Texts were overhauled in 22w16a, LiteralText and TranslatableText are now effectively text content
// and MutableText is now the main Text class.
// We cannot directly use MutableText anymore, however, as it is now a class rather than an interface,
// changing any bytecode method call made to it. So we use a builder instead.
public interface TextBuilder<T extends TextBuilder<T>> {

    MutableText build();

    Style getStyle();

    T withStyle(Style style);

    default T withStyle(UnaryOperator<Style> operator) {
        return withStyle(operator.apply(getStyle()));
    }

    default T formatted(Formatting... formattings) {
        return withStyle(style -> style.withFormatting(formattings));
    }

    T append(TextBuilder<?> text);

    default T append(String s) {
        append(LiteralTextBuilder.builder(s));
        return upcast();
    }

    List<TextBuilder<?>> getChildren();

    T withChildren(Collection<TextBuilder<?>> builders);

    T copy();

    T upcast();

    @ExtensionMethod(ObjectExtensions.class)
    abstract class TextBuilderImpl<T extends TextBuilder<T>> implements TextBuilder<T> {
        private Style style = Style.EMPTY;
        private final List<TextBuilder<?>> children = new ArrayList<>();

        @Override
        public Style getStyle() {
            return style;
        }

        @Override
        public T withStyle(Style style) {
            this.style = style.or(Style.EMPTY);
            return upcast();
        }

        @Override
        public T append(TextBuilder<?> text) {
            children.add(text);
            return upcast();
        }

        @Override
        public List<TextBuilder<?>> getChildren() {
            return ImmutableList.copyOf(children);
        }

        @Override
        public T withChildren(Collection<TextBuilder<?>> builders) {
            children.addAll(builders);
            return upcast();
        }
    }
}
