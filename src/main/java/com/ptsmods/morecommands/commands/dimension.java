package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class dimension {

	public dimension() {}

	// TODO make inspect command

	public static class Commanddimension extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("dim");
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "dimension";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length == 0 || !Reference.isInteger(args[0]) && !args[0].equals("list")) Reference.sendCommandUsage(sender, usage);
			else if (Reference.isInteger(args[0])) {
				int index = Integer.parseInt(args[0]);
				for (WorldServer world : server.worlds)
					if (world.provider.getDimension() == index) {
						if (sender instanceof EntityPlayerMP) server.getPlayerList().transferPlayerToDimension((EntityPlayerMP) sender, index, new Teleporter(world) {
							@Override
							public void placeInPortal(Entity entityIn, float rotationYaw) {}
						});
						else if (sender instanceof Entity) server.getPlayerList().transferEntityToWorld((Entity) sender, sender.getEntityWorld().provider.getDimension(), (WorldServer) sender.getEntityWorld(), world, new Teleporter(world) {
							@Override
							public void placeInPortal(Entity entityIn, float rotationYaw) {}
						});
						else Reference.sendMessage(sender, TextFormatting.RED + "Only entities may use this command.");
						return;
					}
				Reference.sendMessage(sender, TextFormatting.RED + "A world with an id of", index, "could not be found. Is it not yet loaded?");
			} else if (args[0].equals("list")) {
				String list = "This " + (Reference.isSingleplayer() ? "singleplayer " : "") + "server has the following dimensions:";
				for (WorldServer world : server.worlds)
					list += "\n" + world.getWorldInfo().getWorldName() + ": " + world.provider.getDimension();
				Reference.sendMessage(sender, list);
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "dimension", "Teleport to another dimension.", true);
		}

		private String usage = "/dimension <<index>|list> Either teleport to a different dimension or list all dimensions.";

	}

}