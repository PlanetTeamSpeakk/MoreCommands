package com.ptsmods.morecommands.commands;

import java.util.ArrayList;
import java.util.List;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class defuse {

	public defuse() {}

	public static class Commanddefuse extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "defuse";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length == 0 || !Reference.isInteger(args[0]) && !args[0].equals("all")) Reference.sendCommandUsage(sender, usage);
			else {
				List<EntityTNTPrimed> tnt = new ArrayList();
				if (args[0].equals("all")) tnt = EntitySelector.matchEntities(sender, "@e[type=minecraft:tnt]", EntityTNTPrimed.class);
				else tnt = EntitySelector.matchEntities(sender, "@e[type=minecraft:tnt,r=" + Integer.parseInt(args[0]) + "]", EntityTNTPrimed.class);
				for (EntityTNTPrimed tnt0 : tnt) {
					tnt0.setDead();
					sender.getEntityWorld().spawnEntity(new EntityItem(sender.getEntityWorld(), tnt0.posX, tnt0.posY, tnt0.posZ, new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("minecraft:tnt")), 1)));
				}
				Reference.sendMessage(sender, "Successfully defused " + TextFormatting.GRAY + TextFormatting.ITALIC + tnt.size() + Reference.dtf + " primed TNT entit" + (tnt.size() == 1 ? "y" : "ies") + ".");
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "defuse", "Defuse any primed TNT within range or all of it.", true);
		}

		private String usage = "/defuse <{range}:all> Defuse any primed TNT within either the given range or all of it and spawn a TNT item in its place. Range should be an integer and must be set.";

	}

}