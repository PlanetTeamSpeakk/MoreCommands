package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

public class ToggleCheatsCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literalReq("togglecheats")
                .executes(ctx -> {
                    MinecraftServer server = ctx.getSource().getServer();
                    if (server.isSingleplayer()) {
                        server.getPlayerManager().setCheatsAllowed(!server.getPlayerManager().areCheatsAllowed());
                        sendMsg(ctx, "Cheats are now " + Util.formatFromBool(server.getPlayerManager().areCheatsAllowed(), "allowed", "disallowed") + DF + ".");
                        return 1;
                    } else sendError(ctx, "This command can only be used on singleplayer.");
                    return 0;
                }));
    }
}
