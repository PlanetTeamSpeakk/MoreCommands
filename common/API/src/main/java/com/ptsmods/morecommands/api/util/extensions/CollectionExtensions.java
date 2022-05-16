package com.ptsmods.morecommands.api.util.extensions;

import com.google.common.collect.ImmutableList;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionExtensions {

	public static <E> List<E> immutable(Collection<E> self) {
		return ImmutableList.copyOf(self);
	}

	public static <E> List<E> merge(@NonNull Collection<E> collection, Collection<E> others) {
		return others == null ? new ArrayList<>(collection) : Stream.concat(collection.stream(), others.stream()).collect(Collectors.toList());
	}
}
