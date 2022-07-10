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
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class TpaCommand extends Command {

    private static final List<TpaRequest> requests = new ArrayList<>();

    public void preinit(boolean serverOnly) {
        TickEvent.SERVER_LEVEL_POST.register(world -> {
            for (TpaRequest request : new ArrayList<>(requests))
                if (world == request.to.getLevel() && world.getGameTime() - request.creationTick >= world.getGameRules().getInt(MoreGameRules.get().tpaTimeoutRule())) {
                    request.timeout();
                    requests.remove(request);
                }
        });
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReq("tpa")
                .then(argument("player", EntityArgument.player())
                        .executes(ctx -> executeTpa(ctx, false))));

        dispatcher.register(literalReq("tpahere")
                .then(argument("player", EntityArgument.player())
                        .executes(ctx -> executeTpa(ctx, true))));

        dispatcher.getRoot().addChild(MoreCommands.createAlias("tpyes", dispatcher.register(literalReq("tpaccept")
                .executes(ctx -> executeResp(ctx, true)))));

        dispatcher.getRoot().addChild(MoreCommands.createAlias("tpno", dispatcher.register(literalReq("tpdeny")
                .executes(ctx -> executeResp(ctx, false)))));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/tpa";
    }

    private int executeResp(CommandContext<CommandSourceStack> ctx, boolean accept) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        for (int i = requests.size()-1; i >= 0; i--)
            if (requests.get(i).to == player) {
                if (accept) requests.get(i).accept();
                else requests.get(i).deny();
                requests.remove(i);
                break;
            }
        return accept ? 2 : 1;
    }

    private int executeTpa(CommandContext<CommandSourceStack> ctx, boolean here) throws CommandSyntaxException {
        TpaRequest request = new TpaRequest(ctx.getSource().getPlayerOrException(), EntityArgument.getPlayer(ctx, "player"), here);
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
        private final ServerPlayer from, to;
        private final boolean here;
        private final long creationTick;

        private TpaRequest(ServerPlayer from, ServerPlayer to, boolean here) {
            creationTick = from.getLevel().getGameTime();
            this.from = from;
            this.to = to;
            this.here = here;
        }

        private void informOther() {
            sendMsg(to, IMoreCommands.get().textToString(from.getDisplayName(), SS, true) + DF + " has requested " + (here ? "you to teleport to them" : "teleport to you") + ".");
        }

        private void accept() {
            ServerPlayer from = here ? this.to : this.from;
            ServerPlayer to = here ? this.from : this.to;
            MoreCommands.teleport(from, to.getLevel(), to.position(), ((MixinEntityAccessor) to).getYRot_(), ((MixinEntityAccessor) to).getXRot_());
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
