package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.callbacks.EntityTeleportCallback;
import com.ptsmods.morecommands.callbacks.PlayerConnectionCallback;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.miscellaneous.Location;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class BackCommand extends Command {

    private final Map<ServerPlayerEntity, Pair<Location<ServerWorld>, Boolean>> lastLocations = new HashMap<>();

    @Override
    public void preinit() {
        registerCallback(EntityTeleportCallback.EVENT, (entity, worldFrom, worldTo, from, to) -> {
            if (entity instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) entity;
                Location<ServerWorld> loc = new Location<>((ServerWorld) worldFrom, from, player.getRotationClient());
                Pair<Location<ServerWorld>, Boolean> lastLoc = lastLocations.get(player);
                Pair<Location<ServerWorld>, Boolean> pair;
                if (lastLoc != null && lastLoc.getRight()) pair = new Pair<>(new Location<>(lastLoc.getLeft().getWorld(), loc.getPos(), loc.getRot()), false);
                else pair = new Pair<>(loc, worldFrom != worldTo && from.equals(to));
                lastLocations.put(player, pair);
            }
            return false;
        });
        // Player teleports around a bit when first joining which can cause the command to do all kinds of weird things when used upon join.
        registerCallback(PlayerConnectionCallback.JOIN, lastLocations::remove);
    }

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) throws Exception {
        dispatcher.register(literal("back").executes(ctx -> {
            if (!lastLocations.containsKey(ctx.getSource().getPlayer())) sendMsg(ctx, Formatting.RED + "No last location has been saved for you.");
            else {
                Location<ServerWorld> loc = lastLocations.get(ctx.getSource().getPlayer()).getLeft();
                ctx.getSource().getPlayer().teleport(loc.getWorld(), loc.getPos().getX(), loc.getPos().getY(), loc.getPos().getZ(), loc.getRot().y, loc.getRot().x);
                return 1;
            }
            return 0;
        }));
    }
}
