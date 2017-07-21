package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
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

	public static class CommandspawnClientEntity implements ICommand {
		public boolean isUsernameIndex(int sender) {
			return false;
		}

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("sce");
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			if (args.length == 1) {
				return CommandBase.getListOfStringsMatchingLastWord(args, EntityList.getEntityNameList());
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
				Reference.sendCommandUsage(sender, usage);
			} else {
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
					Reference.sendMessage(sender, "Successfully spawned a ghost entity of type " + args[0] + ".");
				}
			}

		}
		
		protected String usage = "/spawncliententity <entity> Spawns an entity on the client side that only the player can see.";

		@Override
		public int compareTo(ICommand arg0) {
			return 0;
		}

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			return true;
		}

	}

}