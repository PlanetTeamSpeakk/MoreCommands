package com.ptsmods.morecommands.commands;

import java.util.ArrayList;
import java.util.List;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class smite {

	public smite() {}

	public static class Commandsmite extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public int getRequiredPermissionLevel() {
			return 2;
		}

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			if (args.length == 1) return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
			else return new ArrayList();
		}

		@Override
		public String getName() {
			return "smite";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length == 0) {
				RayTraceResult result = Reference.rayTrace(getCommandSenderAsPlayer(sender), 160F);
				BlockPos pos = result.getBlockPos() == null && result.entityHit != null ? result.entityHit.getPosition() : result.getBlockPos();
				getCommandSenderAsPlayer(sender).getEntityWorld().addWeatherEffect(new EntityLightningBolt(getCommandSenderAsPlayer(sender).getEntityWorld(), pos.getX(), pos.getY(), pos.getZ(), false));
			} else try {
				List<EntityPlayer> victims = new ArrayList();
				if (args[0].startsWith("@")) victims.addAll(EntitySelector.matchEntities(sender, args[0], EntityPlayer.class));
				else victims.add(getPlayer(server, sender, args[0]));
				World world = sender.getEntityWorld();
				List<String> names = new ArrayList();
				for (EntityPlayer victim : victims) {
					names.add(victim.getName());
					for (int x = 0; x < 3; x += 1)
						world.addWeatherEffect(new EntityLightningBolt(world, victim.getPosition().getX(), victim.getPosition().getY(), victim.getPosition().getZ(), false));
				}
				Reference.sendMessage(sender, joinNiceStringFromCollection(names) + " ha" + (victims.size() > 1 ? "ve" : "s") + " been smitten.");
			} catch (PlayerNotFoundException e) {
				Reference.sendMessage(sender, "The given player does not exist.");
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "smite", "Strike stuff with lightning.", true);
		}

		protected String usage = "/smite [player] Strikes someone with lightning. If no player is giving, strikes wherever you're looking.";

	}

}