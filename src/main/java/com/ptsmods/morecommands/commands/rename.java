package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class rename {

	public rename() {}

	public static class Commandrename extends com.ptsmods.morecommands.miscellaneous.CommandBase {

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
			return "rename";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length != 0) {
				EntityPlayer player = getCommandSenderAsPlayer(sender);
				String name = Reference.convertColorCodes("&r" + Reference.join(args));
				player.getHeldItemMainhand().setStackDisplayName(name);
				Reference.sendMessage(player, "Your " + player.getHeldItemMainhand().getItem().getItemStackDisplayName(player.getHeldItemMainhand()) + " has been renamed to " + name + TextFormatting.RESET + ".");
			} else Reference.sendCommandUsage(sender, usage);
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "rename", "Permission to use the rename command.", true);
		}

		protected String usage = "/rename <name> Renames the item you're holding, you can use color codes like &1, &2, &3, etc.";

	}

}