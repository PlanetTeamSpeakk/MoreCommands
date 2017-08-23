package com.ptsmods.morecommands.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.ptsmods.morecommands.miscellaneous.CommandType;

import net.minecraft.command.CommandHelp;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.ClientCommandHandler;

public class chelp {

	public chelp() {
	}

	public static class Commandchelp extends CommandHelp {

	    public int getRequiredPermissionLevel() {
	        return 0;
	    }

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("c?");
			return aliases;
		}
		
		@Override
		public String getUsage(ICommandSender sender) {
			return this.usage;
		}

		@Override
		public String getName() {
			return "chelp";
		}
		
		@Override
	    protected List<ICommand> getSortedPossibleCommands(ICommandSender sender, MinecraftServer server) {
	        List<ICommand> list = ClientCommandHandler.instance.getPossibleCommands(sender);
	        Collections.sort(list);
	        return list;
	    }
		
		@Override
	    protected Map<String, ICommand> getCommandMap(MinecraftServer server) {
	        return ClientCommandHandler.instance.getCommands();
	    }
		
		public CommandType getCommandType() {
			return CommandType.CLIENT;
		}
		
		private String usage = "/chelp [command] Just like the help command but for client sided commands.";

	}

}