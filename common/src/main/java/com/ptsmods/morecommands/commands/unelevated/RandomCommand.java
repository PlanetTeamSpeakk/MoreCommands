package com.ptsmods.morecommands.commands.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;

import java.util.Random;

public class RandomCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) throws Exception {
        dispatcher.register(literalReq("random")
                .executes(ctx -> execute(ctx, 0, Long.MAX_VALUE, null))
                .then(argument("max", LongArgumentType.longArg(1))
                        .executes(ctx -> execute(ctx, 0, ctx.getArgument("max", long.class), null)))
                .then(argument("min", LongArgumentType.longArg())
                        .then(argument("max", LongArgumentType.longArg())
                                .executes(ctx -> execute(ctx, ctx.getArgument("min", long.class), ctx.getArgument("max", long.class), null))
                                .then(argument("seed", LongArgumentType.longArg())
                                        .executes(ctx -> execute(ctx, ctx.getArgument("min", long.class),
                                                ctx.getArgument("max", long.class), ctx.getArgument("seed", long.class)))))));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/random";
    }

    private static int execute(CommandContext<CommandSourceStack> ctx, long min, long max, Long seed) {
        if (max <= min) return sendError(ctx, "Max must be greater than min.");

        Random random = new Random();
        if (seed != null) random.setSeed(seed);

        long n = random.nextLong(min, max);
        sendMsg(ctx, "Your number is: %s%d", SF, n);
        return (int) n;
    }
}
