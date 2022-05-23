package com.ptsmods.morecommands.miscellaneous;

import com.google.common.collect.ImmutableList;
import com.ptsmods.morecommands.api.util.compat.Compat;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.StairShape;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Chair {
    private static final List<Chair> chairs = new ArrayList<>();
    private static final List<StairShape> VALID_SHAPES = ImmutableList.of(StairShape.INNER_LEFT, StairShape.INNER_RIGHT, StairShape.STRAIGHT);
    private static final Queue<Pair<Chair, World>> chairQueue = new ConcurrentLinkedQueue<>();
    private ArrowEntity arrow = null;
    private final PlayerEntity player;
    private final BlockPos pos;

    static {
        TickEvent.SERVER_LEVEL_POST.register(world -> {
            for (Chair chair : new ArrayList<>(chairs))
                if (chair.arrow != null && world == chair.arrow.world) {
                    if (!chair.arrow.isAlive() && chair.player.getVehicle() == chair.arrow) chair.player.stopRiding();
                    if (chair.player.getVehicle() != chair.arrow) chair.arrow.kill();
                    if (!chair.arrow.isAlive()) chairs.remove(chair);
                }
            while (chairQueue.peek() != null) {
                Pair<Chair, World> pair = chairQueue.poll();
                pair.getLeft().place(pair.getRight());
            }
        });
    }

    public static boolean isValid(BlockState state) {
        return Compat.get().tagContains(new Identifier("minecraft:stairs"), state.getBlock()) && VALID_SHAPES.contains(state.get(StairsBlock.SHAPE)) && state.get(StairsBlock.HALF) == BlockHalf.BOTTOM;
    }

    public static void createAndPlace(BlockPos pos, PlayerEntity player, World world) {
        Chair chair = new Chair(pos, player);
        // This method should really only get called when receiving a packet for it,
        // packets are received on the network thread but entities should be spawned
        // on the server thread.
        chairQueue.add(new Pair<>(chair, world));
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
