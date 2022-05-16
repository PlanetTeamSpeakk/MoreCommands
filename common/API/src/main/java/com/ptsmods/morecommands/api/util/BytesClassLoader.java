package com.ptsmods.morecommands.api.util;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class BytesClassLoader extends URLClassLoader {
	private final Map<String, byte[]> classes;
	private final Map<String, Class<?>> loaded = new HashMap<>();

	public BytesClassLoader(ClassLoader parent, Map<String, byte[]> classes) {
		super(new URL[0], parent);
		this.classes = classes;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if (classes.containsKey(name)) {
			if (loaded.containsKey(name)) return loaded.get(name);

			Class<?> c = defineClass(name, classes.get(name), 0, classes.get(name).length);
			loaded.put(name, c);
			return c;
		}
		return super.findClass(name);
	}

	public Map<String, Class<?>> loadAll() {
		return classes.keySet().stream().collect(Collectors.toMap(s -> s, s -> {
			try {
				return this.loadClass(s);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}));
	}
}
