package com.ptsmods.morecommands;

import com.ptsmods.morecommands.commands.barrier.Commandbarrier;
import com.ptsmods.morecommands.commands.breakBlock.Commandbreak;
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
import com.ptsmods.morecommands.commands.ping.Commandping;
import com.ptsmods.morecommands.commands.ptime.Commandptime;
import com.ptsmods.morecommands.commands.reloadMoreCommands.CommandreloadMoreCommands;
import com.ptsmods.morecommands.commands.save.Commandsave;
import com.ptsmods.morecommands.commands.setBuildLimit.CommandsetBuildLimit;
import com.ptsmods.morecommands.commands.showTime.CommandshowTime;
import com.ptsmods.morecommands.commands.smite.Commandsmite;
import com.ptsmods.morecommands.commands.spawn.Commandspawn;
import com.ptsmods.morecommands.commands.spawnClientEntity.CommandspawnClientEntity;
import com.ptsmods.morecommands.commands.speed.Commandspeed;
import com.ptsmods.morecommands.commands.sudo.Commandsudo;
import com.ptsmods.morecommands.commands.through.Commandthrough;
import com.ptsmods.morecommands.commands.top.Commandtop;
import com.ptsmods.morecommands.commands.tpa.Commandtpa;
import com.ptsmods.morecommands.commands.tpaccept.Commandtpaccept;
import com.ptsmods.morecommands.commands.tpdeny.Commandtpdeny;
import com.ptsmods.morecommands.commands.wild.Commandwild;

import net.minecraft.init.Blocks;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Initialize {

	public static void registerCommands(FMLServerStartingEvent event) {
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
		event.registerServerCommand(new Commandsudo());
		event.registerServerCommand(new CommandsetBuildLimit());
		event.registerServerCommand(new Commandsave());
		event.registerServerCommand(new Commandtop());
		event.registerServerCommand(new CommandconsoleCommand());
		event.registerServerCommand(new CommandreloadMoreCommands());
		event.registerServerCommand(new Commandfly());
		event.registerServerCommand(new Commandtpa());
		event.registerServerCommand(new Commandtpaccept());
		event.registerServerCommand(new Commandtpdeny());
		event.registerServerCommand(new Commandwild());
		event.registerServerCommand(new Commandspawn());
		event.registerServerCommand(new Commandbreak());
		event.registerServerCommand(new Commandsmite());
		event.registerServerCommand(new Commandthrough());
		
		Reference.addBlockToBlacklist(Blocks.AIR);
		Reference.addBlockToBlacklist(Blocks.LAVA);
		Reference.addBlockToBlacklist(Blocks.CACTUS);
		Reference.addBlockToBlacklist(Blocks.MAGMA);
		Reference.addBlockToBlacklist(Blocks.ACACIA_FENCE);
		Reference.addBlockToBlacklist(Blocks.ACACIA_FENCE_GATE);
		Reference.addBlockToBlacklist(Blocks.BIRCH_FENCE);
		Reference.addBlockToBlacklist(Blocks.BIRCH_FENCE_GATE);
		Reference.addBlockToBlacklist(Blocks.DARK_OAK_FENCE);
		Reference.addBlockToBlacklist(Blocks.DARK_OAK_FENCE_GATE);
		Reference.addBlockToBlacklist(Blocks.JUNGLE_FENCE);
		Reference.addBlockToBlacklist(Blocks.JUNGLE_FENCE_GATE);
		Reference.addBlockToBlacklist(Blocks.NETHER_BRICK_FENCE);
		Reference.addBlockToBlacklist(Blocks.OAK_FENCE);
		Reference.addBlockToBlacklist(Blocks.OAK_FENCE_GATE);
		Reference.addBlockToBlacklist(Blocks.SPRUCE_FENCE);
		Reference.addBlockToBlacklist(Blocks.SPRUCE_FENCE_GATE);
		Reference.addBlockToBlacklist(Blocks.FIRE);
		Reference.addBlockToBlacklist(Blocks.WEB);
		Reference.addBlockToBlacklist(Blocks.MOB_SPAWNER);
		Reference.addBlockToBlacklist(Blocks.END_PORTAL);
		Reference.addBlockToBlacklist(Blocks.END_PORTAL_FRAME);
		Reference.addBlockToBlacklist(Blocks.TNT);
		Reference.addBlockToBlacklist(Blocks.IRON_TRAPDOOR);
		Reference.addBlockToBlacklist(Blocks.TRAPDOOR);
		Reference.addBlockToBlacklist(Blocks.BREWING_STAND);
		
		Reference.addBlockToWhitelist(Blocks.AIR);
		Reference.addBlockToWhitelist(Blocks.DEADBUSH);
		Reference.addBlockToWhitelist(Blocks.VINE);
		Reference.addBlockToWhitelist(Blocks.TALLGRASS);
		Reference.addBlockToWhitelist(Blocks.ACACIA_DOOR);
		Reference.addBlockToWhitelist(Blocks.BIRCH_DOOR);
		Reference.addBlockToWhitelist(Blocks.DARK_OAK_DOOR);
		Reference.addBlockToWhitelist(Blocks.IRON_DOOR);
		Reference.addBlockToWhitelist(Blocks.JUNGLE_DOOR);
		Reference.addBlockToWhitelist(Blocks.OAK_DOOR);
		Reference.addBlockToWhitelist(Blocks.SPRUCE_DOOR);
		Reference.addBlockToWhitelist(Blocks.DOUBLE_PLANT);
		Reference.addBlockToWhitelist(Blocks.RED_FLOWER);
		Reference.addBlockToWhitelist(Blocks.YELLOW_FLOWER);
		Reference.addBlockToWhitelist(Blocks.BROWN_MUSHROOM);
		Reference.addBlockToWhitelist(Blocks.RED_MUSHROOM);
		Reference.addBlockToWhitelist(Blocks.WATERLILY);
		Reference.addBlockToWhitelist(Blocks.BEETROOTS);
		Reference.addBlockToWhitelist(Blocks.CARROTS);
		Reference.addBlockToWhitelist(Blocks.WHEAT);
		Reference.addBlockToWhitelist(Blocks.POTATOES);
		Reference.addBlockToWhitelist(Blocks.PUMPKIN_STEM);
		Reference.addBlockToWhitelist(Blocks.MELON_STEM);
		Reference.addBlockToWhitelist(Blocks.SNOW_LAYER);
		
		System.out.println("MoreCommands commands have successfully been registered.");
	}
	
	@SideOnly(Side.CLIENT)
	public static void registerClientCommands() {
		ClientCommandHandler.instance.registerCommand(new CommandspawnClientEntity());
		ClientCommandHandler.instance.registerCommand(new Commandfullbright());
		ClientCommandHandler.instance.registerCommand(new Commandping());
		ClientCommandHandler.instance.registerCommand(new Commandptime());
		
		MinecraftForge.EVENT_BUS.register(new TickHandler());
	}
	
}
