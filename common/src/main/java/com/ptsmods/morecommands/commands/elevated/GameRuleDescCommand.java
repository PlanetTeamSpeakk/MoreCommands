package com.ptsmods.morecommands.commands.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.level.GameRules;

public class GameRuleDescCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) throws Exception {
        if (IMoreCommands.get().isServerOnly()) return; // No lang files on client-side in server-only mode.
        LiteralArgumentBuilder<CommandSourceStack> gamerule = literal("gamerule");
        GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
            public <T extends GameRules.Value<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
                gamerule.then(literal(key.getId())
                        .then(literal("desc")
                                .executes(ctx -> {
                                    sendMsg(ctx, literalText("Description for gamerule " + key.getId() + ": ").append(translatableText(key.getDescriptionId() + ".description")));
                                    return 1;
                                })));
            }
        });
        dispatcher.register(gamerule);
    }

    @Override
    public String getDocsPath() {
        return "/elevated/gamerule-desc";
    }
}
