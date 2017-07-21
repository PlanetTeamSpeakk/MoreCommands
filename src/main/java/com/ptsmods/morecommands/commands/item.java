package com.ptsmods.morecommands.commands;

import java.util.ArrayList;
import java.util.Collections;

import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class item {

	public static Object instance;

	public item() {
	}

	public static class Commanditem extends CommandBase {
		public boolean isUsernameIndex(int sender) {
			return false;
		}

	    public int getRequiredPermissionLevel() {
	        return 2;
	    }

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("i");
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return args.length == 3 ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : (args.length == 1 ? getListOfStringsMatchingLastWord(args, Item.REGISTRY.getKeys()) : Collections.<String>emptyList());
		}

		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		public String getName() {
			return "item";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			EntityPlayer player = (EntityPlayer) sender;
			ItemStack itemstack = new ItemStack(Items.AIR, 0);
			Integer amount = 64;
			if (args.length == 0) {
				Reference.sendCommandUsage(player, usage);
				return;
			} else if (args.length == 1) {
				Item item = getItemByText(sender, args[0]);
				itemstack = new ItemStack(item, 64);
			} else if (args.length == 2) {
				if (Reference.isInteger(args[1])) {
					Item item = getItemByText(sender, args[0]);
					itemstack = new ItemStack(item, Integer.parseInt(args[1]));
				} else {
					Reference.sendCommandUsage(player, usage);
					return;
				}
			} else if (args.length == 3) {
				if (Reference.isInteger(args[1])) {
					Item item = getItemByText(sender, args[0]);
					itemstack = new ItemStack(item, Integer.parseInt(args[1]));
					amount = Integer.parseInt(args[1]);
					try {
						player = getPlayer(server, sender, args[2]);
					} catch (PlayerNotFoundException e) {
						Reference.sendMessage(player, "The given player does not exist.");
						return;
					}
				} else {
					Reference.sendCommandUsage(player, usage);
					return;
				}
			}
			
			player.inventory.addItemStackToInventory(itemstack);
			if (player != (EntityPlayer) sender) {
				Reference.sendMessage(player, sender.getName() + " has given you " + amount.toString() + " " + args[0] + ".");
			} else {
				Reference.sendMessage(player, "Your items have arrived.");
			}
			player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            player.inventoryContainer.detectAndSendChanges();

		}
		
		protected static String usage = "/item <item> [amount] [player] Gives you an item.";

	}

}