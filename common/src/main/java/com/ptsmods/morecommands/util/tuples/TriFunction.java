package com.ptsmods.morecommands.util.tuples;

public interface TriFunction<T, U, V, R> {
    R apply(T t, U u, V v);
}
