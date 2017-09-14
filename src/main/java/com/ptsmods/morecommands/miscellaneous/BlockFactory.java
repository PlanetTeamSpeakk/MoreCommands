package com.ptsmods.morecommands.miscellaneous;

import java.lang.reflect.Modifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public class BlockFactory {

	private Class<? extends Block> blockClass;
	private ResourceLocation location;
	private String unlocalizedName;

	/**
	 * Creates a block with it's registry name and unlocalized name already set.
	 * @param blockClass The class of the block.
	 * @param location The registry name.
	 * @param unlocalizedName The unlocalized in-game name, used for translation files.
	 */
	public BlockFactory(Class<? extends Block> blockClass, ResourceLocation location, String unlocalizedName) {
		this.blockClass = blockClass;
		this.location = location;
		this.unlocalizedName = unlocalizedName;
	}

	/**
	 * Creates a block with it's registry name and unlocalized name already set.
	 * @param blockClass The class of the block.
	 * @param location The registry name.
	 * @param unlocalizedName The unlocalized in-game name, used for translation files.
	 */
	public BlockFactory(Class<? extends Block> blockClass, String location, String unlocalizedName) {
		this(blockClass, new ResourceLocation(location), unlocalizedName);
	}

	public BlockFactory(Block block, String location, String unlocalizedName) {
		this(block.getClass(), location, unlocalizedName);
	}

	public BlockFactory(Block block, ResourceLocation location, String unlocalizedName) {
		this(block.getClass(), location, unlocalizedName);
	}

	/**
	 * Gets a new instance of the {@code Class<? extends Block>} passed.
	 * @return A new instance of the {@code Class<? extends Block>} passed.
	 * @throws AbstractBlockException
	 * @throws ReflectiveOperationException
	 */
	@Nonnull
	public Block getBlock() throws AbstractBlockException, ReflectiveOperationException {
		try {
			return blockClass.newInstance().setUnlocalizedName(unlocalizedName).setRegistryName(location);
		} catch (InstantiationException | IllegalAccessException e) {
			if (Modifier.isAbstract(blockClass.getModifiers())) throw new AbstractBlockException("Could not create a new instance of the block because it's class is abstract.", e);
			else throw e;
		}
	}

	/**
	 * Gets a new instance of the {@code Class<? extends Block>} passed, but consumes any exceptions thrown.
	 * If an exception occurs, it's stacktrace is printed and {@code null} is returned.
	 * @return A new instance of the {@code Class<? extends Block>} passed if not abstract, otherwise {@code null}.
	 */
	@Nullable
	public Block getBlockNoExceptions() {
		try {
			return getBlock();
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

}
