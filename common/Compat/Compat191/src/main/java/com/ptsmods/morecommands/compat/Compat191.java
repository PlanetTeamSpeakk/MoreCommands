package com.ptsmods.morecommands.compat;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.Tuple;

public class Compat191 extends Compat19 {

    @Override
    public int performCommand(Commands commands, CommandSourceStack source, String command) {
        return commands.performPrefixedCommand(source, command);
    }

    @Override
    public void broadcast(PlayerList playerManager, Tuple<Integer, ResourceLocation> type, Component message) {
        playerManager.broadcastSystemMessage(message, false);
    }
}
