package com.ptsmods.morecommands.commands;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class setHome {

	public setHome() {
	}

	public static class CommandsetHome extends com.ptsmods.morecommands.miscellaneous.CommandBase {

	    public int getRequiredPermissionLevel() {
	        return 0;
	    }

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public String getName() {
			return "sethome";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			EntityPlayer player = (EntityPlayer) sender;
			try {
				Reference.addHome(player, player.getPositionVector(), MathHelper.wrapDegrees(player.rotationYaw), MathHelper.wrapDegrees(player.rotationPitch));
				Reference.saveHomesFile();
				Reference.sendMessage(player, "Your home has been set.");
			} catch (IOException e) {
				Reference.sendMessage(sender, "An unknown error occured while reading the file.");
				return;
			}
		}
		
		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}
		
		protected String usage = "/sethome Sets your home.";

	}

}