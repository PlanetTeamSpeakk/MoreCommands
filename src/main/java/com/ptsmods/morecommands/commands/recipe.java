package com.ptsmods.morecommands.commands;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;
import com.ptsmods.morecommands.net.ServerRecipePacket;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IRegistryDelegate;

public class recipe {

	public recipe() {}

	public static class Commandrecipe extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			if (args.length == 1) {
				List<ResourceLocation> list = new ArrayList();
				for (IRecipe recipe : CraftingManager.REGISTRY)
					if (!list.contains(recipe.getRecipeOutput().getItem().getRegistryName())) list.add(recipe.getRecipeOutput().getItem().getRegistryName());
				for (Entry<ItemStack, ItemStack> recipe : FurnaceRecipes.instance().getSmeltingList().entrySet())
					if (!list.contains(recipe.getValue().getItem().getRegistryName())) list.add(recipe.getValue().getItem().getRegistryName());
				list.addAll(PotionType.REGISTRY.getKeys());
				return getListOfStringsMatchingLastWord(args, list);
			} else return new ArrayList();
		}

		@Override
		public String getName() {
			return "recipe";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws PlayerNotFoundException {
			if (args.length == 0) Reference.sendCommandUsage(sender, usage);
			else {
				boolean isPot = false;
				Item item;
				int meta = 0;
				if (PotionType.REGISTRY.containsKey(new ResourceLocation(args[0]))) {
					item = Items.POTIONITEM;
					isPot = true;
				} else if (args[0].split("/").length > 1) {
					item = Item.REGISTRY.getObject(new ResourceLocation(args[0].split("/")[0]));
					meta = Reference.isInteger(args[0].split("/")[1]) ? Integer.parseInt(args[0].split("/")[1]) : 0;
				} else item = Item.REGISTRY.getObject(new ResourceLocation(args[0]));
				if (item == null) Reference.sendMessage(sender, TextFormatting.RED + "The given item could not be found.");
				else if (!(sender instanceof EntityPlayer)) Reference.sendMessage(sender, TextFormatting.RED + "Only players can use this command, sorry.");
				else {
					// Ngl, I am rather proud of this command, not as proud as I was of the
					// permissions system when that first came out, though.
					if (!isPot) {
						for (IRecipe recipe : ForgeRegistries.RECIPES.getValuesCollection())
							if (recipe.getRecipeOutput().getItem() == item && recipe.getRecipeOutput().getMetadata() == meta) {
								Reference.netWrapper.sendTo(new ServerRecipePacket(recipe), getCommandSenderAsPlayer(sender));
								return;
							}
						for (Entry<ItemStack, ItemStack> recipe : FurnaceRecipes.instance().getSmeltingList().entrySet())
							if (recipe.getValue().getItem() == item && recipe.getValue().getMetadata() == meta) {
								Reference.netWrapper.sendTo(new ServerRecipePacket(recipe.getKey(), recipe.getValue()), getCommandSenderAsPlayer(sender));
								return;
							}
					} else try {
						Class ph = PotionHelper.class;
						Field f = Reference.getFieldMapped(ph, "POTION_TYPE_CONVERSIONS", "field_185213_a");
						f.setAccessible(true);
						List vanPredicates = (List) f.get(null);
						// f = ph.getDeclaredField("POTION_ITEM_CONVERSIONS");
						// f.setAccessible(true);
						// vanPredicates.addAll((List) f.get(null));
						List<PotionMixPredicate> predicates = new ArrayList();
						for (Object o : vanPredicates) {
							Field input = Reference.getFieldMapped(o.getClass(), "input", "field_185198_a");
							input.setAccessible(true);
							Field reagent = Reference.getFieldMapped(o.getClass(), "reagent", "field_185199_b");
							reagent.setAccessible(true);
							Field output = Reference.getFieldMapped(o.getClass(), "output", "field_185200_c");
							output.setAccessible(true);
							predicates.add(new PotionMixPredicate((IRegistryDelegate) input.get(o), (Ingredient) reagent.get(o), (IRegistryDelegate) output.get(o)));
						}
						for (PotionMixPredicate pred : predicates) {
							if (!(pred.input.get() instanceof PotionType) || !(pred.output.get() instanceof PotionType)) continue;
							if (pred.output.get().equals(PotionType.REGISTRY.getObject(new ResourceLocation(args[0])))) {
								ItemStack input = new ItemStack(Items.POTIONITEM, 1);
								PotionUtils.addPotionToItemStack(input, (PotionType) pred.input.get());
								ItemStack reagent = pred.reagent.getMatchingStacks().length == 0 ? ItemStack.EMPTY : pred.reagent.getMatchingStacks()[0];
								ItemStack output = new ItemStack(Items.POTIONITEM, 1);
								new PotionEffect(MobEffects.ABSORPTION, 9600, 0);
								PotionUtils.addPotionToItemStack(output, (PotionType) pred.output.get());
								Reference.netWrapper.sendTo(new ServerRecipePacket(input, reagent, output), getCommandSenderAsPlayer(sender));
								return;
							}
						}
					} catch (Exception e) {
						Reference.sendMessage(sender, TextFormatting.DARK_RED + "An unknown error occurred while trying to get the recipes for potions.");
						e.printStackTrace();
						return;
					}
					Reference.sendMessage(sender, TextFormatting.RED + "A recipe for the given item could not be found.");
				}
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "recipe", "Shows you the recipe of various items.", true);
		}

		private String usage = "/recipe <item> Shows you the recipe of various items, for metadata, split with a slash, e.g. dirt/1 would result in the recipe for Coarse Dirt.";

		public static class PotionMixPredicate {

			public final IRegistryDelegate	input, output;
			public final Ingredient			reagent;

			private PotionMixPredicate(IRegistryDelegate input, Ingredient reagent, IRegistryDelegate output) {
				this.input = input;
				this.reagent = reagent;
				this.output = output;
			}

			@Override
			public String toString() {
				return "PotionMixPredicate{Input:{" + input.name() + "},Reagent:{Ingredient{" + Arrays.toString(reagent.getMatchingStacks()) + "}},Output:{" + output.name() + "}}";
			}

		}

	}

}