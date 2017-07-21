package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class resetNBT {

	public resetNBT() {
	}

	public static class CommandresetNBT extends CommandBase {

	    public int getRequiredPermissionLevel() {
	        return 2;
	    }

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public String getName() {
			return "resetnbt";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			EntityPlayer player = (EntityPlayer) sender;
			ItemStack holding = player.getHeldItemMainhand();
			if (holding.hasTagCompound()) {
				player.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(holding.getItem(), holding.getCount()));
				Reference.sendMessage(player, "The NBT data of the item you're holding has been reset.");
			} else {
				Reference.sendMessage(player, "The item you're holding doesn't have any NBT data.");
			}

		}
		
		protected String usage = "/resetnbt Resets the NBT data of the item you're holding.";

	}

}