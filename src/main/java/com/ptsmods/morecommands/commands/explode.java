package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class explode {
	
	public static Object instance;

	public explode() {
	}

	public static class Commandexplode extends CommandBase {
		public boolean isUsernameIndex(int var1) {
			return false;
		}

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

		public String getUsage(ICommandSender var1) {
			return "/explode [power] Explode yourself with the set amount of power, power should be a number.";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender var1, String[] args) {
			int x = var1.getPosition().getX();
			int y = var1.getPosition().getY();
			int z = var1.getPosition().getZ();
			EntityPlayer entity = (EntityPlayer) var1;
			Float power = 4F;
			
			World world = null;
			WorldServer[] list = server.worlds;
			for (WorldServer ins : list) {
				if (ins.provider.getDimension() == entity.world.provider.getDimension())
					world = ins;
			}

			if (world == null)
				world = list[0];
			
			if (args.length != 0) {
				if (Reference.isInteger(args[0])) {
					power = (float) Integer.parseInt(args[0]);
				} else {
					var1.sendMessage(new TextComponentString("Power should be a number."));
					return;
				}
			}
			world.createExplosion((Entity) null, x+0.5, y, z+0.5, power, true);
		}

	}

}
