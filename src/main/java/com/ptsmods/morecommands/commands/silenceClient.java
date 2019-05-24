package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class silenceClient {

	public silenceClient() {}

	public static class CommandsilenceClient extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("cquiet");
			aliases.add("cs");
			aliases.add("cq");
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "csilence";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length != 0 && args[0].equals("get") && sender == Minecraft.getMinecraft().player) sender.sendMessage(new TextComponentString(Reference.dtf + "You have " + (Reference.silencedClient ? "" : "not ") + "silenced MoreCommands."));
			else if (sender == Minecraft.getMinecraft().player) Reference.silencedClient = !Reference.silencedClient;
			else Reference.sendMessage(sender, "This command may only be used by the player.");
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.CLIENT;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "PERMISSION", "DESCRIPTION", false);
		}

		private String usage = "/csilence [get] Silences all messages you receive from MoreCommands commands or gets if you have silenced MoreCommands. To get if you have silenced MoreCommands, type /csilence get. Useful for macros.";

	}

}