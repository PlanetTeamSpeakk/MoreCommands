package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

public class HealCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.getRoot().addChild(MoreCommands.createAlias("feed", dispatcher.register(literal("heal").requires(IS_OP).executes(ctx -> {
            PlayerEntity player = ctx.getSource().getPlayer();
            player.setHealth(player.getMaxHealth());
            player.getHungerManager().add(20, 20);
            sendMsg(ctx, "You have been healed and fed.");
            return 1;
        }))));
    }
}
