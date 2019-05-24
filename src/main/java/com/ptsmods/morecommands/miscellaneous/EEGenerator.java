package com.ptsmods.morecommands.miscellaneous;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.Template.BlockInfo;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class EEGenerator implements IWorldGenerator {

	// I must say, this class may have the lowest complexity to failure rate I've
	// ever made, it worked flawlessly on the first try.
	// Also props to Zerom69 for posting that thread on the MinecraftForge forum.
	// http://www.minecraftforge.net/forum/topic/59735-112-generated-structure-from-template-is-empty/
	private List<Vec3i> necessaryChunks = new ArrayList();

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		if (necessaryChunks.contains(new Vec3i(chunkX, 0, chunkZ))) return; // There is a VERY slim chance that due to the way chunks are loaded, there
																			// might be 2 or more easteregg structures overlapping each other.
		int x = chunkX * 16;
		int z = chunkZ * 16;
		if (world.provider.getDimensionType() == DimensionType.OVERWORLD && Reference.Random.randInt(100000) == 0) {
			int y = 255;
			boolean foundGround = false;
			while (!foundGround && y-- >= 63) {
				Block blockAt = world.getBlockState(new BlockPos(x, y, z)).getBlock();
				foundGround = blockAt == Blocks.DIRT || blockAt == Blocks.GRASS || blockAt == Blocks.SAND || blockAt == Blocks.SNOW || blockAt == Blocks.SNOW_LAYER || blockAt == Blocks.GLASS;
			}
			BlockPos pos = new BlockPos(x, y, z);
			if (y > 63 && isCornerValid(world, pos) && isCornerValid(world, pos.add(65, 0, 10))) {
				Rotation rot = Rotation.values()[Reference.Random.randInt(Rotation.values().length)];
				generateNecessaryChunks(chunkGenerator, chunkProvider, world, rot, chunkX, chunkZ);
				PlacementSettings placementsettings = new PlacementSettings().setMirror(Mirror.NONE).setRotation(rot).setIgnoreEntities(false).setChunk((ChunkPos) null).setReplacedBlock((Block) null).setIgnoreStructureBlock(true);
				for (int i = 0; i <= 7; i++)
					deleteAirBlocks(((WorldServer) world).getStructureTemplateManager().getTemplate(world.getMinecraftServer(), new ResourceLocation("morecommands:easteregg" + i))).addBlocksToWorld(world, getPosFor(i, x, y, z, rot), placementsettings);
			}
		}
	}

	// TODO: make sure this works, NONE seems to work fine, others, especially
	// CLOCKWISE_90, not so much.
	private void generateNecessaryChunks(IChunkGenerator generator, IChunkProvider provider, World world, Rotation rot, int x, int z) {
		switch (rot) {
		case NONE:
			necessaryChunks.add(new Vec3i(x + 1, 0, z));
			necessaryChunks.add(new Vec3i(x + 2, 0, z));
			necessaryChunks.add(new Vec3i(x + 3, 0, z));
			necessaryChunks.add(new Vec3i(x + 4, 0, z));
			necessaryChunks.add(new Vec3i(x + 5, 0, z));
			break;
		case CLOCKWISE_90:
			necessaryChunks.add(new Vec3i(x - 1, 0, z));
			necessaryChunks.add(new Vec3i(x - 1, 0, z + 1));
			necessaryChunks.add(new Vec3i(x - 1, 0, z + 2));
			necessaryChunks.add(new Vec3i(x - 1, 0, z + 3));
			necessaryChunks.add(new Vec3i(x - 1, 0, z + 4));
			break;
		case CLOCKWISE_180:
			necessaryChunks.add(new Vec3i(x - 2, 0, z));
			necessaryChunks.add(new Vec3i(x - 3, 0, z));
			necessaryChunks.add(new Vec3i(x, 0, z - 1));
			necessaryChunks.add(new Vec3i(x - 1, 0, z - 1));
			necessaryChunks.add(new Vec3i(x - 2, 0, z - 1));
			necessaryChunks.add(new Vec3i(x - 3, 0, z - 1));
			necessaryChunks.add(new Vec3i(x - 4, 0, z - 1));
			break;
		case COUNTERCLOCKWISE_90:
			necessaryChunks.add(new Vec3i(x, 0, z - 1));
			necessaryChunks.add(new Vec3i(x, 0, z - 2));
			necessaryChunks.add(new Vec3i(x, 0, z - 3));
			necessaryChunks.add(new Vec3i(x, 0, z - 4));
			necessaryChunks.add(new Vec3i(x, 0, z - 5));
			break;
		default:
			return;
		}
		for (Vec3i vec : necessaryChunks)
			GameRegistry.generateWorld(vec.getX(), vec.getZ(), world, generator, provider);
	}

	private Template deleteAirBlocks(Template template) {
		// At the time I forgot structure voids were a thing, even if I did remember, I
		// would've probably been too lazy to use them.
		Field f;
		try {
			f = Template.class.getDeclaredField("blocks");
			f.setAccessible(true);
			List<Template.BlockInfo> blocks = (List<BlockInfo>) f.get(template);
			for (Template.BlockInfo block : new ArrayList<>(blocks))
				if (block.blockState.getBlock() == Blocks.AIR) blocks.remove(block);
			f.set(template, blocks);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return template;
	}

	private BlockPos getPosFor(int i, int x, int y, int z, Rotation rot) {
		switch (rot) {
		case NONE:
			switch (i) {
			case 0:
				return new BlockPos(x, y, z);
			case 1:
				return new BlockPos(x + 32, y, z);
			case 2:
				return new BlockPos(x + 64, y, z);
			case 3:
				return new BlockPos(x, y + 32, z);
			case 4:
				return new BlockPos(x + 32, y + 32, z);
			case 5:
				return new BlockPos(x + 64, y + 32, z);
			case 6:
				return new BlockPos(x, y + 64, z);
			case 7:
				return new BlockPos(x + 32, y + 64, z);
			default:
				return null;
			}
		case CLOCKWISE_90:
			switch (i) {
			case 0:
				return new BlockPos(x, y, z);
			case 1:
				return new BlockPos(x, y, z + 32);
			case 2:
				return new BlockPos(x, y, z + 64);
			case 3:
				return new BlockPos(x, y + 32, z);
			case 4:
				return new BlockPos(x, y + 32, z + 32);
			case 5:
				return new BlockPos(x, y + 32, z + 64);
			case 6:
				return new BlockPos(x, y + 64, z);
			case 7:
				return new BlockPos(x, y + 64, z + 32);
			default:
				return null;
			}
		case CLOCKWISE_180:
			switch (i) {
			case 0:
				return new BlockPos(x, y, z);
			case 1:
				return new BlockPos(x - 32, y, z);
			case 2:
				return new BlockPos(x - 64, y, z);
			case 3:
				return new BlockPos(x, y + 32, z);
			case 4:
				return new BlockPos(x - 32, y + 32, z);
			case 5:
				return new BlockPos(x - 64, y + 32, z);
			case 6:
				return new BlockPos(x, y + 64, z);
			case 7:
				return new BlockPos(x - 32, y + 64, z);
			default:
				return null;
			}
		case COUNTERCLOCKWISE_90:
			switch (i) {
			case 0:
				return new BlockPos(x, y, z);
			case 1:
				return new BlockPos(x, y, z - 32);
			case 2:
				return new BlockPos(x, y, z - 64);
			case 3:
				return new BlockPos(x, y + 32, z);
			case 4:
				return new BlockPos(x, y + 32, z - 32);
			case 5:
				return new BlockPos(x, y + 32, z - 64);
			case 6:
				return new BlockPos(x, y + 64, z);
			case 7:
				return new BlockPos(x, y + 64, z - 32);
			default:
				return null;
			}
		default:
			return null;
		}
	}

	private boolean isCornerValid(World world, BlockPos pos) {
		int variation = 3;
		int y = 255;
		boolean foundGround = false;
		while (!foundGround && y-- >= 63) {
			Block blockAt = world.getBlockState(new BlockPos(pos.getX(), y, pos.getZ())).getBlock();
			foundGround = blockAt == Blocks.DIRT || blockAt == Blocks.GRASS || blockAt == Blocks.SAND || blockAt == Blocks.SNOW || blockAt == Blocks.SNOW_LAYER || blockAt == Blocks.GLASS;
		}
		return y > pos.getY() - variation && y < pos.getY() + variation;
	}

}
