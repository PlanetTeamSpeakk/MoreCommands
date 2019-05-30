package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class ClientCommandEvent extends CommandEvent {

	public ClientCommandEvent(ICommand command, ICommandSender player, String[] parameters) {
		super(command, player, parameters);
	}

}
