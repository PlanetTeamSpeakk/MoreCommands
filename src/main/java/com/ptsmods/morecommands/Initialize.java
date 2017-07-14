package com.ptsmods.morecommands;

import com.ptsmods.morecommands.commands.barrier.Commandbarrier;
import com.ptsmods.morecommands.commands.clearEffects.CommandclearEffects;
import com.ptsmods.morecommands.commands.clearInv.CommandclearInv;
import com.ptsmods.morecommands.commands.cmdBlock.Commandcmdblock;
import com.ptsmods.morecommands.commands.consoleCommand.CommandconsoleCommand;
import com.ptsmods.morecommands.commands.day.Commandday;
import com.ptsmods.morecommands.commands.easy.Commandeasy;
import com.ptsmods.morecommands.commands.explode.Commandexplode;
import com.ptsmods.morecommands.commands.fly.Commandfly;
import com.ptsmods.morecommands.commands.fullbright.Commandfullbright;
import com.ptsmods.morecommands.commands.gm.Commandgm;
import com.ptsmods.morecommands.commands.gma.Commandgma;
import com.ptsmods.morecommands.commands.gmc.Commandgmc;
import com.ptsmods.morecommands.commands.gms.Commandgms;
import com.ptsmods.morecommands.commands.gmsp.Commandgmsp;
import com.ptsmods.morecommands.commands.god.Commandgod;
import com.ptsmods.morecommands.commands.hard.Commandhard;
import com.ptsmods.morecommands.commands.heal.Commandheal;
import com.ptsmods.morecommands.commands.item.Commanditem;
import com.ptsmods.morecommands.commands.killAll.CommandkillAll;
import com.ptsmods.morecommands.commands.kys.Commandkys;
import com.ptsmods.morecommands.commands.night.Commandnight;
import com.ptsmods.morecommands.commands.noHunger.CommandnoHunger;
import com.ptsmods.morecommands.commands.noRain.CommandnoRain;
import com.ptsmods.morecommands.commands.normal.Commandnormal;
import com.ptsmods.morecommands.commands.peaceful.Commandpeaceful;
import com.ptsmods.morecommands.commands.reloadMoreCommands.CommandreloadMoreCommands;
import com.ptsmods.morecommands.commands.save.Commandsave;
import com.ptsmods.morecommands.commands.setBuildLimit.CommandsetBuildLimit;
import com.ptsmods.morecommands.commands.showTime.CommandshowTime;
import com.ptsmods.morecommands.commands.spawnClientEntity.CommandspawnClientEntity;
import com.ptsmods.morecommands.commands.speed.Commandspeed;
import com.ptsmods.morecommands.commands.sudo.Commandsudo;
import com.ptsmods.morecommands.commands.top;
import com.ptsmods.morecommands.commands.top.Commandtop;

import net.minecraft.init.Blocks;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class Initialize {

	public static void register(FMLServerStartingEvent event) {
		System.out.println("Registering MoreCommands commands...");
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
		event.registerServerCommand(new Commandhard());
		event.registerServerCommand(new Commandnormal());
		event.registerServerCommand(new Commandeasy());
		event.registerServerCommand(new Commandpeaceful());
		event.registerServerCommand(new CommandshowTime());
		event.registerServerCommand(new Commandcmdblock());
		event.registerServerCommand(new Commandbarrier());
		event.registerServerCommand(new CommandnoRain());
		event.registerServerCommand(new Commandkys());
		event.registerServerCommand(new CommandnoHunger());
		event.registerServerCommand(new Commandgod());
		event.registerServerCommand(new CommandspawnClientEntity());
		event.registerServerCommand(new Commandsudo());
		event.registerServerCommand(new CommandsetBuildLimit());
		event.registerServerCommand(new Commandsave());
		event.registerServerCommand(new Commandtop());
		event.registerServerCommand(new CommandconsoleCommand());
		event.registerServerCommand(new CommandreloadMoreCommands());
		event.registerServerCommand(new Commandfly());
		ClientCommandHandler.instance.registerCommand(new Commandfullbright());
		top.addBlockToBlacklist(Blocks.AIR);
		top.addBlockToBlacklist(Blocks.LAVA);
		top.addBlockToBlacklist(Blocks.CACTUS);
		top.addBlockToBlacklist(Blocks.MAGMA);
		top.addBlockToBlacklist(Blocks.ACACIA_FENCE);
		top.addBlockToBlacklist(Blocks.ACACIA_FENCE_GATE);
		top.addBlockToBlacklist(Blocks.BIRCH_FENCE);
		top.addBlockToBlacklist(Blocks.BIRCH_FENCE_GATE);
		top.addBlockToBlacklist(Blocks.DARK_OAK_FENCE);
		top.addBlockToBlacklist(Blocks.DARK_OAK_FENCE_GATE);
		top.addBlockToBlacklist(Blocks.JUNGLE_FENCE);
		top.addBlockToBlacklist(Blocks.JUNGLE_FENCE_GATE);
		top.addBlockToBlacklist(Blocks.NETHER_BRICK_FENCE);
		top.addBlockToBlacklist(Blocks.OAK_FENCE);
		top.addBlockToBlacklist(Blocks.OAK_FENCE_GATE);
		top.addBlockToBlacklist(Blocks.SPRUCE_FENCE);
		top.addBlockToBlacklist(Blocks.SPRUCE_FENCE_GATE);
		top.addBlockToBlacklist(Blocks.FIRE);
		top.addBlockToBlacklist(Blocks.WEB);
		top.addBlockToBlacklist(Blocks.MOB_SPAWNER);
		top.addBlockToBlacklist(Blocks.END_PORTAL);
		top.addBlockToBlacklist(Blocks.TNT);
		top.addBlockToBlacklist(Blocks.IRON_TRAPDOOR);
		top.addBlockToBlacklist(Blocks.TRAPDOOR);
		top.addBlockToBlacklist(Blocks.BREWING_STAND);
		
		top.addBlockToWhitelist(Blocks.AIR);
		top.addBlockToWhitelist(Blocks.DEADBUSH);
		top.addBlockToWhitelist(Blocks.VINE);
		top.addBlockToWhitelist(Blocks.TALLGRASS);
		top.addBlockToWhitelist(Blocks.ACACIA_DOOR);
		top.addBlockToWhitelist(Blocks.BIRCH_DOOR);
		top.addBlockToWhitelist(Blocks.DARK_OAK_DOOR);
		top.addBlockToWhitelist(Blocks.IRON_DOOR);
		top.addBlockToWhitelist(Blocks.JUNGLE_DOOR);
		top.addBlockToWhitelist(Blocks.OAK_DOOR);
		top.addBlockToWhitelist(Blocks.SPRUCE_DOOR);
		top.addBlockToWhitelist(Blocks.DOUBLE_PLANT);
		top.addBlockToWhitelist(Blocks.RED_FLOWER);
		top.addBlockToWhitelist(Blocks.YELLOW_FLOWER);
		top.addBlockToWhitelist(Blocks.BROWN_MUSHROOM);
		top.addBlockToWhitelist(Blocks.RED_MUSHROOM);
		top.addBlockToWhitelist(Blocks.WATERLILY);
		System.out.println("MoreCommands commands have successfully been registered.");
	}
	
}
