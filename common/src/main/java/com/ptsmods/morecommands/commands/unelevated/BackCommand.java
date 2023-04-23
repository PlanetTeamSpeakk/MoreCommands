package com.ptsmods.morecommands.commands.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.api.callbacks.EntityTeleportEvent;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.miscellaneous.Location;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BackCommand extends Command {
    private final Map<UUID, Tuple<Location<ServerLevel>, Boolean>> lastLocations = new HashMap<>();

    @Override
    public void preinit(boolean serverOnly) {
        EntityTeleportEvent.EVENT.register((entity, worldFrom, worldTo, from, to) -> {
            if (entity instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer) entity;
                Location<ServerLevel> loc = new Location<>((ServerLevel) worldFrom, from, player.getRotationVector());
                Tuple<Location<ServerLevel>, Boolean> lastLoc = lastLocations.get(Compat.get().getUUID(player));
                Tuple<Location<ServerLevel>, Boolean> pair;
                if (lastLoc != null && lastLoc.getB()) pair = new Tuple<>(new Location<>(lastLoc.getA().getWorld(), loc.getPos(), loc.getRot()), false);
                else pair = new Tuple<>(loc, worldFrom != worldTo && from.equals(to));
                lastLocations.put(Compat.get().getUUID(player), pair);
            }
            return false;
        });
        // Player teleports around a bit when first joining which can cause the command to do all kinds of weird things when used upon join.
        PlayerEvent.PLAYER_JOIN.register(player -> lastLocations.remove(Compat.get().getUUID(player)));
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) throws Exception {
        dispatcher.register(literalReq("back")
                .executes(ctx -> {
                    if (!lastLocations.containsKey(Compat.get().getUUID(ctx.getSource().getPlayerOrException())))
                        return sendError(ctx, "No last location has been saved for you.");

                    Location<ServerLevel> loc = lastLocations.get(ctx.getSource().getPlayerOrException().getUUID()).getA();
                    ctx.getSource().getPlayerOrException().teleportTo(loc.getWorld(), loc.getPos().x(), loc.getPos().y(), loc.getPos().z(), loc.getRot().y, loc.getRot().x);
                    return 1;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/back";
    }
}
