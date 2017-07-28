package com.ptsmods.morecommands.miscellaneous;

import com.ptsmods.morecommands.commands.fixTime.CommandfixTime;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class ServerEventHandler {

	@SubscribeEvent
	public void onServerTick(ServerTickEvent event) {
		if (CommandfixTime.time != -1 && CommandfixTime.server != null) {
			try {new CommandfixTime().setAllWorldTimes(CommandfixTime.server, CommandfixTime.time);} catch (NullPointerException e) {} // Probably not necessary because it's on server tick and not client tick 
		}																															   // but just for that extra bit of security
	}
	
}
