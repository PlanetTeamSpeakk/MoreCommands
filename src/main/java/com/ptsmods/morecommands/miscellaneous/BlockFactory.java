package com.ptsmods.morecommands.miscellaneous;

import java.lang.reflect.Modifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.ptsmods.morecommands.MoreCommands;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public class BlockFactory<T extends Block> {

	private Class<T>			blockClass;
	private ResourceLocation	location;
	private String				unlocalizedName;

	/**
	 * Creates a block with it's registry name and unlocalized name already set.
	 *
	 * @param blockClass      The class of the block.
	 * @param location        The registry name.
	 * @param unlocalizedName The unlocalized in-game name, used for translation
	 *                            files.
	 */
	public BlockFactory(Class<T> blockClass, ResourceLocation location, String unlocalizedName) {
		MoreCommands.setLogger();
		this.blockClass = blockClass;
		this.location = location;
		this.unlocalizedName = unlocalizedName;
	}

	/**
	 * Creates a block with it's registry name and unlocalized name already set.
	 *
	 * @deprecated Remove the unlocalizedName parameter.
	 * @param blockClass      The class of the block.
	 * @param location        The registry name.
	 * @param unlocalizedName The unlocalized in-game name, used for translation
	 *                            files.
	 */
	@Deprecated
	public BlockFactory(Class<T> blockClass, String location, String unlocalizedName) {
		this(blockClass, new ResourceLocation(location), unlocalizedName);
	}

	public BlockFactory(Class<T> blockClass, String location) {
		this(blockClass, new ResourceLocation(location), getUnlocalizedNameFromResourceLocation(location));
	}

	public BlockFactory(Class<T> blockClass, ResourceLocation location) {
		this(blockClass, location, getUnlocalizedNameFromResourceLocation(location.toString()));
	}

	/**
	 * Creates a block with it's registry name and unlocalized name already set.
	 *
	 * @deprecated Remove the unlocalizedName parameter.
	 * @param blockClass      The class of the block.
	 * @param location        The registry name.
	 * @param unlocalizedName The unlocalized in-game name, used for translation
	 *                            files.
	 */
	@Deprecated
	public BlockFactory(T block, String location, String unlocalizedName) {
		this((Class<T>) block.getClass(), new ResourceLocation(location), unlocalizedName);
	}

	/**
	 * Creates a block with it's registry name and unlocalized name already set.
	 *
	 * @deprecated Remove the unlocalizedName parameter.
	 * @param blockClass      The class of the block.
	 * @param location        The registry name.
	 * @param unlocalizedName The unlocalized in-game name, used for translation
	 *                            files.
	 */
	@Deprecated
	public BlockFactory(T block, ResourceLocation location, String unlocalizedName) {
		this((Class<T>) block.getClass(), location, unlocalizedName);
	}

	public BlockFactory(T block, String location) {
		this((Class<T>) block.getClass(), new ResourceLocation(location), getUnlocalizedNameFromResourceLocation(location));
	}

	public BlockFactory(T block, ResourceLocation location) {
		this((Class<T>) block.getClass(), location, getUnlocalizedNameFromResourceLocation(location.toString()));
	}

	/**
	 * Gets a new instance of the {@code Class<? extends Block>} passed.
	 *
	 * @return A new instance of the {@code Class<? extends Block>} passed.
	 * @throws AbstractBlockException
	 * @throws ReflectiveOperationException
	 */
	@Nonnull
	public T getBlock() throws AbstractBlockException, ReflectiveOperationException {
		try {
			return (T) blockClass.newInstance().setUnlocalizedName(unlocalizedName).setRegistryName(location);
		} catch (InstantiationException | IllegalAccessException e) {
			if (Modifier.isAbstract(blockClass.getModifiers())) throw new AbstractBlockException("Could not create a new instance of the block because its class is abstract.", e);
			else throw e;
		}
	}

	/**
	 * Gets a new instance of the {@code Class<? extends Block>} passed, but
	 * consumes any exceptions thrown. If an exception occurs, its stacktrace is
	 * printed and {@code null} is returned.
	 *
	 * @return A new instance of the {@code Class<? extends Block>} passed if not
	 *         abstract, otherwise {@code null}.
	 */
	@Nullable
	public T getBlockNoExceptions() {
		try {
			return getBlock();
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Converts something like morecommands:locked_chest to lockedChest.
	 *
	 * @param location A ResourceLocation in String format.
	 * @return A String containing an unlocalizedName used for translation.
	 */
	private static String getUnlocalizedNameFromResourceLocation(String location) {
		String unlocalizedName = "";
		boolean capitalizeChar = false;
		if (location.split(":").length != 1) {
			char[] chars = new char[location.split(":")[1].length()];
			location.split(":")[1].getChars(0, location.split(":")[1].length(), chars, 0);
			for (Character ch : chars)
				if (!ch.equals('_')) {
					unlocalizedName += capitalizeChar ? Character.toUpperCase(ch) : ch;
					capitalizeChar = false;
				} else capitalizeChar = true;
		}
		return unlocalizedName;
	}

}
