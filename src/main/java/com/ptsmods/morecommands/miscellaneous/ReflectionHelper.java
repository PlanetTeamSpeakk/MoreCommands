package com.ptsmods.morecommands.miscellaneous;

import com.google.common.base.MoreObjects;
import com.ptsmods.morecommands.MoreCommands;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class ReflectionHelper {

    private static final Map<Class<?>, Map<String, Field>> cachedFields = new HashMap<>();
    private static final Map<Class<?>, Map<String, Method>> cachedMethods = new HashMap<>();

    public static <T, X> T getYarnFieldValue(Class<X> clazz, String yarnName, String name, X instance) {
        Field f = getField(clazz, yarnName);
        return f == null ? getFieldValue(clazz, name, instance) : getFieldValue(clazz, yarnName, instance);
    }

    public static <T, X> boolean setYarnFieldValue(Class<X> clazz, String yarnName, String name, X instance, T value) {
        return setFieldValue(clazz, yarnName, instance, value) || setFieldValue(clazz, name, instance, value);
    }

    public static <T, X> T getFieldValue(Class<X> clazz, String name, X instance) {
        Field f = getField(clazz, name);
        try {
            return (T) f.get(instance);
        } catch (IllegalAccessException e) {
            MoreCommands.log.catching(e);
            return null;
        }
    }

    public static <T, X> boolean setFieldValue(Class<X> clazz, String name, X instance, T value) {
        Field f = getField(clazz, name);
        if (f == null) return false;
        boolean isFinal = Modifier.isFinal(f.getModifiers());
        if (isFinal) removeModifier(f, Modifier.FINAL);
        try {
            f.set(instance, value);
            return true;
        } catch (IllegalAccessException e) {
            MoreCommands.log.catching(e);
            return false;
        } finally {
            if (isFinal) addModifier(f, Modifier.FINAL);
        }
    }

    public static Field getYarnField(Class<?> clazz, String yarnName, String name) {
        return MoreObjects.firstNonNull(getField(clazz, yarnName), getField(clazz, name));
    }

    public static Method getYarnMethod(Class<?> clazz, String yarnName, String name, Class<?>... classes) {
        return MoreObjects.firstNonNull(getMethod(clazz, yarnName, classes), getMethod(clazz, name, classes));
    }

    public static void addModifier(Field f, int modifier) {
        setFieldValue(Field.class, "modifiers", f, f.getModifiers() & modifier);
    }

    public static void removeModifier(Field f, int modifier) {
        setFieldValue(Field.class, "modifiers", f, f.getModifiers() & ~modifier);
    }

    public static Method getMethod(Class<?> clazz, String method, Class<?>... parameterTypes) {
        if (cachedMethods.containsKey(clazz) && cachedMethods.get(clazz).containsKey(getMethodKey(method, parameterTypes))) return cachedMethods.get(clazz).get(getMethodKey(method, parameterTypes));
        Method m;
        try {
            m = clazz.getMethod(method, parameterTypes);
        } catch (NoSuchMethodException e) {
            try {
                m = clazz.getDeclaredMethod(method, parameterTypes);
            } catch (NoSuchMethodException e0) {
                return null;
            }
        }
        if (!m.isAccessible()) m.setAccessible(true);
        if (!cachedMethods.containsKey(clazz)) cachedMethods.put(clazz, new HashMap<>());
        cachedMethods.get(clazz).put(getMethodKey(method, parameterTypes), m);
        return m;
    }

    public static Field getField(Class<?> clazz, String field) {
        if (cachedFields.containsKey(clazz) && cachedFields.get(clazz).containsKey(field)) return cachedFields.get(clazz).get(field);
        Field f;
        try {
            f = clazz.getField(field);
        } catch (NoSuchFieldException e) {
            try {
                f = clazz.getDeclaredField(field);
            } catch (NoSuchFieldException e0) {
                return null;
            }
        }
        if (!f.isAccessible()) f.setAccessible(true);
        if (!cachedFields.containsKey(clazz)) cachedFields.put(clazz, new HashMap<>());
        cachedFields.get(clazz).put(field, f);
        return f;
    }

    private static String getMethodKey(String name, Class<?>... parameterTypes) {
        StringBuilder s = new StringBuilder(name);
        for (Class<?> type : parameterTypes)
            s.append('_').append(type.getName());
        return s.toString();
    }

}
