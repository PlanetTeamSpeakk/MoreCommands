package com.ptsmods.morecommands.api;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation") // We support Java 8 too (for the time being, at least) so we must use #isAccessible and cannot use #canAccess.
public class ReflectionHelper {
	public static final Logger LOG = LogManager.getLogger("MoreCommands-Reflection");
	private static final Map<Class<?>, Map<String, Field>> cachedFields = new HashMap<>();
	private static final Map<Class<?>, Map<String, Method>> cachedMethods = new HashMap<>();
	private static final Map<Class<?>, Map<String, Constructor<?>>> cachedConstructors = new HashMap<>();

	public static <T, X> T getFieldValue(Class<? extends X> clazz, String name, X instance) {
		return getFieldValue(getField(clazz, name), instance);
	}

	public static <T, X> T getFieldValue(Field f, X instance) {
		if (f == null) return null;
		if (!f.isAccessible()) f.setAccessible(true);
		try {
			return cast(f.get(instance));
		} catch (IllegalAccessException e) {
			LOG.catching(e);
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
			LOG.catching(e);
			return false;
		}
	}

	public static Field getYarnField(Class<?> clazz, String yarnName, String name) {
		Field f = getField(clazz, name);
		return f == null ? getField(clazz, yarnName) : f;
	}

	public static Field getField(Class<?> clazz, String field) {
		return cachedFields.computeIfAbsent(clazz, c -> new HashMap<>()).computeIfAbsent(field, fieldName -> {
			Field f;
			try {
				f = clazz.getField(fieldName);
			} catch (NoSuchFieldException e) {
				try {
					f = clazz.getDeclaredField(fieldName);
				} catch (NoSuchFieldException e0) {
					return null;
				}
			}
			if (!f.isAccessible()) f.setAccessible(true);
			return f;
		});
	}

	public static MethodHandle unreflect(Method m) {
		try {
			return MethodHandles.lookup().unreflect(m);
		} catch (IllegalAccessException e) {
			LOG.error("Could not unreflect method " + m, e);
			return null;
		}
	}

	public static Method getYarnMethod(Class<?> clazz, String yarnName, String name, Class<?>... classes) {
		Method m = getMethod(clazz, name, classes);
		return m == null ? getMethod(clazz, yarnName, classes) : m;
	}

	public static Method getMethod(Class<?> clazz, String method, Class<?>... parameterTypes) {
		return cachedMethods.computeIfAbsent(clazz, c -> new HashMap<>()).computeIfAbsent(getMethodKey(method, parameterTypes), key -> {
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
			return m;
		});
	}

	private static String getMethodKey(String name, Class<?>... parameterTypes) {
		StringBuilder s = new StringBuilder(name);
		for (Class<?> type : parameterTypes)
			s.append('_').append(type.getName());
		return s.toString();
	}

	public static <T, X> T invokeMethod(Class<? extends X> clazz, String method, Class<?>[] parameterTypes, X instance, Object... args) {
		return invokeMethod(getMethod(clazz, method, parameterTypes), instance, args);
	}

	public static <T, X> T invokeMethod(Method method, X instance, Object... args) {
		if (method == null) return null;

		try {
			return cast(method.invoke(instance, args));
		} catch (IllegalAccessException | InvocationTargetException e) {
			LOG.catching(e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Constructor<T> getCtor(Class<T> clazz, Class<?>... classes) {
		return (Constructor<T>) cachedConstructors.computeIfAbsent(clazz, c -> new HashMap<>()).computeIfAbsent(getMethodKey("<init>", classes), k -> {
			try {
				return clazz.getConstructor(classes);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public static <T> T newInstance(Constructor<T> ctor, Object... params) {
		try {
			return ctor.newInstance(params);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			LOG.error("Couldn't invoke constructor for " + ctor.getDeclaringClass().getName() + " class.", e);
			return null;
		}
	}

	// For mixins :)
	// Thanks to https://github.com/FabricMC/fabric/blob/1.16/fabric-events-lifecycle-v0/src/main/java/net/fabricmc/fabric/mixin/event/lifecycle/MixinMinecraftServer.java#L36
	public static <T> T cast(Object o) {
		return (T) o;
	}

	public static Class<?> getMcClass(String name) {
		try {
			return Class.forName(FabricLoader.getInstance().getMappingResolver().mapClassName("intermediary", "net.minecraft." + name));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
