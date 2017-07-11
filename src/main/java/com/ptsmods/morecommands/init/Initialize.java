package com.ptsmods.morecommands.init;

import com.ptsmods.morecommands.commands.clearEffects.CommandclearEffects;
import com.ptsmods.morecommands.commands.clearInv.CommandclearInv;
import com.ptsmods.morecommands.commands.day.Commandday;
import com.ptsmods.morecommands.commands.explode.Commandexplode;
import com.ptsmods.morecommands.commands.fullbright.Commandfullbright;
import com.ptsmods.morecommands.commands.gm.Commandgm;
import com.ptsmods.morecommands.commands.gma.Commandgma;
import com.ptsmods.morecommands.commands.gmc.Commandgmc;
import com.ptsmods.morecommands.commands.gms.Commandgms;
import com.ptsmods.morecommands.commands.gmsp.Commandgmsp;
import com.ptsmods.morecommands.commands.heal.Commandheal;
import com.ptsmods.morecommands.commands.item.Commanditem;
import com.ptsmods.morecommands.commands.killAll.CommandkillAll;
import com.ptsmods.morecommands.commands.night.Commandnight;
import com.ptsmods.morecommands.commands.speed.Commandspeed;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class Initialize {

	public static void register(FMLServerStartingEvent event) {
		System.out.println("Registering MoreCommands commands...");
		event.registerServerCommand(new Commandfullbright());
		event.registerServerCommand(new Commandday());
		event.registerServerCommand(new Commandnight());
		event.registerServerCommand(new Commandexplode());
		event.registerServerCommand(new CommandkillAll());
		event.registerServerCommand(new CommandclearInv());
		event.registerServerCommand(new Commandspeed());
		event.registerServerCommand(new CommandclearEffects());
		event.registerServerCommand(new Commandheal());
		event.registerServerCommand(new Commandgma());
		event.registerServerCommand(new Commandgms());
		event.registerServerCommand(new Commandgmc());
		event.registerServerCommand(new Commandgmsp());
		event.registerServerCommand(new Commanditem());
		event.registerServerCommand(new Commandgm());
		System.out.println("MoreCommands commands have successfully been registered.");
	}
	
}
