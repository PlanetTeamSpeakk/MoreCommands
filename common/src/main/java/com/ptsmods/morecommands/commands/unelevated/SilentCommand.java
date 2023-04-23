package com.ptsmods.morecommands.commands.unelevated;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import java.util.Collection;

public class SilentCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) throws Exception {}

    @Override
    public void init(boolean serverOnly, MinecraftServer server) throws Exception {
        // Dispatcher passed to the register method is a temporary empty dispatcher.
        CommandDispatcher<CommandSourceStack> dispatcher = server.getCommands().getDispatcher();
        MoreCommands.removeNode(dispatcher, dispatcher.getRoot().getChild("silent")); // Remove old node as merging only merges the command, not the redirects.

        dispatcher.register(literal("silent").redirect(dispatcher.getRoot(), ctx -> ctx.getSource().withSuppressedOutput()));
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
