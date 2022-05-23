package com.ptsmods.morecommands.api.util.extensions;

import lombok.NonNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class ObjectExtensions {

    @NonNull
    public static <T> T or(T self, T other) {
        return self == null ? Objects.requireNonNull(other) : self;
    }

    @NonNull
    public static <T> T or(T self, Supplier<T> supplier) {
        return self == null ? Objects.requireNonNull(supplier.get()) : self;
    }

    public static <T, R> R ifNonNull(T self, @NonNull Function<T, R> mapper) {
        return self == null ? null : mapper.apply(self);
    }
}
