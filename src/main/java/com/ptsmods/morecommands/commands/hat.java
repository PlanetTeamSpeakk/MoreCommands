package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class hat {

	public hat() {
	}

	public static class Commandhat extends com.ptsmods.morecommands.miscellaneous.CommandBase {

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
			return "hat";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			EntityPlayer player = (EntityPlayer) sender;
			if (!player.getHeldItemMainhand().isEmpty()) {
				ItemStack itemstack = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
				player.setItemStackToSlot(EntityEquipmentSlot.HEAD, player.getHeldItemMainhand());
				player.setHeldItem(EnumHand.MAIN_HAND, itemstack);
				Reference.sendMessage(player, "Enjoy your new hat.");
			} else Reference.sendMessage(player, "Please hold an item or block.");

		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "hat", "Permission to use the hat command.", true);
		}

		protected String usage = "/hat Puts the item you're holding on your head.";

	}

}