package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.google.common.collect.Lists;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGeneratorEnd;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.structure.MapGenEndCity;
import net.minecraft.world.gen.structure.MapGenNetherBridge;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureOceanMonument;
import net.minecraft.world.gen.structure.WoodlandMansion;

public class structure {

	public structure() {}

	public static class Commandstructure extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("struc");
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			if (args.length == 1) return getListOfStringsMatchingLastWord(args, Lists.newArrayList("tp", "find"));
			else return getListOfStringsMatchingLastWord(args, Lists.newArrayList("village", "ocean_monument", "stronghold", "end_city", "fortress", "scattered", "mansion"));
		}

		@Override
		public String getName() {
			return "structure";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length > 1) {
				World worldIn = sender.getEntityWorld();
				BlockPos pos = sender.getPosition();
				boolean findUnexplored = true;
				boolean tp = false;
				BlockPos found = null;
				switch (args[0].toLowerCase()) {
				case "tp":
					tp = true;
				case "find":
					switch (args[1].toLowerCase()) {
					case "village":
						found = getIfDimension(new MapGenVillage().getNearestStructurePos(worldIn, pos, findUnexplored), worldIn, 0);
						break;
					case "ocean_monument":
						found = getIfDimension(new StructureOceanMonument().getNearestStructurePos(worldIn, pos, findUnexplored), worldIn, 0);
						break;
					case "stronghold":
						found = getIfDimension(new MapGenStronghold() {
							@Override
							public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored) {
								world = worldIn;
								return super.getNearestStructurePos(worldIn, pos, findUnexplored);
							}
						}.getNearestStructurePos(worldIn, pos, findUnexplored), worldIn, 0);
						break;
					case "end_city":
						found = getIfDimension(new MapGenEndCity(new ChunkGeneratorEnd(worldIn, true, worldIn.getSeed(), pos)).getNearestStructurePos(worldIn, pos, findUnexplored), worldIn, 1);
						break;
					case "scattered":
						found = getIfDimension(new MapGenScatteredFeature().getNearestStructurePos(worldIn, pos, findUnexplored), worldIn, 0);
						break;
					case "fortress":
						found = getIfDimension(new MapGenNetherBridge() {
							@Override
							public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored) {
								world = worldIn;
								return super.getNearestStructurePos(worldIn, pos, findUnexplored);
							}
						}.getNearestStructurePos(worldIn, pos, findUnexplored), worldIn, -1);
						break;
					case "mansion":
						found = getIfDimension(new WoodlandMansion(new ChunkGeneratorOverworld(worldIn, worldIn.getSeed(), true, "")).getNearestStructurePos(worldIn, pos, findUnexplored), worldIn, 0);
						break;
					}
					if (found == null) Reference.sendMessage(sender, TextFormatting.RED + "A structure of type", args[1], "could not be found in this dimension.");
					else {
						Reference.sendMessage(sender, "Found a structure of type", args[1], "at X: " + pos.getX() + ", Y: " + pos.getY() + ", Z: " + pos.getZ() + ".");
						if (tp) {
							EntityPlayer player = getCommandSenderAsPlayer(sender);
							player.setPositionAndUpdate(found.getX(), found.getY(), found.getZ());
							Reference.teleportSafely(player);
						}
					}
					break;
				default:
					Reference.sendCommandUsage(sender, usage);
				}
			} else Reference.sendCommandUsage(sender, usage);
		}

		private BlockPos getIfDimension(BlockPos pos, World worldIn, int dimension) {
			return dimension == worldIn.provider.getDimension() ? pos : null;
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "structure", "Allows the player to teleport to or find a vanilla Minecraft structure, be careful with to whom you give this permission.", true);
		}

		private String usage = "/structure <tp|find> <structure> Teleport to or find a vanilla Minecraft structure.";

	}

}