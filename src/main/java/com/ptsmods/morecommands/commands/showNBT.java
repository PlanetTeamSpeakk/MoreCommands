package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class showNBT {

	public showNBT() {
	}

	public static class CommandshowNBT implements ICommand {

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public String getName() {
			return "shownbt";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			EntityPlayer player = (EntityPlayer) sender;
			ItemStack holding = player.getHeldItemMainhand();
			if (holding.hasTagCompound() && !(holding.getTagCompound().equals(new NBTTagCompound()))) {
				String data = holding.getTagCompound().toString().substring(1, holding.getTagCompound().toString().length()-1);
				String[] dataArray = data.split(",");
				String dataFinal = "";
				for (int x = 0; x < dataArray.length; x += 1) {
					dataFinal += dataArray[x];
					if (x+1 != dataArray.length) dataFinal += " ";
				}
				dataFinal = TextFormatting.getTextWithoutFormattingCodes(dataFinal);
				Reference.sendMessage(player, "The item you're holding has the following NBT data: " + dataFinal);
			} else {
				Reference.sendMessage(player, "THe item you're holding doesn't have any NBT data.");
			}

		}
		
		protected String usage = "/shownbt Shows the NBT data of the item you're holding.";

		@Override
		public int compareTo(ICommand o) {
			return 0;
		}

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			return true;
		}

		@Override
		public boolean isUsernameIndex(String[] args, int index) {
			return false;
		}

	}

}