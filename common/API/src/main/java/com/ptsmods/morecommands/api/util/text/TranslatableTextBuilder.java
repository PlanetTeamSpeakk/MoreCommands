package com.ptsmods.morecommands.api.util.text;

import com.ptsmods.morecommands.api.util.compat.Compat;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.Objects;

public interface TranslatableTextBuilder extends TextBuilder<TranslatableTextBuilder> {

    String getKey();

    void setKey(String key);

    Object[] getArgs();

    void setArgs(Object... args);

    static TranslatableTextBuilder builder(String key) {
        return new TranslatableTextBuilderImpl(key);
    }

    static TranslatableTextBuilder builder(String key, Object... args) {
        return new TranslatableTextBuilderImpl(key, args);
    }

    static TranslatableTextBuilder builder(String key, Style style) {
        return new TranslatableTextBuilderImpl(key, style);
    }

    static TranslatableTextBuilder builder(String key, Style style, Object... args) {
        return new TranslatableTextBuilderImpl(key, style, args);
    }

    static MutableComponent translatable(String key) {
        return builder(key).build();
    }

    static MutableComponent translatable(String key, Object... args) {
        return builder(key, args).build();
    }

    static MutableComponent translatable(String key, Style style) {
        return builder(key, style).build();
    }

    static MutableComponent translatable(String key, Style style, Object... args) {
        return builder(key, style, args).build();
    }

    class TranslatableTextBuilderImpl extends TextBuilder.TextBuilderImpl<TranslatableTextBuilder> implements TranslatableTextBuilder {
        private static final Object[] EMPTY_ARGS = new Object[0];
        private String key;
        private Object[] args;

        TranslatableTextBuilderImpl(String key) {
            this.key = key;
            this.args = EMPTY_ARGS;
        }

        TranslatableTextBuilderImpl(String key, Object... args) {
            this.key = Objects.requireNonNull(key);
            this.args = args == null || args.length == 0 ? EMPTY_ARGS : args;
        }

        TranslatableTextBuilderImpl(String key, Style style) {
            this(key);
            withStyle(style);
        }

        TranslatableTextBuilderImpl(String key, Style style, Object... args) {
            this(key, args);
            withStyle(style);
        }

        @Override
        public MutableComponent build() {
            return Compat.get().buildText(this);
        }

        @Override
        public TranslatableTextBuilder copy() {
            return builder(getKey(), getArgs())
                    .withStyle(getStyle())
                    .withChildren(getChildren());
        }

        @Override
        public final TranslatableTextBuilder upcast() {
            return this;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public void setKey(String key) {
            this.key = Objects.requireNonNull(key);
        }

        @Override
        public Object[] getArgs() {
            return args;
        }

        @Override
        public void setArgs(Object... args) {
            this.args = args == null || args.length == 0 ? EMPTY_ARGS : args;
        }
    }
}
