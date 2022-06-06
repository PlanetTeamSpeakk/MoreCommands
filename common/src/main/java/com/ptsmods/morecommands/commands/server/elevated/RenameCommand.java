package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;

public class RenameCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literalReqOp("rename")
                .then(argument("name", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            ItemStack stack = ctx.getSource().getPlayerOrThrow().getMainHandStack();
                            if (stack.isEmpty()) sendError(ctx, "You are not holding an item.");
                            else {
                                String nameRaw = ctx.getArgument("name", String.class);
                                String name = MoreGameRules.get().checkBooleanWithPerm(ctx.getSource().getWorld().getGameRules(),
                                        MoreGameRules.get().doItemColoursRule(), ctx.getSource().getEntity()) ?
                                        MoreCommands.translateFormattings(nameRaw) : nameRaw;
                                stack.setCustomName(literalText(name).build());
                                sendMsg(ctx, "The item has been renamed to " + name + Formatting.RESET + ".");
                                return 1;
                            }
                            return 0;
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/rename";
    }
}
