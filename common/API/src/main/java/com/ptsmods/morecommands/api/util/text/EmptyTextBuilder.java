package com.ptsmods.morecommands.api.util.text;

import com.ptsmods.morecommands.api.util.compat.Compat;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;

public interface EmptyTextBuilder extends TextBuilder<EmptyTextBuilder> {

    static EmptyTextBuilder builder() {
        return new EmptyTextBuilderImpl();
    }

    static EmptyTextBuilder builder(Style style) {
        return new EmptyTextBuilderImpl(style);
    }

    static MutableText empty() {
        return builder().build();
    }

    static MutableText empty(Style style) {
        return builder(style).build();
    }

    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    class EmptyTextBuilderImpl extends TextBuilderImpl<EmptyTextBuilder> implements EmptyTextBuilder {

        EmptyTextBuilderImpl(Style style) {
            withStyle(style);
        }

        @Override
        public MutableText build() {
            return Compat.get().buildText(this);
        }

        @Override
        public EmptyTextBuilder copy() {
            return builder()
                    .withStyle(getStyle())
                    .withChildren(getChildren());
        }

        @Override
        public EmptyTextBuilder upcast() {
            return this;
        }
    }
}
