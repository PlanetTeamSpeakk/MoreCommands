package com.ptsmods.morecommands.compat;

import com.esotericsoftware.reflectasm.FieldAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.ptsmods.morecommands.MoreCommands;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class CompatASMReflection {
    private static final Map<Class<?>, Constructor<?>> constructorMap = new Object2ObjectOpenHashMap<>();
    private static final Map<Class<?>, MethodAccess> methodAccessMap = new Object2ObjectOpenHashMap<>();
    private static final Map<Class<?>, FieldAccess> fieldAccessMap = new Object2ObjectOpenHashMap<>();
    private static final Map<Class<?>, Map<Pair<String, Class<?>[]>, Integer>> methodIndices = new Object2ObjectOpenHashMap<>();
    private static final Map<Class<?>, Map<String, Integer>> fieldIndices = new Object2ObjectOpenHashMap<>();

    @SuppressWarnings("unchecked")
    protected <T> Constructor<T> getCtor(Class<T> clazz, Class<?>... classes) {
        // ConstructorAccess from ReflectASM only supports no-arg constructors.
        return (Constructor<T>) constructorMap.computeIfAbsent(clazz, clazz0 -> {
            try {
                return clazz0.getConstructor(classes);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }

    protected <T> T invokeCtor(Constructor<T> ctor, Object... params) {
        try {
            return ctor.newInstance(params);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            MoreCommands.LOG.error("Couldn't invoke constructor for " + ctor.getDeclaringClass().getName() + " class.", e);
            return null;
        }
    }

    protected MethodAccess getMA(Class<?> clazz) {
        return methodAccessMap.computeIfAbsent(clazz, MethodAccess::get);
    }

    protected FieldAccess getFA(Class<?> clazz) {
        return fieldAccessMap.computeIfAbsent(clazz, FieldAccess::get);
    }

    protected int getMI(Class<?> clazz, String name, Class<?>... classes) {
        Pair<String, Class<?>[]> pair = Pair.of(name, classes);
        return methodIndices.computeIfAbsent(clazz, clazz0 -> new Object2ObjectOpenHashMap<>()).computeIfAbsent(pair, pair0 -> getMA(clazz).getIndex(name, classes));
    }

    protected int getFI(Class<?> clazz, String name) {
        return fieldIndices.computeIfAbsent(clazz, clazz0 -> new Object2ObjectOpenHashMap<>()).computeIfAbsent(name, name0 -> getFA(clazz).getIndex(name0));
    }
}
