package com.ptsmods.morecommands.commands;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;
import com.ptsmods.morecommands.miscellaneous.Reference.Random;

import net.minecraft.block.BlockChest;
import net.minecraft.block.properties.IProperty;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.storage.loot.LootTableList;

public class genloot {

	public genloot() {}

	public static class Commandgenloot extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("lootgen");
			aliases.add("generateloot");
			aliases.add("loottable");
			aliases.add("loot");
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			switch (args.length) {
			case 1:
				return getListOfStringsMatchingLastWord(args, LootTableList.getAll().stream().collect(Collectors.toList()));
			case 2:
			case 3:
			case 4:
				return Lists.newArrayList("~");
			}
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "genloot";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws NumberInvalidException {
			if (args.length == 0) Reference.sendCommandUsage(sender, usage);
			else {
				ResourceLocation loc = new ResourceLocation(args[0]);
				boolean found = false;
				for (ResourceLocation table : LootTableList.getAll())
					if (table.equals(loc)) {
						loc = table;
						found = true;
						break;
					}
				if (!found) Reference.sendMessage(sender, TextFormatting.RED + "The given loottable could not be found.");
				else {
					int x = sender.getPosition().getX();
					int y = sender.getPosition().getY();
					int z = sender.getPosition().getZ();
					if (args.length >= 4 && Reference.isCoordsDouble(args[1]) && Reference.isCoordsDouble(args[2]) && Reference.isCoordsDouble(args[3])) {
						x = (int) parseDouble(x, args[1], false);
						y = (int) parseDouble(y, args[2], false);
						z = (int) parseDouble(z, args[3], false);
					}
					EnumFacing facing = EnumFacing.SOUTH;
					if (sender instanceof Entity) facing = ((Entity) sender).getAdjustedHorizontalFacing();
					else if (sender.getEntityWorld().getTileEntity(sender.getPosition()) != null) for (IProperty<?> prop : sender.getEntityWorld().getBlockState(sender.getEntityWorld().getTileEntity(sender.getPosition()).getPos()).getPropertyKeys())
						if (((ParameterizedType) prop.getClass().getGenericSuperclass()).getActualTypeArguments()[0] == EnumFacing.class) facing = sender.getEntityWorld().getBlockState(sender.getEntityWorld().getTileEntity(sender.getPosition()).getPos()).getValue((IProperty<EnumFacing>) prop);
					sender.getEntityWorld().setBlockState(new BlockPos(x, y, z), Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, facing == EnumFacing.UP ? EnumFacing.NORTH : facing == EnumFacing.DOWN ? EnumFacing.SOUTH : facing));
					((TileEntityChest) sender.getEntityWorld().getTileEntity(new BlockPos(x, y, z))).setLootTable(loc, Random.randLong(Long.MIN_VALUE, Long.MAX_VALUE));
					Reference.sendMessage(sender, "The loot chest has been created.");
				}
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "genloot", "Generate a chest with loot in it.", true);
		}

		private String usage = "/genloot <loot_table> [x] [y] [z] Generate a chest with loot from the given loot table at either your feet or the given location.";

	}

}