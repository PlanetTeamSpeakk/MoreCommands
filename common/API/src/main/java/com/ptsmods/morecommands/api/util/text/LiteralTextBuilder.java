package com.ptsmods.morecommands.api.util.text;

import com.ptsmods.morecommands.api.util.compat.Compat;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;

public interface LiteralTextBuilder extends TextBuilder<LiteralTextBuilder> {

    String getLiteral();

    LiteralTextBuilder setLiteral(String literal);

    default LiteralTextBuilder format(Object... formattings) {
        setLiteral(String.format(getLiteral(), formattings));
        return upcast();
    }

    static LiteralTextBuilder builder(@NonNull String literal) {
        return new LiteralTextBuilderImpl(literal);
    }

    static LiteralTextBuilder builder(@NonNull String literal, Object... formattings) {
        return builder(literal).format(formattings);
    }

    static LiteralTextBuilder builder(@NonNull String literal, Style style) {
        return new LiteralTextBuilderImpl(literal, style);
    }

    static LiteralTextBuilder builder(@NonNull String literal, Style style, Object... formattings) {
        return builder(literal, style).format(formattings);
    }

    static MutableText literal(String literal) {
        return builder(literal).build();
    }

    static MutableText literal(String literal, Object... formattings) {
        return builder(literal, formattings).build();
    }

    static MutableText literal(String literal, Style style) {
        return builder(literal, style).build();
    }

    static MutableText literal(String literal, Style style, Object... formattings) {
        return builder(literal, style, formattings).build();
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    class LiteralTextBuilderImpl extends TextBuilderImpl<LiteralTextBuilder> implements LiteralTextBuilder {
        private @NonNull String literal;

        LiteralTextBuilderImpl(@NonNull String literal, Style style) {
            this(literal);
            withStyle(style);
        }

        @Override
        public MutableText build() {
            return Compat.get().buildText(this);
        }

        @Override
        public LiteralTextBuilder copy() {
            return builder(getLiteral())
                    .withStyle(getStyle())
                    .withChildren(getChildren());
        }

        @Override
        public final LiteralTextBuilder upcast() {
            return this;
        }

        @NonNull
        @Override
        public String getLiteral() {
            return literal;
        }

        @Override
        public LiteralTextBuilder setLiteral(@NonNull String literal) {
            this.literal = literal;
            return upcast();
        }
    }
}
