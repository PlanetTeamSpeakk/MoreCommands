package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class showNBT {

	public showNBT() {}

	public static class CommandshowNBT extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "shownbt";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (!(sender instanceof EntityLivingBase)) Reference.sendMessage(sender, TextFormatting.RED + "Only living entities may use this command.");
			else {
				ItemStack holding = ((EntityLivingBase) sender).getHeldItemMainhand();
				if (holding.hasTagCompound() && !holding.getTagCompound().isEmpty()) Reference.sendMessage(sender, "Your " + holding.getDisplayName() + Reference.dtf + " has the following NBT data: " + TextFormatting.getTextWithoutFormattingCodes(holding.getTagCompound().toString()).replaceAll("\\\\", ""));
				else Reference.sendMessage(sender, "The item you're holding doesn't have any NBT data.");
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "shownbt", "Permission to use the shownbt command.", true);
		}

		protected String usage = "/shownbt Shows the NBT data of the item you're holding.";

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			return true;
		}

	}

}