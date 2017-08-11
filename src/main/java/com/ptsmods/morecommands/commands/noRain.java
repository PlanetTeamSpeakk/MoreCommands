package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

public class noRain {

	public noRain() {
	}

	public static class CommandnoRain extends com.ptsmods.morecommands.miscellaneous.CommandBase {

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

		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		public String getName() {
			return "norain";
		}

		public String getUsage(ICommandSender sender) {
			return "/norain Stops the rain.";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			EntityPlayer player = (EntityPlayer) sender;
			World world = player.getEntityWorld();
			WorldInfo worldinfo = world.getWorldInfo();
            worldinfo.setCleanWeatherTime(20000000);
            worldinfo.setRainTime(0);
            worldinfo.setThunderTime(0);
            worldinfo.setRaining(false);
            worldinfo.setThundering(false);
            Reference.sendMessage(player, "Can't you tell I got news for you? The sun is shining and so are you.");

		}
		
		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

	}

}