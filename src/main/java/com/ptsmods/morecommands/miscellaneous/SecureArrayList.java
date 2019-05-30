package com.ptsmods.morecommands.miscellaneous;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import sun.reflect.Reflection;

public class SecureArrayList<E> extends ArrayList<E> {
	private static final long	serialVersionUID	= -8628980615529174791L;
	private final List<Class>	permittedClasses;

	@SuppressWarnings("deprecation")
	public SecureArrayList() {
		this(Reference.getCallerClassQuickly());
	}

	public SecureArrayList(Class... permittedClasses) {
		super();
		this.permittedClasses = ImmutableList.copyOf(permittedClasses);
	}

	public SecureArrayList(Collection<? extends E> c, Class... permittedClasses) {
		super(c);
		this.permittedClasses = ImmutableList.copyOf(permittedClasses);
	}

	public SecureArrayList(int initialCapacity, Class... permittedClasses) {
		super(initialCapacity);
		this.permittedClasses = ImmutableList.copyOf(permittedClasses);
	}

	@Override
	public boolean add(E e) {
		check();
		return super.add(e);
	}

	@Override
	public void add(int index, E e) {
		check();
		super.add(index, e);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		check();
		return super.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		check();
		return super.addAll(index, c);
	}

	@SuppressWarnings("deprecation")
	private void check() {
		if (!permittedClasses.contains(Reflection.getCallerClass(3))) Reference.throwWithoutDeclaration(new IllegalAccessException("The class calling class, " + Reflection.getCallerClass(3).getName() + ", may not use this method."));
	}

}
