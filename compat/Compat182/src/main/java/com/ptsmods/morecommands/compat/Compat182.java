package com.ptsmods.morecommands.compat;

import com.google.common.collect.ImmutableMap;
import com.ptsmods.morecommands.api.ReflectionHelper;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.*;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;

public class Compat182 extends Compat18 {
	private static Class<?> registryEntryReference = null;
	private static Map<Identifier, Object> blockTags = null;

	@Override
	public <T> boolean registryContainsId(SimpleRegistry<T> registry, Identifier id) {
		// Used to be client-only, now it's not.
		// In 1.18.2, the field used by this method was changed from a BiMap of key Identifier and value T, to a map of key Identifier and value Reference of type T.
		return registry.containsId(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean tagContains(Object tag, Object obj) {
		Object registryEntry = obj;

		if (registryEntryReference == null) {
			Class<?> c = null;
			try {
				c = RegistryEntry.Reference.class;
			} catch (Exception ignored) {}

			registryEntryReference = c;
		}

		for (Method method : obj.getClass().getMethods())
			if (method.getReturnType() == registryEntryReference && method.getParameterTypes().length == 0 && !Modifier.isStatic(method.getModifiers())) {
				try {
					registryEntry = method.invoke(obj);
				} catch (IllegalAccessException | InvocationTargetException e) {
					ReflectionHelper.LOG.error("Could not get registry entry of object " + obj);
					return false;
				}
			}

		if (!(registryEntry instanceof RegistryEntry<?>)) return false;

		return ((RegistryEntry<Object>) registryEntry).isIn((TagKey<Object>) tag);
	}

	@Override
	public <E> Registry<E> getRegistry(DynamicRegistryManager manager, RegistryKey<? extends Registry<E>> key) {
		return manager.get(key); // It's an interface starting from 1.18.2
	}

	@Override
	public Biome getBiome(World world, BlockPos pos) {
		return world.getBiome(pos).value();
	}

	@Override
	public Map<Identifier, Object> getBlockTags() {
		return blockTags == null ? blockTags = Arrays.stream(BlockTags.class.getFields())
				.map(f -> ReflectionHelper.<TagKey<?>, Object>getFieldValue(f, null))
				.collect(ImmutableMap.toImmutableMap(TagKey::id, tag -> tag)) : blockTags;
	}
}
