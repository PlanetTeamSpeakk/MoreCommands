package com.ptsmods.morecommands.commands;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class recipes {

	public recipes() {}

	public static class Commandrecipes extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public List getAliases() {
			return Lists.newArrayList();
		}

		@Override
		public List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "recipes";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws PlayerNotFoundException {
			// @formatter:off
//			IRecipe recipe = new ShapedRecipes("saddle", 3, 2, NonNullList.from(new Ingredient(new ItemStack(Items.AIR)), new Ingredient(new ItemStack(Items.LEATHER)), new Ingredient(new ItemStack(Items.LEATHER)), new Ingredient(new ItemStack(Items.LEATHER)), new Ingredient(new ItemStack(Items.LEATHER)), new Ingredient(new ItemStack(Items.STRING)), new Ingredient(new ItemStack(Items.LEATHER))), new ItemStack(Items.SADDLE));
//			CraftingManager.REGISTRY.register(findNextFreeId(), new ResourceLocation(Reference.MOD_ID, "custom_0"), recipe);
			// @formatter:on
			EntityPlayer player = getCommandSenderAsPlayer(sender);
			player.openGui(MoreCommands.INSTANCE, 0, player.getEntityWorld(), player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
			Reference.sendMessage(sender, "Saddle recipe added");
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "recipes", "Create your own recipes.", true);
		}

		private String usage = "/recipes <create|delete|list>";

	}

	public static class Ingredient extends net.minecraft.item.crafting.Ingredient {
		public Ingredient(ItemStack... stacks) { // Making the constructor public
			super(stacks);
		}
	}

}