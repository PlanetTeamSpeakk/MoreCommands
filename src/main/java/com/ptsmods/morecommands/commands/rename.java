package com.ptsmods.morecommands.commands;

import java.util.ArrayList;
import java.util.Set;

import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class rename {

	public rename() {
	}

	public static class Commandrename extends CommandBase {

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
			return "rename";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length != 0) {
				EntityPlayer player = (EntityPlayer) sender;
				String name = "";
				for (int x = 0; x < args.length; x += 1) {
					name += args[x];
					if ((x + 1) != args.length) {
						name += " ";
					}
				}
				name = name.replaceAll("&", "§");
				player.getHeldItemMainhand().setStackDisplayName(name);
				Reference.sendMessage(player, "Your " + player.getHeldItemMainhand().getItem().getRegistryName().toString().split(":")[1] + " has been renamed to " + name + TextFormatting.RESET + ".");
			} else {
				Reference.sendCommandUsage(sender, usage);
			}

		}
		
		protected String usage = "/rename <name> Renames the item you're holding, you can use color codes like &1, &2, &3, etc.";

	}

}