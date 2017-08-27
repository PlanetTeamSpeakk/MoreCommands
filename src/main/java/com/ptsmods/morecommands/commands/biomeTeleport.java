package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class biomeTeleport {

	public biomeTeleport() {
	}

	public static class CommandbiomeTeleport extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public int getRequiredPermissionLevel() {
			return 2;
		}

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("bpt");
			aliases.add("btp");
			aliases.add("biometp");
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			if (args.length == 1) return getListOfStringsMatchingLastWord(args, Reference.getBiomeNames());
			else return new ArrayList();
		}

		@Override
		public String getName() {
			return "biometeleport";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			EntityPlayer player = (EntityPlayer) sender;
			if (args.length == 0) Reference.sendCommandUsage(sender, usage);
			else if (Reference.getBiomeByName(Reference.join(args).replaceAll("_", " ")) == null) Reference.sendMessage(sender, "The given biome does not exist.");
			else {
				Reference.sendMessage(sender, "Looking for the requested biome...");
				Biome biome = Reference.getBiomeByName(Reference.join(args).replaceAll("_", " "));
				boolean found = false;
				int tries = 0;
				double x;
				double z;
				while (!found && tries <= 8192) {
					tries += 1;
					x = Reference.Random.randDouble(player.getEntityWorld().getWorldBorder().maxX());
					z = Reference.Random.randDouble(player.getEntityWorld().getWorldBorder().maxZ());
					if (player.getEntityWorld().getBiome(new BlockPos(x, 0, z)) == biome) {
						found = true;
						player.setPositionAndUpdate(x, 0, z);
						Reference.teleportSafely(player);
						Reference.sendMessage(player, "You have been teleported to a " + biome.getBiomeName() + " biome. Your new coords are \nX: " + player.getPosition().getX() + ", Y: " + player.getPosition().getY() + ", Z: " + player.getPosition().getZ() +
								"\nChunk: X: " + player.chunkCoordX + ", Y: " + player.chunkCoordY + ", Z: " + player.chunkCoordZ + "\nBiome: " + player.getEntityWorld().getBiome(player.getPosition()).getBiomeName() +
								"\nWorld: " + player.getEntityWorld().getWorldInfo().getWorldName() + ".");
						return;
					}
				}
				Reference.sendMessage(sender, "The given biome could not be found in the world, maybe give it another try.");
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			return super.checkPermission(server, sender);
			//return true;
		}

		@Override
		public boolean hasCooldown() {
			return true;
		}

		@Override
		public int getCooldownSeconds() {
			return 10;
		}

		@Override
		public boolean singleplayerOnly() {
			return true;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "btp", "Permission to use the biometeleport command.", true);
		}

		protected String usage = "/biometeleport <biome> Teleports you to a biome.";

	}

}