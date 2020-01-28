package com.ptsmods.morecommands.net;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.ptsmods.morecommands.miscellaneous.RecipeGUI;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ServerRecipePacket extends AbstractPacket {

	public static enum Type {
		WORKBENCH, FURNACE, BREWING;
	}

	private List<ItemStack>	stacks;
	private Type			type;

	public ServerRecipePacket() {}

	public ServerRecipePacket(IRecipe recipe) {
		stacks = new ArrayList(9);
		for (int i = 0; i <= 9; i++)
			stacks.add(ItemStack.EMPTY);
		List<Ingredient> ingredients = recipe.getIngredients();
		if (recipe instanceof ShapedRecipes) {
			ShapedRecipes shaped = (ShapedRecipes) recipe;
			for (int i = 0; i < 9; i++) {
				int i0 = i;
				switch (shaped.recipeWidth) {
				case 1:
					switch (i) {
					case 0:
						i = 1;
						break;
					case 1:
						i = 4; // Centering it as well, just because.
						break;
					case 2:
						i = 7;
						break;
					default:
						break;
					}
					break;
				case 2:
					switch (i) {
					case 2:
						i = 3;
						break;
					case 3:
						i = 4;
						break;
					case 4:
						i = 6;
						break;
					case 5:
						i = 7;
						break;
					default:
						break;
					}
					break;
				// No change needed for a width of 3.
				}
				if (shaped.recipeWidth == 1 && shaped.recipeHeight == 1) i = 4;
				if (i0 < ingredients.size()) {
					stacks.remove(i);
					stacks.add(i, ingredients.get(i0).getMatchingStacks().length == 0 ? ItemStack.EMPTY : ingredients.get(i0).getMatchingStacks()[0]);
				}
				i = i0;
			}
		} else for (int i = 0; i < ingredients.size(); i++)
			stacks.add(i, ingredients.get(i).getMatchingStacks().length == 0 ? ItemStack.EMPTY : ingredients.get(i).getMatchingStacks()[0]);
		stacks.add(9, recipe.getRecipeOutput());
		type = Type.WORKBENCH;
	}

	public ServerRecipePacket(ItemStack furnaceIn, ItemStack furnaceOut) {
		stacks = Lists.newArrayList(furnaceIn, furnaceOut);
		type = Type.FURNACE;
	}

	public ServerRecipePacket(ItemStack brewingIn, ItemStack reagent, ItemStack brewingOut) {
		stacks = Lists.newArrayList(brewingIn, reagent, brewingOut);
		type = Type.BREWING;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		type = Type.values()[buf.readByte()];
		stacks = new ArrayList();
		int size = buf.readByte();
		for (int i = 0; i < size; i++)
			stacks.add(ByteBufUtils.readItemStack(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeByte(type.ordinal());
		buf.writeByte(stacks.size());
		for (ItemStack stack : stacks)
			ByteBufUtils.writeItemStack(buf, stack);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage run(MessageContext ctx) throws Exception {
		Minecraft.getMinecraft().displayGuiScreen(new RecipeGUI(type, stacks, Minecraft.getMinecraft().player.inventory));
		return null;
	}

}
