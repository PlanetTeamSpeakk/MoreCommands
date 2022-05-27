package com.ptsmods.morecommands.commands.server.elevated;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.GameMode;

import java.util.Map;

public class GmCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        Map<String, GameMode> modes = ImmutableMap.of(
                "gmc", GameMode.CREATIVE,
                "gms", GameMode.SURVIVAL,
                "gma", GameMode.ADVENTURE,
                "gmsp", GameMode.SPECTATOR
        );

        modes.forEach((literal, gameMode) -> dispatcher.register(literalReqOp(literal)
                .executes(ctx -> ctx.getSource().getServer().getCommandManager().getDispatcher().getRoot().getChild("gamemode").getChild(gameMode.getName()).getCommand().run(ctx))));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/gm";
    }
}
