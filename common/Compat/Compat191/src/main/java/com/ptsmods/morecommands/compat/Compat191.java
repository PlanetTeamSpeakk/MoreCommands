package com.ptsmods.morecommands.compat;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class Compat191 extends Compat19 {

    @Override
    public int performCommand(Commands commands, CommandSourceStack source, String command) {
        return commands.performPrefixedCommand(source, command);
    }
}
