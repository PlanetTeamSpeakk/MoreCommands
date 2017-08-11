package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class explode {

	public explode() {
	}

	public static class Commandexplode extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		public int getRequiredPermissionLevel() {
	        return 2;
	    }

		public java.util.List getAliases() {
			return new ArrayList();
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		public String getName() {
			return "explode";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			int x = sender.getPosition().getX();
			int y = sender.getPosition().getY();
			int z = sender.getPosition().getZ();
			Float power = 4F;
			
			EntityPlayer player = (EntityPlayer) sender;
			World world = player.getEntityWorld();
			player.setInvisible(true);
			Boolean fire = false;
			
			if (args.length != 0) {
				if (Reference.isInteger(args[0])) {
					power = (float) Integer.parseInt(args[0]);
				} else {
					Reference.sendCommandUsage(sender, usage);
					return;
				}
				if (args.length == 2 && Reference.isBoolean(args[1])) {
					fire = Boolean.parseBoolean(args[1]);
				}
			}
			Explosion explosion = world.newExplosion((Entity) null, x+0.5, y, z+0.5, power, fire, true);
		}
		
		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}
		
		private static String usage = "/explode [power] [fire] Explode yourself with the set amount of power, power should be a number and defaults to 4, fire should be a boolean (true/false) and defaults to false.";

	}

}

