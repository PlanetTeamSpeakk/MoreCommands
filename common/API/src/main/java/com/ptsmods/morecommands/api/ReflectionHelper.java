package com.ptsmods.morecommands.api;

import dev.architectury.platform.Platform;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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

	public static Field getMappedField(Class<?> clazz, String yarn, String intermediary, String moj) {
		if (Platform.isForge()) return getField(clazz, moj);
		Field f = getField(clazz, intermediary);
		return f == null ? getField(clazz, yarn) : f;
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

	public static Method getMappedMethod(Class<?> clazz, String yarn, String intermediary, String moj, Class<?>... classes) {
		if (Platform.isForge()) return getMethod(clazz, moj, classes);
		Method m = getMethod(clazz, intermediary, classes);
		return m == null ? getMethod(clazz, yarn, classes) : m;
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

	@SuppressWarnings("unchecked")
	public static <T> Class<? extends T> getMcClass(String name, String mojName) {
		try {
			String clazz = Platform.isFabric() ? FabricLoader.getInstance().getMappingResolver().mapClassName("intermediary", "net.minecraft." + name) : mojName.replace('/', '.');
			return (Class<? extends T>) Class.forName(clazz);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> List<Class<? extends T>> getClasses(Class<T> superClass, String pckg) throws IOException {
		Path jar = IMoreCommands.get().getJar();

		List<String> classNames = new ArrayList<>();
		// It should only be a directory in the case of a dev environment, otherwise it should always be a jar file.
		if (Files.isDirectory(jar))
			try (Stream<Path> files = Files.walk(new File(jar.toAbsolutePath().toString() + File.separatorChar + String.join(File.separator, pckg.split("\\.")) + File.separatorChar).toPath())) {
				classNames.addAll(files
						.filter(path -> Files.isRegularFile(path) && !path.getFileName().toString().contains("$"))
						.map(path -> path.toAbsolutePath().toString().substring(jar.toAbsolutePath().toString().length() + 1, path.toAbsolutePath().toString().lastIndexOf('.')).replace(File.separatorChar, '.'))
						.collect(Collectors.toList()));
			}
		else {
			ZipFile zip = new ZipFile(jar.toFile());
			Enumeration<? extends ZipEntry> entries = zip.entries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (entry.getName().startsWith(pckg.replace(".", "/")) && entry.getName().endsWith(".class") &&
						!entry.getName().split("/")[entry.getName().split("/").length - 1].contains("$"))
					classNames.add(entry.getName().replace('/', '.'));
			}

			zip.close();
		}

		return classNames.stream()
				.map(name -> {
					try {
						return Class.forName(name, false, ReflectionHelper.class.getClassLoader());
					} catch (ClassNotFoundException e) {
						throw new RuntimeException(e);
					}
				})
				.filter(c -> superClass.isAssignableFrom(c) && c != superClass)
				.map(c -> (Class<? extends T>) c)
				.collect(Collectors.toList());
	}
}
