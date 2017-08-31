package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class findEntity {

	public findEntity() {
	}

	public static class CommandfindEntity extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			if (args.length == 1) return getListOfStringsMatchingLastWord(args, EntityList.getEntityNameList());
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "findentity";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length != 0) {
				if (!args[0].contains(":")) args[0] = "minecraft:" + args[0];
				if (EntityList.getEntityNameList().contains(new ResourceLocation(args[0]))) {
					String output = "";
					for (Entity entity : EntitySelector.matchEntities(sender, "@e[type="+args[0]+"]", Entity.class))
						output += "Found " + entity.getName() + " at X: " + entity.getPosition().getX() + ", Y: " + entity.getPosition().getY() + ", Z: " + entity.getPosition().getZ() + "\n";
					if (!output.equals("")) Reference.sendMessage(sender, output.trim());
					else Reference.sendMessage(sender, "Did not find any entities of type " + args[0] + ".");
				} else Reference.sendMessage(sender, "The given entity does not exist.");
			}

		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "findentity", "Permission to use the findentity command.", true);
		}

		private String usage = "/findentity <entity> Shows you a list of all loaded entities of the given type in the world with their coords.";

	}

}