package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.IReach;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.ReachProvider;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class reach {

	public reach() {}

	public static class Commandreach extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("br");
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "reach";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			// You know that feeling that you think something isn't a feature in a game
			// while you want it to be, so you feel obligated, as a modder, to add it and
			// later find out it actually is a feature?
			// Well, I am having that rn.
			// It wasn't totally for nothing though, with vanilla reach it only works for
			// blocks. Plus, vanilla reach is only a thing on 1.12+.
			EntityPlayerMP player = getCommandSenderAsPlayer(sender);
			IAttributeInstance vanillaReach = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE);
			IReach modReach = player.getCapability(ReachProvider.reachCap, null);
			if (args.length == 0) Reference.sendMessage(sender, "Your current reach is", (vanillaReach.getAttributeValue() == modReach.get() ? modReach.get() : "unsynchronized, please reset it") + ".");
			else if (Reference.isFloat(args[0])) {
				float old = modReach.get();
				vanillaReach.setBaseValue(Double.parseDouble(args[0]));
				modReach.set(player, Float.parseFloat(args[0]));
				Reference.sendMessage(sender, "Your reach has been set from", old, "to", Float.parseFloat(args[0]) + ".");
			} else Reference.sendMessage(sender, "The given argument was not a float.");
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "reach", "Allows you set or get your reach.", true);
		}

		private String usage = "/reach [float] Sets your reach to the given float, if none given, shows current reach instead. Default is 5.";

	}

}