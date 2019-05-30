package com.ptsmods.morecommands.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class item {

	public item() {}

	public static class Commanditem extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public int getRequiredPermissionLevel() {
			return 2;
		}

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("i");
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return args.length == 3 ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : args.length == 1 ? getListOfStringsMatchingLastWord(args, Stream.concat(Item.REGISTRY.getKeys().stream(), PotionType.REGISTRY.getKeys().stream()).collect(Collectors.toList())) : Collections.<String>emptyList();
		}

		@Override
		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		@Override
		public String getName() {
			return "item";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length == 0 || args.length >= 2 && !Reference.isInteger(args[1])) Reference.sendCommandUsage(sender, usage);
			else {
				ItemStack stack;
				if (PotionType.REGISTRY.getKeys().contains(new ResourceLocation(args[0]))) {
					stack = new ItemStack(Items.POTIONITEM, args.length >= 2 ? Integer.parseInt(args[1]) : 64);
					PotionUtils.addPotionToItemStack(stack, PotionType.REGISTRY.getObject(new ResourceLocation(args[0])));
				} else stack = Item.getByNameOrId(args[0]) == null ? null : new ItemStack(Item.getByNameOrId(args[0]), args.length >= 2 ? Integer.parseInt(args[1]) : 64, args[0].split("/").length >= 2 && Reference.isInteger(args[0].split("/")[1]) ? Integer.parseInt(args[0].split("/")[1]) : 0);
				if (stack != null) {
					EntityPlayer player = args.length >= 3 ? getPlayer(server, sender, args[2]) : getCommandSenderAsPlayer(sender);
					player.inventory.addItemStackToInventory(stack);
					if (player != getCommandSenderAsPlayer(sender)) Reference.sendMessage(player, sender.getName() + " has given you " + (args.length >= 2 ? Integer.parseInt(args[1]) : 64) + " " + stack.getDisplayName() + ".");
					else Reference.sendMessage(player, "Your items have arrived.");
					player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
					player.inventoryContainer.detectAndSendChanges();
				} else Reference.sendMessage(sender, TextFormatting.RED + "An item with an id of", args[0], "could not be found.");
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "item", "Permission to use the item command.", true);
		}

		protected static String usage = "/item <item> [amount] [player] Gives you an item, for metadata, split with a slash, e.g. /i dirt/1 would give Coarse Dirt.";

	}

}