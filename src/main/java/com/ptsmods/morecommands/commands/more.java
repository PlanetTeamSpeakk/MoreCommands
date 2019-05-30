package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

public class more {

	public more() {}

	public static class Commandmore extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("mroe");
			aliases.add("moar");
			aliases.add("stock");
			aliases.add("restock");
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "moar";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			EntityPlayer player = getCommandSenderAsPlayer(sender);
			player.getHeldItemMainhand().setCount(player.getHeldItemMainhand().getMaxStackSize());
			player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "more", "Restocks your itemstack to the max value.", true);
		}

		private String usage = "/more Restocks your itemstack to the max value.";

	}

}