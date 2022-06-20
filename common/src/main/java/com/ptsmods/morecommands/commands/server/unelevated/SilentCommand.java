package com.ptsmods.morecommands.commands.server.unelevated;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Collection;

public class SilentCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) throws Exception {}

    @Override
    public void init(boolean serverOnly, MinecraftServer server) throws Exception {
        // Dispatcher passed to the register method is a temporary empty dispatcher.
        CommandDispatcher<ServerCommandSource> dispatcher = server.getCommandManager().getDispatcher();
        MoreCommands.removeNode(dispatcher, dispatcher.getRoot().getChild("silent")); // Remove old node as merging only merges the command, not the redirects.

        dispatcher.register(literal("silent").redirect(dispatcher.getRoot(), ctx -> ctx.getSource().withSilent()));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/silent";
    }

    @Override
    public Collection<String> getRegisteredNodes() {
        return ImmutableList.of("silent");
    }
}
