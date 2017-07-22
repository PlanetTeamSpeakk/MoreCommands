package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class superPickaxe {

	public superPickaxe() {
	}

	public static class CommandsuperPickaxe extends CommandBase {

	    public int getRequiredPermissionLevel() {
	        return 2;
	    }

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public String getName() {
			return "superpickaxe";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}
		
		public static Boolean enabled = false;

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			enabled = !enabled;
			Reference.sendMessage(sender, "Superpickaxe has been " + (enabled == true ? "enabled, do note that using the pickaxe may crash your Minecraft due to some server ticking look randomly giving a ConcurrentModificationException." : "disabled."));

		}
		
		protected String usage = "/superpickaxe Enables superpickaxe.";

	}

}