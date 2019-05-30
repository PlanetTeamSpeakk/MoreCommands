package com.ptsmods.morecommands.commands;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import com.google.common.collect.Lists;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;
import com.ptsmods.morecommands.miscellaneous.Reference.Random;

import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenBirchTree;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import net.minecraft.world.gen.feature.WorldGenMegaJungle;
import net.minecraft.world.gen.feature.WorldGenMegaPineTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;

public class tree {

	public tree() {}

	public static class Commandtree extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return args.length == 1 ? getListOfStringsMatchingLastWord(args, Lists.newArrayList("oak", "birch", "spruce", "jungle", "acacia", "dark_oak")) : args.length == 2 ? Lists.newArrayList("true", "false") : new ArrayList();
		}

		@Override
		public String getName() {
			return "tree";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			RayTraceResult result = Reference.rayTrace(getCommandSenderAsPlayer(sender), 160);
			if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
				BlockPos pos = result.getBlockPos().add(0, 1, 0);
				boolean big = args.length >= 2 && Reference.isBoolean(args[1]) ? Boolean.parseBoolean(args[1]) : false;
				BlockPlanks.EnumType type = BlockPlanks.EnumType.OAK;
				if (args.length >= 1) for (BlockPlanks.EnumType type0 : BlockPlanks.EnumType.values())
					if (type0.getName().equalsIgnoreCase(args[0])) {
						type = type0;
						break;
					}
				WorldGenerator generator = null;
				switch (type) {
				case OAK:
					generator = big ? new WorldGenBigTree(true) {
						@Override
						public boolean generate(World worldIn, java.util.Random rand, BlockPos position) {
							try {
								// Lots of reflection, but everything is either private or package private.
								Class c = WorldGenBigTree.class;
								Field f = c.getDeclaredField("world");
								f.setAccessible(true);
								f.set(this, worldIn);
								f = c.getDeclaredField("basePos");
								f.setAccessible(true);
								f.set(this, position);
								f = c.getDeclaredField("rand");
								f.setAccessible(true);
								f.set(this, rand = new java.util.Random(rand.nextLong()));
								f = c.getDeclaredField("heightLimit");
								f.setAccessible(true);
								if (f.getInt(this) == 0) f.set(this, 5 + rand.nextInt(12));
								Method m = c.getDeclaredMethod("generateLeafNodeList");
								m.setAccessible(true);
								m.invoke(this);
								m = c.getDeclaredMethod("generateLeaves");
								m.setAccessible(true);
								m.invoke(this);
								m = c.getDeclaredMethod("generateTrunk");
								m.setAccessible(true);
								m.invoke(this);
								m = c.getDeclaredMethod("generateLeafNodeBases");
								m.setAccessible(true);
								m.invoke(this);
								f = c.getDeclaredField("world");
								f.setAccessible(true);
								f.set(this, null);
							} catch (Exception e) {
								e.printStackTrace();
								return false;
							}
							return true;
						}
					} : new WorldGenTrees(true) {
						@Override
						public boolean generate(World worldIn, java.util.Random rand, BlockPos position) {
							// Copied from super to remove checks.
							try {
								Class c = WorldGenTrees.class;
								Field f = c.getDeclaredField("minTreeHeight");
								f.setAccessible(true);
								int i = rand.nextInt(3) + f.getInt(this);
								if (position.getY() >= 1 && position.getY() + i + 1 <= worldIn.getHeight()) {
									IBlockState state = worldIn.getBlockState(position.down());
									if (position.getY() < worldIn.getHeight() - i - 1) {
										state.getBlock().onPlantGrow(state, worldIn, position.down(), position);
										for (int i3 = position.getY() - 3 + i; i3 <= position.getY() + i; ++i3) {
											int i4 = i3 - (position.getY() + i);
											int j1 = 1 - i4 / 2;
											for (int k1 = position.getX() - j1; k1 <= position.getX() + j1; ++k1) {
												int l1 = k1 - position.getX();
												for (int i2 = position.getZ() - j1; i2 <= position.getZ() + j1; ++i2) {
													int j2 = i2 - position.getZ();
													if (Math.abs(l1) != j1 || Math.abs(j2) != j1 || rand.nextInt(2) != 0 && i4 != 0) {
														BlockPos blockpos = new BlockPos(k1, i3, i2);
														state = worldIn.getBlockState(blockpos);
														if (state.getBlock().isAir(state, worldIn, blockpos) || state.getBlock().isLeaves(state, worldIn, blockpos) || state.getMaterial() == Material.VINE) {
															f = c.getDeclaredField("metaLeaves");
															f.setAccessible(true);
															setBlockAndNotifyAdequately(worldIn, blockpos, (IBlockState) f.get(this));
														}
													}
												}
											}
										}
										f = c.getDeclaredField("vinesGrow");
										f.setAccessible(true);
										boolean vinesGrow = f.getBoolean(this);
										for (int j3 = 0; j3 < i; ++j3) {
											BlockPos upN = position.up(j3);
											state = worldIn.getBlockState(upN);
											if (state.getBlock().isAir(state, worldIn, upN) || state.getBlock().isLeaves(state, worldIn, upN) || state.getMaterial() == Material.VINE) {
												f = c.getDeclaredField("metaWood");
												f.setAccessible(true);
												setBlockAndNotifyAdequately(worldIn, position.up(j3), (IBlockState) f.get(this));
												if (vinesGrow && j3 > 0) {
													if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(-1, j3, 0))) setBlockAndNotifyAdequately(worldIn, position.add(-1, j3, 0), Blocks.VINE.getDefaultState().withProperty(BlockVine.EAST, true));
													if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(1, j3, 0))) setBlockAndNotifyAdequately(worldIn, position.add(1, j3, 0), Blocks.VINE.getDefaultState().withProperty(BlockVine.WEST, true));
													if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(0, j3, -1))) setBlockAndNotifyAdequately(worldIn, position.add(0, j3, -1), Blocks.VINE.getDefaultState().withProperty(BlockVine.SOUTH, true));
													if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(0, j3, 1))) setBlockAndNotifyAdequately(worldIn, position.add(0, j3, 1), Blocks.VINE.getDefaultState().withProperty(BlockVine.NORTH, true));
												}
											}
										}
										return true;
									} else return false;
								} else return false;
							} catch (Exception e) {
								return false;
							}
						}
					};
					break;
				case SPRUCE:
					generator = big ? new WorldGenMegaPineTree(true, Random.randInt(2) == 0) {
						@Override
						protected boolean ensureGrowable(World worldIn, java.util.Random rand, BlockPos treePos, int height) {
							return true;
						}
					} : new WorldGenTaiga2(true) {
						// Copied from super method but without the check if the tree can be placed
						@Override
						public boolean generate(World worldIn, java.util.Random rand, BlockPos position) {
							int i = rand.nextInt(4) + 6;
							int j = 1 + rand.nextInt(2);
							int k = i - j;
							int l = 2 + rand.nextInt(2);
							if (position.getY() >= 1 && position.getY() + i + 1 <= worldIn.getHeight()) {
								BlockPos down = position.down();
								IBlockState state = worldIn.getBlockState(down);
								if (position.getY() < worldIn.getHeight() - i - 1) {
									state.getBlock().onPlantGrow(state, worldIn, down, position);
									int i3 = rand.nextInt(2);
									int j3 = 1;
									int k3 = 0;
									for (int l3 = 0; l3 <= k; ++l3) {
										int j4 = position.getY() + i - l3;
										for (int i2 = position.getX() - i3; i2 <= position.getX() + i3; ++i2) {
											int j2 = i2 - position.getX();
											for (int k2 = position.getZ() - i3; k2 <= position.getZ() + i3; ++k2) {
												int l2 = k2 - position.getZ();
												if (Math.abs(j2) != i3 || Math.abs(l2) != i3 || i3 <= 0) {
													BlockPos blockpos = new BlockPos(i2, j4, k2);
													state = worldIn.getBlockState(blockpos);
													if (state.getBlock().canBeReplacedByLeaves(state, worldIn, blockpos)) setBlockAndNotifyAdequately(worldIn, blockpos, Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.SPRUCE).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false)));
												}
											}
										}
										if (i3 >= j3) {
											i3 = k3;
											k3 = 1;
											++j3;
											if (j3 > l) j3 = l;
										} else++i3;
									}
									int i4 = rand.nextInt(3);
									for (int k4 = 0; k4 < i - i4; ++k4) {
										BlockPos upN = position.up(k4);
										state = worldIn.getBlockState(upN);
										if (state.getBlock().isAir(state, worldIn, upN) || state.getBlock().isLeaves(state, worldIn, upN)) setBlockAndNotifyAdequately(worldIn, position.up(k4), Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE));
									}
									return true;
								} else return false;
							} else return false;
						}
					};
					break;
				case BIRCH:
					generator = new WorldGenBirchTree(true, false) {
						@Override
						public boolean generate(World worldIn, java.util.Random rand, BlockPos position) {
							try {
								int i = rand.nextInt(3) + 5;
								if (big) i += rand.nextInt(5) + 3;
								if (position.getY() >= 1 && position.getY() + i + 1 <= 256) {
									if (position.getY() < worldIn.getHeight() - i - 1) {
										for (int i2 = position.getY() - 3 + i; i2 <= position.getY() + i; ++i2) {
											int k2 = i2 - (position.getY() + i);
											int l2 = 1 - k2 / 2;
											for (int i3 = position.getX() - l2; i3 <= position.getX() + l2; ++i3) {
												int j1 = i3 - position.getX();
												for (int k1 = position.getZ() - l2; k1 <= position.getZ() + l2; ++k1) {
													int l1 = k1 - position.getZ();
													if (Math.abs(j1) != l2 || Math.abs(l1) != l2 || rand.nextInt(2) != 0 && k2 != 0) {
														BlockPos blockpos = new BlockPos(i3, i2, k1);
														IBlockState state2 = worldIn.getBlockState(blockpos);
														if (state2.getBlock().isAir(state2, worldIn, blockpos) || state2.getBlock().isAir(state2, worldIn, blockpos)) setBlockAndNotifyAdequately(worldIn, blockpos, Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.BIRCH).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false)));
													}
												}
											}
										}
										for (int j2 = 0; j2 < i; ++j2) {
											BlockPos upN = position.up(j2);
											IBlockState state2 = worldIn.getBlockState(upN);
											if (state2.getBlock().isAir(state2, worldIn, upN) || state2.getBlock().isLeaves(state2, worldIn, upN)) setBlockAndNotifyAdequately(worldIn, position.up(j2), Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.BIRCH));
										}

										return true;
									} else return false;
								} else return false;
							} catch (Exception e) {
								return false;
							}
						}
					};
					break;
				case JUNGLE:
					generator = big ? new WorldGenMegaJungle(true, 10, 20, Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE), Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false))) {
						@Override
						protected boolean ensureGrowable(World worldIn, java.util.Random rand, BlockPos treePos, int height) {
							return true;
						}
					} : new WorldGenTrees(true, 4 + Random.randInt(7), Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE), Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE).withProperty(BlockLeaves.CHECK_DECAY, false), false) {
						@Override
						public boolean generate(World worldIn, java.util.Random rand, BlockPos position) {
							// Copied from super to remove checks.
							try {
								Class c = WorldGenTrees.class;
								Field f = c.getDeclaredField("minTreeHeight");
								f.setAccessible(true);
								int i = rand.nextInt(3) + f.getInt(this);
								if (position.getY() >= 1 && position.getY() + i + 1 <= worldIn.getHeight()) {
									IBlockState state = worldIn.getBlockState(position.down());
									if (position.getY() < worldIn.getHeight() - i - 1) {
										state.getBlock().onPlantGrow(state, worldIn, position.down(), position);
										for (int i3 = position.getY() - 3 + i; i3 <= position.getY() + i; ++i3) {
											int i4 = i3 - (position.getY() + i);
											int j1 = 1 - i4 / 2;
											for (int k1 = position.getX() - j1; k1 <= position.getX() + j1; ++k1) {
												int l1 = k1 - position.getX();
												for (int i2 = position.getZ() - j1; i2 <= position.getZ() + j1; ++i2) {
													int j2 = i2 - position.getZ();
													if (Math.abs(l1) != j1 || Math.abs(j2) != j1 || rand.nextInt(2) != 0 && i4 != 0) {
														BlockPos blockpos = new BlockPos(k1, i3, i2);
														state = worldIn.getBlockState(blockpos);
														if (state.getBlock().isAir(state, worldIn, blockpos) || state.getBlock().isLeaves(state, worldIn, blockpos) || state.getMaterial() == Material.VINE) {
															f = c.getDeclaredField("metaLeaves");
															f.setAccessible(true);
															setBlockAndNotifyAdequately(worldIn, blockpos, (IBlockState) f.get(this));
														}
													}
												}
											}
										}
										f = c.getDeclaredField("vinesGrow");
										f.setAccessible(true);
										boolean vinesGrow = f.getBoolean(this);
										for (int j3 = 0; j3 < i; ++j3) {
											BlockPos upN = position.up(j3);
											state = worldIn.getBlockState(upN);
											if (state.getBlock().isAir(state, worldIn, upN) || state.getBlock().isLeaves(state, worldIn, upN) || state.getMaterial() == Material.VINE) {
												f = c.getDeclaredField("metaWood");
												f.setAccessible(true);
												setBlockAndNotifyAdequately(worldIn, position.up(j3), (IBlockState) f.get(this));
												if (vinesGrow && j3 > 0) {
													if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(-1, j3, 0))) setBlockAndNotifyAdequately(worldIn, position.add(-1, j3, 0), Blocks.VINE.getDefaultState().withProperty(BlockVine.EAST, true));
													if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(1, j3, 0))) setBlockAndNotifyAdequately(worldIn, position.add(1, j3, 0), Blocks.VINE.getDefaultState().withProperty(BlockVine.WEST, true));
													if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(0, j3, -1))) setBlockAndNotifyAdequately(worldIn, position.add(0, j3, -1), Blocks.VINE.getDefaultState().withProperty(BlockVine.SOUTH, true));
													if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(0, j3, 1))) setBlockAndNotifyAdequately(worldIn, position.add(0, j3, 1), Blocks.VINE.getDefaultState().withProperty(BlockVine.NORTH, true));
												}
											}
										}
										if (vinesGrow) {
											for (int k3 = position.getY() - 3 + i; k3 <= position.getY() + i; ++k3) {
												int j4 = k3 - (position.getY() + i);
												int k4 = 2 - j4 / 2;
												BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();
												for (int l4 = position.getX() - k4; l4 <= position.getX() + k4; ++l4)
													for (int i5 = position.getZ() - k4; i5 <= position.getZ() + k4; ++i5) {
														blockpos$mutableblockpos1.setPos(l4, k3, i5);
														state = worldIn.getBlockState(blockpos$mutableblockpos1);
														if (state.getBlock().isLeaves(state, worldIn, blockpos$mutableblockpos1)) {
															BlockPos blockpos2 = blockpos$mutableblockpos1.west();
															BlockPos blockpos3 = blockpos$mutableblockpos1.east();
															BlockPos blockpos4 = blockpos$mutableblockpos1.north();
															BlockPos blockpos1 = blockpos$mutableblockpos1.south();
															if (rand.nextInt(4) == 0 && worldIn.isAirBlock(blockpos2)) {
																setBlockAndNotifyAdequately(worldIn, blockpos2, Blocks.VINE.getDefaultState().withProperty(BlockVine.EAST, true));
																int i0 = 4;
																for (BlockPos blockpos = blockpos2.down(); worldIn.isAirBlock(blockpos) && i0 > 0; --i0, blockpos = blockpos.down())
																	setBlockAndNotifyAdequately(worldIn, blockpos, Blocks.VINE.getDefaultState().withProperty(BlockVine.EAST, true));
															}
															if (rand.nextInt(4) == 0 && worldIn.isAirBlock(blockpos3)) {
																setBlockAndNotifyAdequately(worldIn, blockpos3, Blocks.VINE.getDefaultState().withProperty(BlockVine.WEST, true));
																int i0 = 4;
																for (BlockPos blockpos = blockpos3.down(); worldIn.isAirBlock(blockpos) && i0 > 0; --i0, blockpos = blockpos.down())
																	setBlockAndNotifyAdequately(worldIn, blockpos, Blocks.VINE.getDefaultState().withProperty(BlockVine.WEST, true));
															}
															if (rand.nextInt(4) == 0 && worldIn.isAirBlock(blockpos4)) {
																setBlockAndNotifyAdequately(worldIn, blockpos4, Blocks.VINE.getDefaultState().withProperty(BlockVine.SOUTH, true));
																int i0 = 4;
																for (BlockPos blockpos = blockpos4.down(); worldIn.isAirBlock(blockpos) && i0 > 0; --i0, blockpos = blockpos.down())
																	setBlockAndNotifyAdequately(worldIn, blockpos, Blocks.VINE.getDefaultState().withProperty(BlockVine.SOUTH, true));
															}
															if (rand.nextInt(4) == 0 && worldIn.isAirBlock(blockpos1)) {
																setBlockAndNotifyAdequately(worldIn, blockpos1, Blocks.VINE.getDefaultState().withProperty(BlockVine.NORTH, true));
																int i0 = 4;
																for (BlockPos blockpos = blockpos1.down(); worldIn.isAirBlock(blockpos) && i0 > 0; --i0, blockpos = blockpos.down())
																	setBlockAndNotifyAdequately(worldIn, blockpos, Blocks.VINE.getDefaultState().withProperty(BlockVine.NORTH, true));
															}
														}
													}
											}
											if (rand.nextInt(5) == 0 && i > 5) for (int l3 = 0; l3 < 2; ++l3)
												for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
													if (rand.nextInt(4 - l3) == 0) {
														EnumFacing enumfacing1 = enumfacing.getOpposite();
														setBlockAndNotifyAdequately(worldIn, position.add(enumfacing1.getFrontOffsetX(), i - 5 + l3, enumfacing1.getFrontOffsetZ()), Blocks.COCOA.getDefaultState().withProperty(BlockCocoa.AGE, rand.nextInt(3)).withProperty(BlockHorizontal.FACING, enumfacing));
													}
										}
										return true;
									} else return false;
								} else return false;
							} catch (Exception e) {
								return false;
							}
						}
					};
					;
					break;
				case ACACIA:
					generator = new WorldGenSavannaTree(true) {
						@Override
						public boolean generate(World worldIn, java.util.Random rand, BlockPos position) {
							IBlockState TRUNK = Blocks.LOG2.getDefaultState().withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.ACACIA);
							IBlockState LEAF = Blocks.LEAVES2.getDefaultState().withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.ACACIA).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
							int i = rand.nextInt(3) + rand.nextInt(3) + 5 + (big ? rand.nextInt(7) : 0);
							if (position.getY() >= 1 && position.getY() + i + 1 <= 256) {
								BlockPos down = position.down();
								IBlockState state = worldIn.getBlockState(down);
								if (position.getY() < worldIn.getHeight() - i - 1) {
									state.getBlock().onPlantGrow(state, worldIn, down, position);
									EnumFacing enumfacing = EnumFacing.Plane.HORIZONTAL.random(rand);
									int k2 = i - rand.nextInt(4) - 1;
									int l2 = 3 - rand.nextInt(3);
									int i3 = position.getX();
									int j1 = position.getZ();
									int k1 = 0;
									for (int l1 = 0; l1 < i; ++l1) {
										int i2 = position.getY() + l1;
										if (l1 >= k2 && l2 > 0) {
											i3 += enumfacing.getFrontOffsetX();
											j1 += enumfacing.getFrontOffsetZ();
											--l2;
										}
										BlockPos blockpos = new BlockPos(i3, i2, j1);
										state = worldIn.getBlockState(blockpos);
										if (state.getBlock().isAir(state, worldIn, blockpos) || state.getBlock().isLeaves(state, worldIn, blockpos)) {
											setBlockAndNotifyAdequately(worldIn, blockpos, TRUNK);
											;
											k1 = i2;
										}
									}
									BlockPos blockpos2 = new BlockPos(i3, k1, j1);
									for (int j3 = -3; j3 <= 3; ++j3)
										for (int i4 = -3; i4 <= 3; ++i4)
											if (Math.abs(j3) != 3 || Math.abs(i4) != 3) {
												IBlockState state0 = worldIn.getBlockState(blockpos2.add(j3, 0, i4));
												if (state.getBlock().isAir(state0, worldIn, blockpos2.add(j3, 0, i4)) || state.getBlock().isLeaves(state0, worldIn, blockpos2.add(j3, 0, i4))) setBlockAndNotifyAdequately(worldIn, blockpos2.add(j3, 0, i4), LEAF);
											}
									blockpos2 = blockpos2.up();
									for (int k3 = -1; k3 <= 1; ++k3)
										for (int j4 = -1; j4 <= 1; ++j4) {
											IBlockState state0 = worldIn.getBlockState(blockpos2.add(k3, 0, j4));
											if (state.getBlock().isAir(state0, worldIn, blockpos2.add(k3, 0, j4)) || state.getBlock().isLeaves(state0, worldIn, blockpos2.add(k3, 0, j4))) setBlockAndNotifyAdequately(worldIn, blockpos2.add(k3, 0, j4), LEAF);
										}
									IBlockState state0 = worldIn.getBlockState(blockpos2.east(2));
									if (state.getBlock().isAir(state0, worldIn, blockpos2.east(2)) || state.getBlock().isLeaves(state0, worldIn, blockpos2.east(2))) setBlockAndNotifyAdequately(worldIn, blockpos2.east(2), LEAF);
									state0 = worldIn.getBlockState(blockpos2.west(2));
									if (state.getBlock().isAir(state0, worldIn, blockpos2.west(2)) || state.getBlock().isLeaves(state0, worldIn, blockpos2.west(2))) setBlockAndNotifyAdequately(worldIn, blockpos2.west(2), LEAF);
									state0 = worldIn.getBlockState(blockpos2.south(2));
									if (state.getBlock().isAir(state0, worldIn, blockpos2.south(2)) || state.getBlock().isLeaves(state0, worldIn, blockpos2.south(2))) setBlockAndNotifyAdequately(worldIn, blockpos2.south(2), LEAF);
									state0 = worldIn.getBlockState(blockpos2.north(2));
									if (state.getBlock().isAir(state0, worldIn, blockpos2.north(2)) || state.getBlock().isLeaves(state0, worldIn, blockpos2.north(2))) setBlockAndNotifyAdequately(worldIn, blockpos2.north(2), LEAF);
									i3 = position.getX();
									j1 = position.getZ();
									EnumFacing enumfacing1 = EnumFacing.Plane.HORIZONTAL.random(rand);
									if (enumfacing1 != enumfacing) {
										int l3 = k2 - rand.nextInt(2) - 1;
										int k4 = 1 + rand.nextInt(3);
										k1 = 0;
										for (int l4 = l3; l4 < i && k4 > 0; --k4) {
											if (l4 >= 1) {
												int j2 = position.getY() + l4;
												i3 += enumfacing1.getFrontOffsetX();
												j1 += enumfacing1.getFrontOffsetZ();
												BlockPos blockpos1 = new BlockPos(i3, j2, j1);
												state = worldIn.getBlockState(blockpos1);

												if (state.getBlock().isAir(state, worldIn, blockpos1) || state.getBlock().isLeaves(state, worldIn, blockpos1)) {
													setBlockAndNotifyAdequately(worldIn, blockpos1, TRUNK);
													k1 = j2;
												}
											}

											++l4;
										}
										if (k1 > 0) {
											BlockPos blockpos3 = new BlockPos(i3, k1, j1);

											for (int i5 = -2; i5 <= 2; ++i5)
												for (int k5 = -2; k5 <= 2; ++k5)
													if (Math.abs(i5) != 2 || Math.abs(k5) != 2) {
														state0 = worldIn.getBlockState(blockpos3.add(i5, 0, k5));
														if (state.getBlock().isAir(state0, worldIn, blockpos3.add(i5, 0, k5)) || state.getBlock().isLeaves(state0, worldIn, blockpos3.add(i5, 0, k5))) setBlockAndNotifyAdequately(worldIn, blockpos3.add(i5, 0, k5), LEAF);
													}

											blockpos3 = blockpos3.up();

											for (int j5 = -1; j5 <= 1; ++j5)
												for (int l5 = -1; l5 <= 1; ++l5) {
													state0 = worldIn.getBlockState(blockpos3.add(j5, 0, l5));
													if (state.getBlock().isAir(state0, worldIn, blockpos3.add(j5, 0, l5)) || state.getBlock().isLeaves(state0, worldIn, blockpos3.add(j5, 0, l5))) setBlockAndNotifyAdequately(worldIn, blockpos3.add(j5, 0, l5), LEAF);
												}
										}
									}

									return true;
								} else return false;
							} else return false;
						}
					};
					break;
				case DARK_OAK:
					generator = new WorldGenCanopyTree(true) {
						@Override
						public boolean generate(World worldIn, java.util.Random rand, BlockPos position) {
							Class c = WorldGenCanopyTree.class;
							Method placeLogAt;
							Method placeLeafAt;
							try {
								placeLogAt = c.getDeclaredMethod("placeLogAt", World.class, BlockPos.class);
								placeLogAt.setAccessible(true);
								placeLeafAt = c.getDeclaredMethod("placeLeafAt", World.class, int.class, int.class, int.class);
								placeLeafAt.setAccessible(true);
								int i = rand.nextInt(3) + rand.nextInt(2) + 6;
								int j = position.getX();
								int k = position.getY();
								int l = position.getZ();
								if (k >= 1 && k + i + 1 < 256) {
									BlockPos blockpos = position.down();
									IBlockState state = worldIn.getBlockState(blockpos);
									EnumFacing enumfacing = EnumFacing.Plane.HORIZONTAL.random(rand);
									int i1 = i - rand.nextInt(4);
									int j1 = 2 - rand.nextInt(3);
									int k1 = j;
									int l1 = l;
									int i2 = k + i - 1;
									for (int j2 = 0; j2 < i; ++j2) {
										if (j2 >= i1 && j1 > 0) {
											k1 += enumfacing.getFrontOffsetX();
											l1 += enumfacing.getFrontOffsetZ();
											--j1;
										}
										int k2 = k + j2;
										BlockPos blockpos1 = new BlockPos(k1, k2, l1);
										state = worldIn.getBlockState(blockpos1);
										if (state.getBlock().isAir(state, worldIn, blockpos1) || state.getBlock().isLeaves(state, worldIn, blockpos1)) {
											placeLogAt.invoke(this, worldIn, blockpos1);
											placeLogAt.invoke(this, worldIn, blockpos1.east());
											placeLogAt.invoke(this, worldIn, blockpos1.south());
											placeLogAt.invoke(this, worldIn, blockpos1.east().south());
										}
									}
									for (int i3 = -2; i3 <= 0; ++i3)
										for (int l3 = -2; l3 <= 0; ++l3) {
											int k4 = -1;
											placeLeafAt.invoke(this, worldIn, k1 + i3, i2 + k4, l1 + l3);
											placeLeafAt.invoke(this, worldIn, 1 + k1 - i3, i2 + k4, l1 + l3);
											placeLeafAt.invoke(this, worldIn, k1 + i3, i2 + k4, 1 + l1 - l3);
											placeLeafAt.invoke(this, worldIn, 1 + k1 - i3, i2 + k4, 1 + l1 - l3);
											if ((i3 > -2 || l3 > -1) && (i3 != -1 || l3 != -2)) {
												k4 = 1;
												placeLeafAt.invoke(this, worldIn, k1 + i3, i2 + k4, l1 + l3);
												placeLeafAt.invoke(this, worldIn, 1 + k1 - i3, i2 + k4, l1 + l3);
												placeLeafAt.invoke(this, worldIn, k1 + i3, i2 + k4, 1 + l1 - l3);
												placeLeafAt.invoke(this, worldIn, 1 + k1 - i3, i2 + k4, 1 + l1 - l3);
											}
										}
									if (rand.nextBoolean()) {
										placeLeafAt.invoke(this, worldIn, k1, i2 + 2, l1);
										placeLeafAt.invoke(this, worldIn, k1 + 1, i2 + 2, l1);
										placeLeafAt.invoke(this, worldIn, k1 + 1, i2 + 2, l1 + 1);
										placeLeafAt.invoke(this, worldIn, k1, i2 + 2, l1 + 1);
									}
									for (int j3 = -3; j3 <= 4; ++j3)
										for (int i4 = -3; i4 <= 4; ++i4)
											if ((j3 != -3 || i4 != -3) && (j3 != -3 || i4 != 4) && (j3 != 4 || i4 != -3) && (j3 != 4 || i4 != 4) && (Math.abs(j3) < 3 || Math.abs(i4) < 3)) placeLeafAt.invoke(this, worldIn, k1 + j3, i2, l1 + i4);
									for (int k3 = -1; k3 <= 2; ++k3)
										for (int j4 = -1; j4 <= 2; ++j4)
											if ((k3 < 0 || k3 > 1 || j4 < 0 || j4 > 1) && rand.nextInt(3) <= 0) {
												int l4 = rand.nextInt(3) + 2;
												for (int i5 = 0; i5 < l4; ++i5)
													placeLogAt.invoke(this, worldIn, new BlockPos(j + k3, i2 - i5 - 1, l + j4));
												for (int j5 = -1; j5 <= 1; ++j5)
													for (int l2 = -1; l2 <= 1; ++l2)
														placeLeafAt.invoke(this, worldIn, k1 + k3 + j5, i2, l1 + j4 + l2);
												for (int k5 = -2; k5 <= 2; ++k5)
													for (int l5 = -2; l5 <= 2; ++l5)
														if (Math.abs(k5) != 2 || Math.abs(l5) != 2) placeLeafAt.invoke(this, worldIn, k1 + k3 + k5, i2 - 1, l1 + j4 + l5);
											}
									return true;
								} else return false;
							} catch (Exception e) {
								return false;
							}
						}
					};
					break;
				}
				if (!generator.generate(sender.getEntityWorld(), sender.getEntityWorld().rand, pos)) Reference.sendMessage(sender, TextFormatting.RED + "The tree could not be placed.");
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "tree", "Plants a tree where you're looking.", true);
		}

		private String usage = "/tree [type] [big] Spawns a tree where you're currently looking.";

	}

}