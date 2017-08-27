package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class explode {

	public explode() {
	}

	public static class Commandexplode extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public int getRequiredPermissionLevel() {
			return 2;
		}

		@Override
		public java.util.List getAliases() {
			return new ArrayList();
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
			return "explode";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			double x = sender.getPositionVector().x;
			double y = sender.getPositionVector().y;
			double z = sender.getPositionVector().z;
			Float power = 4F;
			World world = sender.getEntityWorld();
			Boolean fire = false;

			if (args.length != 0) {
				if (Reference.isInteger(args[0]))
					power = Float.parseFloat(args[0]);
				else {
					Reference.sendCommandUsage(sender, usage);
					return;
				}
				if (args.length == 2 && Reference.isBoolean(args[1]))
					fire = Boolean.parseBoolean(args[1]);
			}
			world.newExplosion((Entity) null, x, y, z, power, fire, true);
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "explode", "Permission to use the explode command.", true);
		}

		private static String usage = "/explode [power] [fire] Explode yourself with the set amount of power, power should be a number and defaults to 4, fire should be a boolean (true/false) and defaults to false.";

	}

}

