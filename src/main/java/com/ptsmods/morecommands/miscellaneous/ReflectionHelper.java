package com.ptsmods.morecommands.miscellaneous;

import com.google.common.base.MoreObjects;
import com.ptsmods.morecommands.MoreCommands;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

public class ReflectionHelper {

    private static final Map<Class<?>, Map<String, Field>> cachedFields = new HashMap<>();
    private static final Map<Class<?>, Map<String, Method>> cachedMethods = new HashMap<>();
    private static final Map<Class<?>, Map<String, Constructor<?>>> cachedConstructors = new HashMap<>();

    public static <T> T newInstance(Class<? extends T> clazz, Object... parameterArguments) {
        Class<?>[] parameterTypes = new Class[parameterArguments.length];
        for (int i = 0; i < parameterTypes.length; i++)
            parameterTypes[i] = parameterArguments[i].getClass();
        Constructor<? extends T> cons = getConstructor(clazz, parameterTypes);
        if (cons == null) return null;
        return newInstance(cons, parameterArguments);
    }

    public static <T> T newInstance(Constructor<? extends T> cons, Object... parameterArguments) {
        try {
            return cons.newInstance(parameterArguments);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    public static <T> Constructor<? extends T> getConstructor(Class<? extends T> clazz, Class<?>... parameterTypes) {
        if (cachedConstructors.containsKey(clazz) && cachedConstructors.get(clazz).containsKey(getMethodKey("init", parameterTypes))) return cast(cachedConstructors.get(clazz).get(getMethodKey("init", parameterTypes)));
        Constructor<? extends T> cons;
        try {
            cons = clazz.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            try {
                cons = clazz.getDeclaredConstructor(parameterTypes);
            } catch (NoSuchMethodException noSuchMethodException) {
                return null;
            }
        }
        if (!cons.isAccessible()) cons.setAccessible(true);
        if (!cachedConstructors.containsKey(clazz)) cachedConstructors.put(clazz, new HashMap<>());
        cachedConstructors.get(clazz).put(getMethodKey("init", parameterTypes), cons);
        return cons;
    }

    public static <T, X> T getYarnFieldValue(Class<? extends X> clazz, String yarnName, String name, X instance) {
        Field f = getField(clazz, yarnName);
        return f == null ? getFieldValue(clazz, name, instance) : getFieldValue(clazz, yarnName, instance);
    }

    public static <T, X> boolean setYarnFieldValue(Class<? extends X> clazz, String yarnName, String name, X instance, T value) {
        return setFieldValue(clazz, yarnName, instance, value) || setFieldValue(clazz, name, instance, value);
    }

    public static <T, X> T getFieldValue(Class<? extends X> clazz, String name, X instance) {
        return getFieldValue(getField(clazz, name), instance);
    }

    public static <T, X> T getFieldValue(Field f, X instance) {
        if (f == null) return null;
        if (!f.isAccessible()) f.setAccessible(true);
        try {
            return cast(f.get(instance));
        } catch (IllegalAccessException e) {
            MoreCommands.log.catching(e);
            return null;
        }
    }

    public static <T, X> boolean setFieldValue(Class<? extends X> clazz, String name, X instance, T value) {
        return setFieldValue(getField(clazz, name), instance, value);
    }

    public static <T, X> boolean setFieldValue(Field f, X instance, T value) {
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
        if (Modifier.isFinal(f.getModifiers())) removeModifier(f, Modifier.FINAL);
        if (!f.isAccessible()) f.setAccessible(true);
        if (!cachedFields.containsKey(clazz)) cachedFields.put(clazz, new HashMap<>());
        cachedFields.get(clazz).put(field, f);
        return f;
    }

    public static Method getYarnMethod(Class<?> clazz, String yarnName, String name, Class<?>... classes) {
        return MoreObjects.firstNonNull(getMethod(clazz, yarnName, classes), getMethod(clazz, name, classes));
    }

    public static Method getMethod(Class<?> clazz, String method, Class<?>... parameterTypes) {
        parameterTypes = parameterTypes == null ? new Class<?>[0] : parameterTypes;
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

    private static String getMethodKey(String name, Class<?>... parameterTypes) {
        StringBuilder s = new StringBuilder(name);
        for (Class<?> type : parameterTypes)
            s.append('_').append(type.getName());
        return s.toString();
    }

    public static <T, X> T invokeYarnMethod(Class<? extends X> clazz, String yarnName, String method, Class<?>[] parameterTypes, X instance, Object... args) {
        return invokeMethod(clazz, getMethod(clazz, yarnName, parameterTypes) == null ? method : yarnName, parameterTypes, instance, args);
    }

    public static <T, X> T invokeMethod(Class<? extends X> clazz, String method, Class<?>[] parameterTypes, X instance, Object... args) {
        Method m = getMethod(clazz, method, parameterTypes);
        if (m == null) return null;
        try {
            return cast(m.invoke(instance, args));
        } catch (IllegalAccessException | InvocationTargetException e) {
            MoreCommands.log.catching(e);
            return null;
        }
    }

    public static <T extends Enum<T>> T newEnumInstance(Class<? extends T> clazz, Class<?>[] parameterTypes, String name, Object... parameterArguments) {
        Constructor<? extends T> cons = getConstructor(clazz, ArrayUtils.addAll(new Class[] {String.class, int.class}, parameterTypes));
        Object ca = getFieldValue(Constructor.class, "constructorAccessor", cons);
        if (ca == null) ca = invokeMethod(Constructor.class, "acquireConstructorAccessor", null, cons);
        if (ca == null) {
            MoreCommands.log.error("Could not acquire ConstructorAccessor for class " + clazz.getName() + ".");
            return null;
        }
        T instance = cast(invokeMethod(ca.getClass(), "newInstance", new Class[] {Object[].class}, ca, new Object[] {ArrayUtils.addAll(new Object[] {name, clazz.getEnumConstants().length}, parameterArguments)}));
        // Following code gets the values field of the Enum class.
        // Thanks to Forge for this one https://github.com/ExtrabiomesXL/forge/blob/master/common/net/minecraftforge/common/EnumHelper.java#L205
        int flags = Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL | 0x1000 /*SYNTHETIC*/;
        String valueType = String.format("[L%s;", clazz.getName().replace('.', '/'));
        for (Field field : clazz.getDeclaredFields())
            if ((field.getModifiers() & flags) == flags && field.getType().getName().replace('.', '/').equals(valueType)) { //Apparently some JVMs return .'s and some don't..
                removeModifier(field, Modifier.FINAL);
                field.setAccessible(true);
                setFieldValue(field, null, ArrayUtils.add(ReflectionHelper.<T[], T>getFieldValue(field, null), instance));
            }
        return instance;
    }

    public static void addModifier(Field f, int modifier) {
        setFieldValue(Field.class, "modifiers", f, f.getModifiers() & modifier);
    }

    public static void removeModifier(Field f, int modifier) {
        setFieldValue(Field.class, "modifiers", f, f.getModifiers() & ~modifier);
    }

    // For mixins :)
    // Thanks to https://github.com/FabricMC/fabric/blob/1.16/fabric-events-lifecycle-v0/src/main/java/net/fabricmc/fabric/mixin/event/lifecycle/MixinMinecraftServer.java#L36
    public static <T> T cast(Object o) {
        return (T) o;
    }

}
