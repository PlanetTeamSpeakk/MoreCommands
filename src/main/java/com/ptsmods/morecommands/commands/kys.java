package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;

public class kys {

	public kys() {}

	public static class Commandkys extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		public static final DamageSource SUICIDE = new DamageSource("suicide").setDamageAllowedInCreativeMode();

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("suicide");
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "kys";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return "/kys Commit suicide.";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (sender instanceof Entity) {
				Reference.sendMessage(sender, "Goodbye cruel world.");
				((Entity) sender).attackEntityFrom(SUICIDE, Float.MAX_VALUE);
			}

		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "kys", "Commit suicide.", true);
		}

	}

}