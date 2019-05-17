package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class breakBlock {

	public static Object instance;

	public breakBlock() {}

	public static class Commandbreak extends com.ptsmods.morecommands.miscellaneous.CommandBase {

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
		public String getName() {
			return "break";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			BlockPos block = Reference.rayTrace(getCommandSenderAsPlayer(sender), 160).getBlockPos();
			EntityPlayer player = (EntityPlayer) sender;
			if (block == null || player.getEntityWorld().getBlockState(block).getBlock() == Blocks.AIR) Reference.sendMessage(player, "You cannot break air.");
			else {
				player.getEntityWorld().setBlockToAir(block);
				Reference.sendMessage(player, "The block at X: " + block.getX() + ", Y: " + block.getY() + ", Z: " + block.getZ() + " has been broken.");
			}

		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "break", "Permission to use the break command.", true);
		}

		protected String usage = "/break Breaks the block you're looking at.";

	}

}