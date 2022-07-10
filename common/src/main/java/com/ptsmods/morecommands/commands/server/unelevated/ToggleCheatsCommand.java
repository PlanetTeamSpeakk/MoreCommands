package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;

public class ToggleCheatsCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReq("togglecheats")
                .executes(ctx -> {
                    MinecraftServer server = ctx.getSource().getServer();
                    if (server.isSingleplayer()) {
                        server.getPlayerList().setAllowCheatsForAllPlayers(!server.getPlayerList().isAllowCheatsForAllPlayers());
                        sendMsg(ctx, "Cheats are now " + Util.formatFromBool(server.getPlayerList().isAllowCheatsForAllPlayers(), "allowed", "disallowed") + DF + ".");
                        return 1;
                    } else sendError(ctx, "This command can only be used on singleplayer.");
                    return 0;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/toggle-cheats";
    }
}
