package com.ptsmods.morecommands.commands.server.elevated;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import java.util.Map;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.level.GameType;

public class GmCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        Map<String, GameType> modes = ImmutableMap.of(
                "gmc", GameType.CREATIVE,
                "gms", GameType.SURVIVAL,
                "gma", GameType.ADVENTURE,
                "gmsp", GameType.SPECTATOR
        );

        modes.forEach((literal, gameMode) -> dispatcher.register(literalReqOp(literal)
                .executes(ctx -> ctx.getSource().getServer().getCommands().getDispatcher().getRoot().getChild("gamemode").getChild(gameMode.getName()).getCommand().run(ctx))));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/gm";
    }
}
