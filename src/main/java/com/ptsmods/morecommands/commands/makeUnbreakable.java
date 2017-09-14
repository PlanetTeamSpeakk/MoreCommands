package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class makeUnbreakable {

	public makeUnbreakable() {
	}

	public static class CommandmakeUnbreakable extends com.ptsmods.morecommands.miscellaneous.CommandBase {

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
			if (args.length == 1)
				return getListOfStringsMatchingLastWord(args, Block.REGISTRY.getKeys());
			else return new ArrayList();
		}

		@Override
		public String getName() {
			return "makeunbreakable";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length != 0)
				if (!args[0].equals("*"))
					try {
						Block block = getBlockByText(sender, args[0]);
						block.setBlockUnbreakable();
						block.setResistance(Float.MAX_VALUE);
						Reference.sendMessage(sender, Reference.getLocalizedName(block) + " has been made unbreakable.");
					} catch (NumberInvalidException e) {
						Reference.sendMessage(sender, "The given block could not be found.");
						return;
					}
				else {
					for (ResourceLocation blockName : Block.REGISTRY.getKeys()) {
						Block.REGISTRY.getObject(blockName).setBlockUnbreakable();
						Block.REGISTRY.getObject(blockName).setResistance(Float.MAX_VALUE);
					}
					Reference.sendMessage(sender, "Every block is now unbreakable, glhf.");
				}
			else Reference.sendCommandUsage(sender, usage);

		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "makeunbreakable", "Permission to use the makeunbreakable command.", true);
		}

		protected String usage = "/makeunbreakable <block> Makes the given block unbreakable, " + TextFormatting.DARK_RED + TextFormatting.BOLD + "USE WITH CAUTION!";

	}

}