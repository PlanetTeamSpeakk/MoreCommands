package com.ptsmods.morecommands;

import com.ptsmods.morecommands.commands.ascend.Commandascend;
import com.ptsmods.morecommands.commands.barrier.Commandbarrier;
import com.ptsmods.morecommands.commands.breakBlock.Commandbreak;
import com.ptsmods.morecommands.commands.calc.Commandcalc;
import com.ptsmods.morecommands.commands.clearEffects.CommandclearEffects;
import com.ptsmods.morecommands.commands.clearInv.CommandclearInv;
import com.ptsmods.morecommands.commands.cmdBlock.Commandcmdblock;
import com.ptsmods.morecommands.commands.consoleCommand.CommandconsoleCommand;
import com.ptsmods.morecommands.commands.day.Commandday;
import com.ptsmods.morecommands.commands.descend.Commanddescend;
import com.ptsmods.morecommands.commands.easy.Commandeasy;
import com.ptsmods.morecommands.commands.enchant.Commandenchant;
import com.ptsmods.morecommands.commands.evalJavaScript.CommandevalJavaScript;
import com.ptsmods.morecommands.commands.explode.Commandexplode;
import com.ptsmods.morecommands.commands.fireball.Commandfireball;
import com.ptsmods.morecommands.commands.fixTime.CommandfixTime;
import com.ptsmods.morecommands.commands.fly.Commandfly;
import com.ptsmods.morecommands.commands.fullbright.Commandfullbright;
import com.ptsmods.morecommands.commands.gm.Commandgm;
import com.ptsmods.morecommands.commands.gma.Commandgma;
import com.ptsmods.morecommands.commands.gmc.Commandgmc;
import com.ptsmods.morecommands.commands.gms.Commandgms;
import com.ptsmods.morecommands.commands.gmsp.Commandgmsp;
import com.ptsmods.morecommands.commands.god.Commandgod;
import com.ptsmods.morecommands.commands.hard.Commandhard;
import com.ptsmods.morecommands.commands.hat.Commandhat;
import com.ptsmods.morecommands.commands.heal.Commandheal;
import com.ptsmods.morecommands.commands.item.Commanditem;
import com.ptsmods.morecommands.commands.killAll.CommandkillAll;
import com.ptsmods.morecommands.commands.kys.Commandkys;
import com.ptsmods.morecommands.commands.night.Commandnight;
import com.ptsmods.morecommands.commands.noHunger.CommandnoHunger;
import com.ptsmods.morecommands.commands.noRain.CommandnoRain;
import com.ptsmods.morecommands.commands.normal.Commandnormal;
import com.ptsmods.morecommands.commands.opTool.CommandopTool;
import com.ptsmods.morecommands.commands.pastNames.CommandpastNames;
import com.ptsmods.morecommands.commands.peaceful.Commandpeaceful;
import com.ptsmods.morecommands.commands.ping.Commandping;
import com.ptsmods.morecommands.commands.powerTool.CommandpowerTool;
import com.ptsmods.morecommands.commands.ptime.Commandptime;
import com.ptsmods.morecommands.commands.reloadMoreCommands.CommandreloadMoreCommands;
import com.ptsmods.morecommands.commands.rename.Commandrename;
import com.ptsmods.morecommands.commands.repair.Commandrepair;
import com.ptsmods.morecommands.commands.resetNBT.CommandresetNBT;
import com.ptsmods.morecommands.commands.runcmd.Commandruncmd;
import com.ptsmods.morecommands.commands.save.Commandsave;
import com.ptsmods.morecommands.commands.serverStatus.CommandserverStatus;
import com.ptsmods.morecommands.commands.setBuildLimit.CommandsetBuildLimit;
import com.ptsmods.morecommands.commands.setFOV.CommandsetFOV;
import com.ptsmods.morecommands.commands.setRenderDistance.CommandsetRenderDistance;
import com.ptsmods.morecommands.commands.showNBT.CommandshowNBT;
import com.ptsmods.morecommands.commands.showTime.CommandshowTime;
import com.ptsmods.morecommands.commands.smite.Commandsmite;
import com.ptsmods.morecommands.commands.spawn.Commandspawn;
import com.ptsmods.morecommands.commands.spawnClientEntity.CommandspawnClientEntity;
import com.ptsmods.morecommands.commands.speed.Commandspeed;
import com.ptsmods.morecommands.commands.sudo.Commandsudo;
import com.ptsmods.morecommands.commands.superPickaxe.CommandsuperPickaxe;
import com.ptsmods.morecommands.commands.through.Commandthrough;
import com.ptsmods.morecommands.commands.top.Commandtop;
import com.ptsmods.morecommands.commands.tpa.Commandtpa;
import com.ptsmods.morecommands.commands.tpaccept.Commandtpaccept;
import com.ptsmods.morecommands.commands.tpdeny.Commandtpdeny;
import com.ptsmods.morecommands.commands.vanish.Commandvanish;
import com.ptsmods.morecommands.commands.wild.Commandwild;
import com.ptsmods.morecommands.miscellaneous.ClientEventHandler;
import com.ptsmods.morecommands.miscellaneous.Reference;
import com.ptsmods.morecommands.miscellaneous.ServerEventHandler;

