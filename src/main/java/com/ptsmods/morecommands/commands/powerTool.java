package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class powerTool {

	public powerTool() {
	}

	public static class CommandpowerTool extends CommandBase {

	    public int getRequiredPermissionLevel() {
	        return 2;
	    }
		
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("pt");
			aliases.add("ptool");
			aliases.add("powert");
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public String getName() {
			return "powertool";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			EntityPlayer player = (EntityPlayer) sender;
			ItemStack holding = player.getHeldItemMainhand();
			if (args.length == 0) {
				if (holding.hasTagCompound()) {
					if (holding.getTagCompound().hasKey("ptcmd")) {
						NBTTagCompound nbt = holding.getTagCompound();
						if (player.getUniqueID().equals(nbt.getUniqueId("ptowner"))) {
							String command = nbt.getString("ptcmd");
							nbt.removeTag("ptcmd");
							nbt.removeTag("ptownerLeast");
							nbt.removeTag("ptownerMost");
							holding.setTagCompound(holding.getTagCompound());
							Reference.sendMessage(player, "The command " + command + " has been unassigned.");
						} else {
							Reference.sendMessage(player, "This is not your powertool.");
						}
					} else {
						Reference.sendMessage(player, "The itemstack you're holding doesn't have a command assigned to it.");
					}
				} else {
					Reference.sendMessage(player, "The itemstack you're holding doesn't have any nbt data.");
				}
			} else {
				String command = "";
				for (int x = 0; x < args.length; x += 1) {
					command += args[x];
					if (x+1 != args.length) command += " ";
				}
				NBTTagCompound nbt;
				if (holding.hasTagCompound()) {
					nbt = holding.getTagCompound();
					if (nbt.hasUniqueId("ptowner") && !(nbt.getUniqueId("ptowner").equals(player.getUniqueID()))) {
						Reference.sendMessage(player, "This item already has a command asigned to it, but not by you.");
						return;
					}
				} else {
					nbt = new NBTTagCompound();
				}
				nbt.setString("ptcmd", command);
				nbt.setUniqueId("ptowner", player.getUniqueID());
				holding.setTagCompound(nbt);
				Reference.sendMessage(player, "The command " + TextFormatting.GRAY + TextFormatting.ITALIC + command + TextFormatting.RESET + " has successfully been assigned to your item using nbt. "
						+ "Do note that using the powertool may crash your minecraft due to some server ticking loop randomly giving a ConcurrentModificationException.");
			}

		}
		
		protected String usage = "/powertool <command> Assigns a command to the itemstack you're holding.";

	}

}