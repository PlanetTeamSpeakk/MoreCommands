package com.ptsmods.morecommands.commands;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class killAll {

	public killAll() {}

	public static class CommandkillAll extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		public boolean isUsernameIndex(int sender) {
			return false;
		}

		@Override
		public int getRequiredPermissionLevel() {
			return 2;
		}

		@Override
		public java.util.List getAliases() {
			return new ArrayList();
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			if (args.length == 1) {
				List list = getListOfStringsMatchingLastWord(args, EntityList.getEntityNameList());
				list.addAll(getListOfStringsMatchingLastWord(args, Lists.newArrayList("monster", "creature", "animal")));
				return list;
			} else return new ArrayList();
		}

		@Override
		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		@Override
		public String getName() {
			return "killall";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length == 1) {
				int counter = 0;
				if (args[0].equals("all")) args[0] = "!player";
				Class<? extends Entity> entityClass = Entity.class;
				if (args[0].startsWith("monster")) {
					entityClass = EntityMob.class;
					args[0] = "!player";
				} else if (args[0].startsWith("animal")) {
					entityClass = EntityAnimal.class;
					args[0] = "!player";
				} else if (args[0].startsWith("creature")) {
					entityClass = EntityCreature.class;
					args[0] = "!player";
				}
				List<Entity> entities = EntitySelector.matchEntities(sender, args[0].startsWith("@") ? args[0] : "@e[type=" + args[0] + "]", entityClass);
				for (Entity entity : entities) {
					entity.setPositionAndUpdate(entity.posX, -128, entity.posZ);
					entity.onKillCommand();
					counter += 1;
				}
				Reference.sendMessage(sender, "Successfully killed all entities " + (args[0].startsWith("@") ? "matching with the token " : "of type ") + (entityClass != Entity.class ? entityClass == EntityMob.class ? "monster" : entityClass == EntityAnimal.class ? "animal" : entityClass == EntityCreature.class ? "creature" : args[0] : args[0]) + ", killing a total of " + counter + " entities.");
			} else Reference.sendCommandUsage(sender, usage);
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "killall", "Permission to use the killall command.", true);
		}

		protected String usage = "/killall <entity> Kills all of the given entity in the world.";

	}

}