import net.minecraft.block.Block;
import net.minecraft.command.ICommand;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Initialize {

	public static void registerCommands(FMLServerStartingEvent event) {
		System.out.println("Registering MoreCommands commands.");
		
		ICommand[] commands = {new Commandday(), new Commandnight(), new Commandexplode(), new CommandkillAll(), new CommandclearInv(), new Commandspeed(), new CommandclearEffects(), new Commandheal(), 
				new Commandgma(), new Commandgms(), new Commandgmc(), new Commandgmsp(), new Commanditem(), new Commandgm(), new Commandhard(), new Commandnormal(), new Commandeasy(), new Commandpeaceful(), 
				new CommandshowTime(), new Commandcmdblock(), new Commandbarrier(), new CommandnoRain(), new Commandkys(), new CommandnoHunger(), new Commandgod(), new Commandsudo(), new CommandsetBuildLimit(), 
				new Commandsave(), new Commandtop(), new CommandconsoleCommand(), new CommandreloadMoreCommands(), new Commandfly(), new Commandtpa(), new Commandtpaccept(), new Commandtpdeny(), new Commandwild(), 
				new Commandspawn(), new Commandbreak(), new Commandsmite(), new Commandthrough(), new Commandping(), new Commandrepair(), new Commandrename(), new Commandhat(), new CommandpowerTool(), 
				new CommandresetNBT(), new CommandshowNBT(), new Commandvanish(), new Commanddescend(), new Commandascend(), new CommandopTool(), new CommandsuperPickaxe(), new Commandenchant(),
				new CommandfixTime(), new Commandfireball()};
		
		Integer counter = 0;
		Integer failed = 0;
		
		for (int x = 0; x < commands.length; x += 1) {
			try {
				event.registerServerCommand(commands[x]);
			} catch (Exception e) {failed += 1; continue;}
			counter += 1;
		}
		
		System.out.println("Successfully registered " + counter.toString() + " MoreCommands commands and failed to register " + failed.toString() + " commands.");
		System.out.println("Setting up the MoreCommands block blacklist.");
		
		Block[] blacklist = {Blocks.AIR, Blocks.LAVA, Blocks.CACTUS, Blocks.MAGMA, Blocks.ACACIA_FENCE, Blocks.ACACIA_FENCE_GATE, Blocks.BIRCH_FENCE, Blocks.BIRCH_FENCE_GATE, Blocks.DARK_OAK_FENCE, Blocks.DARK_OAK_FENCE_GATE,
				Blocks.JUNGLE_FENCE, Blocks.JUNGLE_FENCE_GATE, Blocks.NETHER_BRICK_FENCE, Blocks.OAK_FENCE, Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE, Blocks.SPRUCE_FENCE_GATE, Blocks.FIRE, Blocks.WEB, Blocks.MOB_SPAWNER,
				Blocks.END_PORTAL, Blocks.END_PORTAL_FRAME, Blocks.TNT, Blocks.IRON_TRAPDOOR, Blocks.TRAPDOOR, Blocks.BREWING_STAND};

		counter = 0;
		
		for (int x = 0; x < blacklist.length; x += 1) {
			Reference.addBlockToBlacklist(blacklist[x]);
			counter += 1;
		}
		
		MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
		
		System.out.println("Successfully set up the block blacklist and added " + counter.toString() + " blocks.");
		System.out.println("Setting up the MoreCommands block whitelist.");
		
		Block[] whitelist = {Blocks.AIR, Blocks.DEADBUSH, Blocks.VINE, Blocks.TALLGRASS, Blocks.ACACIA_DOOR, Blocks.BIRCH_DOOR, Blocks.DARK_OAK_DOOR, Blocks.IRON_DOOR, Blocks.JUNGLE_DOOR, Blocks.OAK_DOOR, Blocks.SPRUCE_DOOR,
				Blocks.DOUBLE_PLANT, Blocks.RED_FLOWER, Blocks.YELLOW_FLOWER, Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM, Blocks.WATERLILY, Blocks.BEETROOTS, Blocks.CARROTS, Blocks.WHEAT, Blocks.POTATOES, Blocks.PUMPKIN_STEM,
				Blocks.MELON_STEM, Blocks.SNOW_LAYER};
		
		counter = 0;
		
		for (int x = 0; x < whitelist.length; x += 1) {
			Reference.addBlockToWhitelist(whitelist[x]);
			counter += 1;
		}
		
		System.out.println("Successfully set up the block whitelist and added " + counter.toString() + " blocks.");
	}
	
	@SideOnly(Side.CLIENT)
	public static void registerClientCommands() {
		System.out.println("Registering MoreCommands client sided commands.");
		ICommand[] commands = {new CommandspawnClientEntity(), new Commandfullbright(), new Commandptime(), new Commandruncmd(), new CommandsetFOV(), new CommandsetRenderDistance(), new Commandcalc(),
				new CommandserverStatus(), new CommandpastNames(), new CommandevalJavaScript()};

		Integer counter = 0;
		Integer fails = 0;
		
		for (int x = 0; x < commands.length; x += 1) {
			try {
				ClientCommandHandler.instance.registerCommand(commands[x]);
				counter += 1;
			} catch (Exception e) {fails += 1; continue;}
		}
		
		System.out.println("Successfully registered " + counter.toString() + " client sided commands, with " + fails.toString() + " fails.");
		
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
	}
	
}
