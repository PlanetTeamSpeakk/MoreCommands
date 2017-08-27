package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class opTool {

	public opTool() {
	}

	public static class CommandopTool extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public int getRequiredPermissionLevel() {
			return 2;
		}

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
			return "optool";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			EntityPlayer player = (EntityPlayer) sender;
			NBTTagCompound nbt = new NBTTagCompound();
			String nbtData = "{ench:[";
			for (int x = 0; x <= 10000; x += 1)
				if (Enchantment.getEnchantmentByID(x) != null && x != 71) nbtData += "{lvl:16384s,id:" + Integer.toString(x) + "s},"; // enchantment 71 is curse of vanishing.
			nbtData = nbtData.substring(0, nbtData.length()-1) + "]}";
			try {
				nbt = JsonToNBT.getTagFromJson(nbtData);
			} catch (NBTException e) {
				Reference.sendMessage(sender, "An unknown error occured while gathering the NBT data to put on your item, if you have access to it look at the console for more information.");
				e.printStackTrace();
				return;
			}
			player.getHeldItemMainhand().setTagCompound(nbt);
			Reference.sendMessage(player, "Your " + Reference.getLocalizedName(player.getHeldItemMainhand().getItem()) + " has been made OP.");

		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "optool", "Permission to use the optool command.", true);
		}

		protected String usage = "/optool Makes whatever you're holding op.";

	}

}