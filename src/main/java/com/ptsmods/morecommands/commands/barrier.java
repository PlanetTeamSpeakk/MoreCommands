package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

public class barrier {

	public barrier() {}

	public static class Commandbarrier extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		public boolean isUsernameIndex(int sender) {
			return false;
		}

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
		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		@Override
		public String getName() {
			return "barrier";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return "/barrier Gives you a barrier.";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			EntityPlayer player = getCommandSenderAsPlayer(sender);
			player.inventory.addItemStackToInventory(new ItemStack(Blocks.BARRIER, 1));
			Reference.playSound(player, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS);
			player.inventoryContainer.detectAndSendChanges();
			Reference.sendMessage(sender, "Your barrier has arrived.");
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "barrier", "Get yourself a barrier block.", true);
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

	}

}