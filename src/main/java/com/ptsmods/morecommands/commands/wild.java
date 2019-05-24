package com.ptsmods.morecommands.commands;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.GameRuleWildLimit;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.block.Block;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class wild {

	public wild() {}

	public static class Commandwild extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("rtp");
			aliases.add("randomtp");
			aliases.add("randomteleport");
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "wild";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			EntityPlayer player = (EntityPlayer) sender;
			int limit = GameRuleWildLimit.instance.getValue(sender.getEntityWorld());
			Block blockIn = Blocks.WATER; // Less code as there would otherwise be the exact same code here as in the
											// while loop.
			while (blockIn == Blocks.WATER) {
				player.setPosition(Reference.Random.randDouble(-limit, limit), 0, Reference.Random.randDouble(-limit, limit));
				Reference.teleportSafely(player);
				blockIn = player.getEntityWorld().getBlockState(new BlockPos(player.getPosition().getX(), player.getPosition().getY() - 1, player.getPosition().getZ())).getBlock();
			}
			String biomeName = "unknown";
			try {
				Field f = Biome.class.getDeclaredField("biomeName");
				f.setAccessible(true);
				biomeName = (String) f.get(player.getEntityWorld().getBiome(player.getPosition()));
			} catch (Exception e) {
				e.printStackTrace();
				biomeName = "ERRORED";
			}
			Reference.sendMessage(player, "You have been randomly teleported, your new coords are\nX: " + player.getPosition().getX() + ", Y: " + player.getPosition().getY() + ", Z: " + player.getPosition().getZ() + ", Biome: " + biomeName + ".");
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "wild", "Permission to use the wild command.", true);
		}

		protected String usage = "/wild Teleports you somewhere random.";

		@Override
		public boolean hasCooldown() {
			return true;
		}

		@Override
		public int getCooldownSeconds() {
			return 10;
		}

	}

}