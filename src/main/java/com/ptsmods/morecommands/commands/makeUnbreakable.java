package com.ptsmods.morecommands.commands;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class makeUnbreakable {

	public makeUnbreakable() {}

	public static class CommandmakeUnbreakable extends com.ptsmods.morecommands.miscellaneous.CommandBase {

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
			if (args.length == 1) return getListOfStringsMatchingLastWord(args, Block.REGISTRY.getKeys());
			else return new ArrayList();
		}

		@Override
		public String getName() {
			return "makeunbreakable";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length != 0) if (!args[0].equals("*")) try {
				init();
				Block block = getBlockByText(sender, args[0]);
				if (!messedUpBlocks.contains(block)) {
					block.setBlockUnbreakable();
					block.setResistance(Float.MAX_VALUE);
					removeFlammabilityAndEncouragement(block);
					messedUpBlocks.add(block);
					Reference.sendMessage(sender, Reference.getLocalizedName(block) + " has been made unbreakable.");
				} else {
					block.setHardness(origHardness.get(block));
					block.setResistance(origResistance.get(block) / 3F);
					addFlammabilityAndEncouragement(block, false, 0, 0);
					messedUpBlocks.remove(block);
					Reference.sendMessage(sender, Reference.getLocalizedName(block) + " has been made breakable. (kinda)");
				}
			} catch (NumberInvalidException e) {
				Reference.sendMessage(sender, "The given block could not be found.");
				return;
			}
			else {
				for (ResourceLocation blockName : Block.REGISTRY.getKeys()) {
					Block.REGISTRY.getObject(blockName).setBlockUnbreakable();
					Block.REGISTRY.getObject(blockName).setResistance(Float.MAX_VALUE);
					removeFlammabilityAndEncouragement(Block.REGISTRY.getObject(blockName));
				}
				Reference.sendMessage(sender, "Every block is now unbreakable, glhf.");
			}
			else Reference.sendCommandUsage(sender, usage);
		}

		private volatile boolean	initialised	= false;
		private List<Block>			messedUpBlocks;
		private Map<Block, Float>	origHardness;
		private Map<Block, Float>	origResistance;
		private Map<Block, Integer>	origFlammabilities;
		private Map<Block, Integer>	origEncouragements;
		private Map<Block, Integer>	flammabilities;
		private Map<Block, Integer>	encouragements;

		private void removeFlammabilityAndEncouragement(Block block) {
			init();
			flammabilities.remove(block);
			encouragements.remove(block);

		}

		private void addFlammabilityAndEncouragement(Block block, boolean force, int forceFlammability, int forceEncouragement) {
			init();
			if (force || origFlammabilities.containsKey(block) && !flammabilities.containsKey(block)) flammabilities.put(block, force ? forceFlammability : origFlammabilities.get(block));
			if (force || origEncouragements.containsKey(block) && !encouragements.containsKey(block)) encouragements.put(block, force ? forceEncouragement : origEncouragements.get(block));
		}

		private void init() {
			if (!initialised) {
				messedUpBlocks = new ArrayList();
				origHardness = new HashMap();
				origResistance = new HashMap();
				try {
					for (ResourceLocation blockName : Block.REGISTRY.getKeys()) {
						Block block = Block.REGISTRY.getObject(blockName);
						origHardness.put(block, block.getDefaultState().getBlockHardness(null, BlockPos.ORIGIN));
						origResistance.put(block, block.getExplosionResistance(null, BlockPos.ORIGIN, null, null) * 5);
					}
					origHardness = Collections.unmodifiableMap(origHardness);
					origResistance = Collections.unmodifiableMap(origResistance);
					Field f = Reference.fieldExists(BlockFire.class, "flammabilities") ? BlockFire.class.getDeclaredField("flammabilities") : BlockFire.class.getDeclaredField("field_149848_b");
					f.setAccessible(true);
					flammabilities = (Map<Block, Integer>) f.get(Blocks.FIRE);
					origFlammabilities = Collections.unmodifiableMap(flammabilities);
					f = Reference.fieldExists(BlockFire.class, "encouragements") ? BlockFire.class.getDeclaredField("encouragements") : BlockFire.class.getDeclaredField("field_149849_a");
					f.setAccessible(true);
					encouragements = (Map<Block, Integer>) f.get(Blocks.FIRE);
					origEncouragements = Collections.unmodifiableMap(encouragements);
					initialised = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "makeunbreakable", "Permission to use the makeunbreakable command.", true);
		}

		protected String usage = "/makeunbreakable <block> Makes the given block unbreakable, " + TextFormatting.DARK_RED + TextFormatting.BOLD + "USE WITH CAUTION!";

	}

}