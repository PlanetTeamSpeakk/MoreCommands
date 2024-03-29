package com.ptsmods.morecommands.commands.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;

public class BroadcastCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReqOp("broadcast")
                .then(argument("msg", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            broadcast(ctx.getSource().getServer(), Util.translateFormats(ctx.getArgument("msg", String.class)));
                            return 1;
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/broadcast";
    }
}
