package com.ptsmods.morecommands.miscellaneous;

import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.StairShape;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class Chair {

	private static final List<Chair> chairs = new ArrayList<>();
	private static final List<StairShape> VALID_SHAPES = ImmutableList.of(StairShape.INNER_LEFT, StairShape.INNER_RIGHT, StairShape.STRAIGHT);
	private ArrowEntity arrow = null;
	private final PlayerEntity player;
	private final BlockPos pos;

	static {
		ServerTickEvents.END_WORLD_TICK.register(world -> {
			for (Chair chair : new ArrayList<>(chairs))
				if (chair.arrow != null && world == chair.arrow.world) {
					if (!chair.arrow.isAlive() && chair.player.getVehicle() == chair.arrow) chair.player.stopRiding();
					if (chair.player.getVehicle() != chair.arrow) chair.arrow.kill();
					if (!chair.arrow.isAlive()) chairs.remove(chair);
				}
		});
	}

	public static boolean isValid(BlockState state) {
		return BlockTags.STAIRS.contains(state.getBlock()) && VALID_SHAPES.contains(state.get(StairsBlock.SHAPE)) && state.get(StairsBlock.HALF) == BlockHalf.BOTTOM;
	}

	public static Chair createAndPlace(BlockPos pos, PlayerEntity player, World world) {
		Chair chair = new Chair(pos, player);
		chair.place(world);
		return chair;
	}

	private Chair(BlockPos pos, PlayerEntity player) {
		this.pos = pos;
		this.player = player;
	}

	public void place(World world) {
		arrow = new ArrowEntity(world, pos.getX() + .5, pos.getY(), pos.getZ() + .5);
		arrow.setInvisible(true);
		world.spawnEntity(arrow);
		player.startRiding(arrow);
		chairs.add(this);
	}
}
