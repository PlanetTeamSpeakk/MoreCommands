package com.ptsmods.morecommands.api.util.text;

import com.ptsmods.morecommands.api.util.compat.Compat;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;

public interface LiteralTextBuilder extends TextBuilder<LiteralTextBuilder> {

    String getLiteral();

    void setLiteral(String literal);

    static LiteralTextBuilder builder(@NonNull String literal) {
        return new LiteralTextBuilderImpl(literal);
    }

    static LiteralTextBuilder builder(@NonNull String literal, Style style) {
        return new LiteralTextBuilderImpl(literal, style);
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

        @Override
        public String getLiteral() {
            return literal;
        }

        @Override
        public void setLiteral(String literal) {
            this.literal = literal;
        }
    }
}
