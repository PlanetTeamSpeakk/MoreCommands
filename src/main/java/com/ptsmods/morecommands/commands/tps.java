package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;
import com.ptsmods.morecommands.miscellaneous.Ticker;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class tps {

	public tps() {}

	public static class Commandtps extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("tickspersecond");
			aliases.add("tickspersec");
			aliases.add("ticks");
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "tps";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			Reference.sendMessage(sender, "Current:", TextFormatting.YELLOW, Ticker.INSTANCE.tps + Reference.dtf + ", 5 minutes:", TextFormatting.YELLOW, Ticker.INSTANCE.tps5 + Reference.dtf + ", 15 minutes:", TextFormatting.YELLOW, Ticker.INSTANCE.tps15);
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "tps", "Get the ticks per second of the server, used to indicate lag.", true);
		}

		private String usage = "/tps Get the ticks per second of the server, used to indicate lag.";

	}

}