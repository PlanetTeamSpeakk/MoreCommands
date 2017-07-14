package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;

public class spawnClientEntity {

	public static Object instance;

	public spawnClientEntity() {
	}

	public static class CommandspawnClientEntity extends CommandBase {
		public boolean isUsernameIndex(int sender) {
			return false;
		}

	    public int getRequiredPermissionLevel() {
	        return 2;
	    }

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("sce");
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			if (args.length == 1) {
				return getListOfStringsMatchingLastWord(args, EntityList.getEntityNameList());
			} else if (args.length == 2) {
				return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
			} else {
				return new ArrayList();
			}
		}

		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		public String getName() {
			return "spawncliententity";
		}

		public String getUsage(ICommandSender sender) {
			return this.usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length == 0) {
				sender.sendMessage(new TextComponentString(Reference.RED + "Usage: " + this.usage));
			} else if (args.length == 1 || args.length == 3) {
				EntityPlayer player = (EntityPlayer) sender;
				World world = Minecraft.getMinecraft().world;
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setString("id", args[0]);
				double d0 = player.getPosition().getX() + 0.5;
				double d1 = player.getPosition().getY();
				double d2 = player.getPosition().getZ() + 0.5;
				Entity entity = AnvilChunkLoader.readWorldEntityPos(nbt, world, d0, d1, d2, true);
				entity.setLocationAndAngles(d0, d1, d2, entity.rotationYaw, entity.rotationPitch);
				if (args.length != 3) {
					sender.sendMessage(new TextComponentString("Successfully spawned a ghost entity of type " + args[0] + "."));
				}
			} else if (args.length == 2) {
				EntityPlayer victim;
				try {
					victim = getPlayer(server, sender, args[1]);
					server.getCommandManager().executeCommand(victim, "sce " + args[0] + " " + victim.getName() + " true"); // added true so there are 3 arguments and the victim doesn't get a message that a ghost entity has been spawned
					sender.sendMessage(new TextComponentString("Successfully spawned a ghost enitity of type " + args[0] + " as " + victim.getName() + "."));
				} catch (PlayerNotFoundException e) {
					sender.sendMessage(new TextComponentString("The given player does not exist."));
				}
			}

		}
		
		protected String usage = "/spawncliententity <entity> [player] Spawns an entity on the client side that only the player can see.";

	}

}