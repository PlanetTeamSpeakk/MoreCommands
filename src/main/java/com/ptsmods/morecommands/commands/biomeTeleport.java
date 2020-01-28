package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class biomeTeleport {

	public biomeTeleport() {}

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
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			EntityPlayer player = getCommandSenderAsPlayer(sender);
			if (args.length == 0) Reference.sendCommandUsage(sender, usage);
			else if (Reference.getBiomeByName(Reference.join(args).replaceAll("_", " ")) == null) Reference.sendMessage(sender, "The given biome does not exist.");
			else {
				Reference.sendMessage(sender, "Looking for the requested biome...");
				Reference.execute(() -> { // Don't wanna freeze the server for like up to 7 seconds.
					Biome biome = Reference.getBiomeByName(Reference.join(args).replaceAll("_", " "));
					long begin = System.nanoTime();
					float time = 0;
					int attempts = 0;
					double x;
					double z;
					while (attempts <= 1 << 15) { // 1 << 15 = 32767
						long start = System.nanoTime();
						attempts += 1;
						x = Reference.Random.randDouble(player.getEntityWorld().getWorldBorder().minX(), player.getEntityWorld().getWorldBorder().maxX());
						z = Reference.Random.randDouble(player.getEntityWorld().getWorldBorder().minZ(), player.getEntityWorld().getWorldBorder().maxZ());
						final double sX = x;
						final double sZ = z;
						boolean flag = false;
						flag = player.getEntityWorld().getBiome(new BlockPos(sX, 0, sZ)) == biome;
						long nanoTaken = System.nanoTime() - start;
						time = time == 0 ? nanoTaken : (time + nanoTaken) / 2F;
						if (flag) {
							int attempts0 = attempts;
							float time0 = time;
							long totalTime = System.nanoTime() - begin;
							server.addScheduledTask(() -> {
								player.setPositionAndUpdate(sX, 0, sZ);
								Reference.teleportSafely(player);
								Reference.sendMessage(player, "After " + attempts0 + " attempt" + (attempts0 == 1 ? "" : "s") + " taking an average of " + time0 / 1000000F + " ms per attempt and a total of " + totalTime / 1000000F + " ms, you have been teleported to a " + Reference.getBiomeName(biome) + " biome. Your new coords are \nX: " + player.getPosition().getX() + ", Y: " + player.getPosition().getY() + ", Z: " + player.getPosition().getZ() + "\nChunk: X: " + player.chunkCoordX + ", Y: " + player.chunkCoordY + ", Z: " + player.chunkCoordZ + "\nWorld: " + player.getEntityWorld().getWorldInfo().getWorldName() + ".");
							});
							return;
						}
					}
					Reference.sendMessage(sender, "The given biome could not be found in the world.");
				});
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
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
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "btp", "Permission to use the biometeleport command.", true);
		}

		protected String usage = "/biometeleport <biome> Teleports you to a biome.";

	}

}