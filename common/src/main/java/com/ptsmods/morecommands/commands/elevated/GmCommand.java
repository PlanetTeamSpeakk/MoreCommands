package com.ptsmods.morecommands.commands.elevated;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.mixin.common.accessor.MixinGameModeCommandAccessor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.level.GameType;

import java.util.List;
import java.util.Map;

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
                .executes(ctx -> MixinGameModeCommandAccessor.callSetMode(ctx, List.of(ctx.getSource().getPlayerOrException()), gameMode))));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/gm";
    }
}
