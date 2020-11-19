package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.server.command.ServerCommandSource;

public class BroadcastCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("broadcast").requires(IS_OP).then(argument("msg", StringArgumentType.greedyString()).executes(ctx -> {
            broadcast(ctx.getSource().getMinecraftServer(), translateFormats(ctx.getArgument("msg", String.class)));
            return 1;
        })));
    }
}
