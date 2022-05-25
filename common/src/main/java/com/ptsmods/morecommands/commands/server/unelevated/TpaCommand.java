package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import com.ptsmods.morecommands.mixin.common.accessor.MixinEntityAccessor;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class TpaCommand extends Command {

    private static final List<TpaRequest> requests = new ArrayList<>();

    public void preinit(boolean serverOnly) {
        TickEvent.SERVER_LEVEL_POST.register(world -> {
            for (TpaRequest request : new ArrayList<>(requests))
                if (world == request.to.getWorld() && world.getTime() - request.creationTick >= world.getGameRules().getInt(MoreGameRules.get().tpaTimeoutRule())) {
                    request.timeout();
                    requests.remove(request);
                }
        });
    }

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literalReq("tpa")
                .then(argument("player", EntityArgumentType.player())
                        .executes(ctx -> executeTpa(ctx, false))));

        dispatcher.register(literalReq("tpahere")
                .then(argument("player", EntityArgumentType.player())
                        .executes(ctx -> executeTpa(ctx, true))));

        dispatcher.getRoot().addChild(MoreCommands.createAlias("tpyes", dispatcher.register(literalReq("tpaccept")
                .executes(ctx -> executeResp(ctx, true)))));

        dispatcher.getRoot().addChild(MoreCommands.createAlias("tpno", dispatcher.register(literalReq("tpdeny")
                .executes(ctx -> executeResp(ctx, false)))));
    }

    private int executeResp(CommandContext<ServerCommandSource> ctx, boolean accept) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        for (int i = requests.size()-1; i >= 0; i--)
            if (requests.get(i).to == player) {
                if (accept) requests.get(i).accept();
                else requests.get(i).deny();
                requests.remove(i);
                break;
            }
        return accept ? 2 : 1;
    }

    private int executeTpa(CommandContext<ServerCommandSource> ctx, boolean here) throws CommandSyntaxException {
        TpaRequest request = new TpaRequest(ctx.getSource().getPlayerOrThrow(), EntityArgumentType.getPlayer(ctx, "player"), here);
        request.informOther();
        requests.add(request);
        sendMsg(ctx, "A teleportation request has been sent.");
        return 1;
    }

    @Override
    public boolean isDedicatedOnly() {
        return true;
    }

    private static class TpaRequest {
        private final ServerPlayerEntity from, to;
        private final boolean here;
        private final long creationTick;

        private TpaRequest(ServerPlayerEntity from, ServerPlayerEntity to, boolean here) {
            creationTick = from.getWorld().getTime();
            this.from = from;
            this.to = to;
            this.here = here;
        }

        private void informOther() {
            sendMsg(to, IMoreCommands.get().textToString(from.getDisplayName(), SS, true) + DF + " has requested " + (here ? "you to teleport to them" : "teleport to you") + ".");
        }

        private void accept() {
            ServerPlayerEntity from = here ? this.to : this.from;
            ServerPlayerEntity to = here ? this.from : this.to;
            MoreCommands.teleport(from, to.getWorld(), to.getPos(), ((MixinEntityAccessor) to).getYaw_(), ((MixinEntityAccessor) to).getPitch_());
        }

        private void deny() {
            sendMsg(from, IMoreCommands.get().textToString(to.getDisplayName(), SS, true) + DF + " has declined your teleportation request.");
        }

        private void timeout() {
            sendMsg(to, "The teleportation request from " + IMoreCommands.get().textToString(from.getDisplayName(), SS, true) + DF + " has timed out.");
            sendMsg(from, "The teleportation request to " + IMoreCommands.get().textToString(to.getDisplayName(), SS, true) + DF + " has timed out.");
        }

    }
}
