package com.ptsmods.morecommands.miscellaneous;

import com.google.common.collect.ImmutableList;
import com.ptsmods.morecommands.api.util.compat.Compat;
import dev.architectury.event.events.common.TickEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;

public class Chair {
    private static final List<Chair> chairs = new ArrayList<>();
    private static final List<StairsShape> VALID_SHAPES = ImmutableList.of(StairsShape.INNER_LEFT, StairsShape.INNER_RIGHT, StairsShape.STRAIGHT);
    private static final Queue<Tuple<Chair, Level>> chairQueue = new ConcurrentLinkedQueue<>();
    private Arrow arrow = null;
    private final Player player;
    private final BlockPos pos;

    static {
        TickEvent.SERVER_LEVEL_POST.register(world -> {
            for (Chair chair : new ArrayList<>(chairs))
                if (chair.arrow != null && world == chair.arrow.level) {
                    if (!chair.arrow.isAlive() && chair.player.getVehicle() == chair.arrow) chair.player.stopRiding();
                    if (chair.player.getVehicle() != chair.arrow) chair.arrow.kill();
                    if (!chair.arrow.isAlive()) chairs.remove(chair);
                }
            while (chairQueue.peek() != null) {
                Tuple<Chair, Level> pair = chairQueue.poll();
                pair.getA().place(pair.getB());
            }
        });
    }

    public static boolean isValid(BlockState state) {
        return Compat.get().tagContains(new ResourceLocation("minecraft:stairs"), state.getBlock()) && VALID_SHAPES.contains(state.getValue(StairBlock.SHAPE)) && state.getValue(StairBlock.HALF) == Half.BOTTOM;
    }

    public static void createAndPlace(BlockPos pos, Player player, Level world) {
        Chair chair = new Chair(pos, player);
        // This method should really only get called when receiving a packet for it,
        // packets are received on the network thread but entities should be spawned
        // on the server thread.
        chairQueue.add(new Tuple<>(chair, world));
    }

    private Chair(BlockPos pos, Player player) {
        this.pos = pos;
        this.player = player;
    }

    public void place(Level world) {
        arrow = new Arrow(world, pos.getX() + .5, pos.getY(), pos.getZ() + .5);
        arrow.setInvisible(true);
        world.addFreshEntity(arrow);
        player.startRiding(arrow);
        chairs.add(this);
    }
}
