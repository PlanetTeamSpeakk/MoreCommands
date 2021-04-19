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
		if (f == null || Modifier.isFinal(f.getModifiers())) return false;
		try {
			f.set(instance, value);
			return true;
		} catch (IllegalAccessException e) {
			MoreCommands.log.catching(e);
			return false;
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

	// For mixins :)
	// Thanks to https://github.com/FabricMC/fabric/blob/1.16/fabric-events-lifecycle-v0/src/main/java/net/fabricmc/fabric/mixin/event/lifecycle/MixinMinecraftServer.java#L36
	public static <T> T cast(Object o) {
		return (T) o;
	}

}
