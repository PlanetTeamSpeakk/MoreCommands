package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class wild {

	public wild() {
	}

	public static class Commandwild extends com.ptsmods.morecommands.miscellaneous.CommandBase {

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
			return "wild";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			EntityPlayer player = (EntityPlayer) sender;
			player.setPosition(Reference.Random.randDouble(100000), 0, Reference.Random.randDouble(100000));
			Reference.teleportSafely(player);
			Block blockIn = player.getEntityWorld().getBlockState(new BlockPos(player.getPosition().getX(), player.getPosition().getY()-1, player.getPosition().getZ())).getBlock();
			while (blockIn == Blocks.WATER) {
				player.setPosition(Reference.Random.randDouble(100000), 0, Reference.Random.randDouble(100000));
				Reference.teleportSafely(player);
				blockIn = player.getEntityWorld().getBlockState(new BlockPos(player.getPosition().getX(), player.getPosition().getY()-1, player.getPosition().getZ())).getBlock();
			}
			Reference.sendMessage(player, "You have been randomly teleported, your new coords are\nX: " + player.getPosition().getX() + ", Y: " + player.getPosition().getY() + ", Z: " + player.getPosition().getZ() +
					", Biome: " + player.getEntityWorld().getBiome(player.getPosition()).getBiomeName() + ".");

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