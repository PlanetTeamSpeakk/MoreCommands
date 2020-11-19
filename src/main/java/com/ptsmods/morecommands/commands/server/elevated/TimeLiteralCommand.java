package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.server.command.ServerCommandSource;

// Smallest command class in the whole mod? :O
public class TimeLiteralCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.getRoot().addChild(dispatcher.getRoot().getChild("time").getChild("set").getChild("day"));
        dispatcher.getRoot().addChild(dispatcher.getRoot().getChild("time").getChild("set").getChild("night"));
    }
}
