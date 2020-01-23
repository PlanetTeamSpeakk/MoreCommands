package com.ptsmods.morecommands;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.ptsmods.morecommands.commands.alias.Commandalias;
import com.ptsmods.morecommands.commands.ascend.Commandascend;
import com.ptsmods.morecommands.commands.barrier.Commandbarrier;
import com.ptsmods.morecommands.commands.bind.Commandbind;
import com.ptsmods.morecommands.commands.biomeTeleport.CommandbiomeTeleport;
import com.ptsmods.morecommands.commands.breakBlock.Commandbreak;
import com.ptsmods.morecommands.commands.broadcast.Commandbroadcast;
import com.ptsmods.morecommands.commands.calc.Commandcalc;
import com.ptsmods.morecommands.commands.cannon.Commandcannon;
import com.ptsmods.morecommands.commands.chelp.Commandchelp;
import com.ptsmods.morecommands.commands.clearEffects.CommandclearEffects;
import com.ptsmods.morecommands.commands.clearInv.CommandclearInv;
import com.ptsmods.morecommands.commands.clientScoreboard.CommandclientScoreboard;
import com.ptsmods.morecommands.commands.clone.Commandclone;
import com.ptsmods.morecommands.commands.cmdBlock.Commandcmdblock;
import com.ptsmods.morecommands.commands.compareIPs.CommandcompareIPs;
import com.ptsmods.morecommands.commands.consoleCommand.CommandconsoleCommand;
import com.ptsmods.morecommands.commands.copy.Commandcopy;
import com.ptsmods.morecommands.commands.crash.Commandcrash;
import com.ptsmods.morecommands.commands.ctopevents.Commandctopevents;
import com.ptsmods.morecommands.commands.curConvert.CommandcurConvert;
import com.ptsmods.morecommands.commands.cyclePainting.CommandcyclePainting;
import com.ptsmods.morecommands.commands.day.Commandday;
import com.ptsmods.morecommands.commands.defuse.Commanddefuse;
import com.ptsmods.morecommands.commands.delHome.CommanddelHome;
import com.ptsmods.morecommands.commands.delWarp.CommanddelWarp;
import com.ptsmods.morecommands.commands.descend.Commanddescend;
import com.ptsmods.morecommands.commands.dimension.Commanddimension;
import com.ptsmods.morecommands.commands.download.Commanddownload;
import com.ptsmods.morecommands.commands.dropstore.Commanddropstore;
import com.ptsmods.morecommands.commands.easy.Commandeasy;
import com.ptsmods.morecommands.commands.enchant.Commandenchant;
import com.ptsmods.morecommands.commands.enderChest.CommandenderChest;
import com.ptsmods.morecommands.commands.evalJavaScript.CommandevalJavaScript;
import com.ptsmods.morecommands.commands.explode.Commandexplode;
import com.ptsmods.morecommands.commands.extinguish.Commandextinguish;
import com.ptsmods.morecommands.commands.fakePlayer.CommandfakePlayer;
import com.ptsmods.morecommands.commands.findEntity.CommandfindEntity;
import com.ptsmods.morecommands.commands.fireball.Commandfireball;
import com.ptsmods.morecommands.commands.fixTime.CommandfixTime;
import com.ptsmods.morecommands.commands.fly.Commandfly;
import com.ptsmods.morecommands.commands.fps.Commandfps;
import com.ptsmods.morecommands.commands.fromBinary.CommandfromBinary;
import com.ptsmods.morecommands.commands.fullbright.Commandfullbright;
import com.ptsmods.morecommands.commands.genloot.Commandgenloot;
import com.ptsmods.morecommands.commands.gm.Commandgm;
import com.ptsmods.morecommands.commands.gma.Commandgma;
import com.ptsmods.morecommands.commands.gmc.Commandgmc;
import com.ptsmods.morecommands.commands.gms.Commandgms;
import com.ptsmods.morecommands.commands.gmsp.Commandgmsp;
import com.ptsmods.morecommands.commands.god.Commandgod;
import com.ptsmods.morecommands.commands.hard.Commandhard;
import com.ptsmods.morecommands.commands.hat.Commandhat;
import com.ptsmods.morecommands.commands.heal.Commandheal;
import com.ptsmods.morecommands.commands.home.Commandhome;
import com.ptsmods.morecommands.commands.inspect.Commandinspect;
import com.ptsmods.morecommands.commands.invsee.Commandinvsee;
import com.ptsmods.morecommands.commands.item.Commanditem;
import com.ptsmods.morecommands.commands.jump.Commandjump;
import com.ptsmods.morecommands.commands.killAll.CommandkillAll;
import com.ptsmods.morecommands.commands.kys.Commandkys;
import com.ptsmods.morecommands.commands.macro.Commandmacro;
import com.ptsmods.morecommands.commands.makeUnbreakable.CommandmakeUnbreakable;
import com.ptsmods.morecommands.commands.more.Commandmore;
import com.ptsmods.morecommands.commands.moreCommandsPermissions.CommandmoreCommandsPermissions;
import com.ptsmods.morecommands.commands.morecommandsInfo.CommandmorecommandsInfo;
import com.ptsmods.morecommands.commands.narrate.Commandnarrate;
import com.ptsmods.morecommands.commands.night.Commandnight;
import com.ptsmods.morecommands.commands.noHunger.CommandnoHunger;
import com.ptsmods.morecommands.commands.noRain.CommandnoRain;
import com.ptsmods.morecommands.commands.normal.Commandnormal;
import com.ptsmods.morecommands.commands.opTool.CommandopTool;
import com.ptsmods.morecommands.commands.pastNames.CommandpastNames;
import com.ptsmods.morecommands.commands.peaceful.Commandpeaceful;
import com.ptsmods.morecommands.commands.ping.Commandping;
import com.ptsmods.morecommands.commands.potion.Commandpotion;
import com.ptsmods.morecommands.commands.powerTool.CommandpowerTool;
import com.ptsmods.morecommands.commands.ptime.Commandptime;
import com.ptsmods.morecommands.commands.reach.Commandreach;
import com.ptsmods.morecommands.commands.recipe.Commandrecipe;
import com.ptsmods.morecommands.commands.reloadMoreCommands.CommandreloadMoreCommands;
import com.ptsmods.morecommands.commands.rename.Commandrename;
import com.ptsmods.morecommands.commands.repair.Commandrepair;
import com.ptsmods.morecommands.commands.resetNBT.CommandresetNBT;
import com.ptsmods.morecommands.commands.runcmd.Commandruncmd;
import com.ptsmods.morecommands.commands.save.Commandsave;
import com.ptsmods.morecommands.commands.screenshot.Commandscreenshot;
import com.ptsmods.morecommands.commands.serverStatus.CommandserverStatus;
import com.ptsmods.morecommands.commands.setBuildLimit.CommandsetBuildLimit;
import com.ptsmods.morecommands.commands.setFOV.CommandsetFOV;
import com.ptsmods.morecommands.commands.setHome.CommandsetHome;
import com.ptsmods.morecommands.commands.setRenderDistance.CommandsetRenderDistance;
import com.ptsmods.morecommands.commands.setWarp.CommandsetWarp;
import com.ptsmods.morecommands.commands.showNBT.CommandshowNBT;
import com.ptsmods.morecommands.commands.showTime.CommandshowTime;
import com.ptsmods.morecommands.commands.silence.Commandsilence;
import com.ptsmods.morecommands.commands.silenceClient.CommandsilenceClient;
import com.ptsmods.morecommands.commands.skull.Commandskull;
import com.ptsmods.morecommands.commands.smite.Commandsmite;
import com.ptsmods.morecommands.commands.spawn.Commandspawn;
import com.ptsmods.morecommands.commands.spawnClientEntity.CommandspawnClientEntity;
import com.ptsmods.morecommands.commands.spawnmob.CommandspawnMob;
import com.ptsmods.morecommands.commands.speed.Commandspeed;
import com.ptsmods.morecommands.commands.structure.Commandstructure;
import com.ptsmods.morecommands.commands.sudo.Commandsudo;
import com.ptsmods.morecommands.commands.superPickaxe.CommandsuperPickaxe;
import com.ptsmods.morecommands.commands.sysinfo.Commandsysinfo;
import com.ptsmods.morecommands.commands.through.Commandthrough;
import com.ptsmods.morecommands.commands.timeIn.CommandtimeIn;
import com.ptsmods.morecommands.commands.toBinary.CommandtoBinary;
import com.ptsmods.morecommands.commands.toggleCheats.CommandtoggleCheats;
import com.ptsmods.morecommands.commands.toggleOverlay.CommandtoggleOverlay;
import com.ptsmods.morecommands.commands.top.Commandtop;
import com.ptsmods.morecommands.commands.topevents.Commandtopevents;
import com.ptsmods.morecommands.commands.tpChunk.CommandtpChunk;
import com.ptsmods.morecommands.commands.tpa.Commandtpa;
import com.ptsmods.morecommands.commands.tpaccept.Commandtpaccept;
import com.ptsmods.morecommands.commands.tpdeny.Commandtpdeny;
import com.ptsmods.morecommands.commands.tps.Commandtps;
import com.ptsmods.morecommands.commands.tree.Commandtree;
import com.ptsmods.morecommands.commands.urban.Commandurban;
import com.ptsmods.morecommands.commands.vanish.Commandvanish;
import com.ptsmods.morecommands.commands.village.Commandvillage;
import com.ptsmods.morecommands.commands.warp.Commandwarp;
import com.ptsmods.morecommands.commands.whereAmI.CommandwhereAmI;
import com.ptsmods.morecommands.commands.whois.Commandwhois;
import com.ptsmods.morecommands.commands.wild.Commandwild;
import com.ptsmods.morecommands.miscellaneous.*;
import com.ptsmods.morecommands.miscellaneous.IGameRule.Inject;
import com.ptsmods.morecommands.miscellaneous.Reference.LogType;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules.ValueType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Initialize {

	private Initialize() {}

	public static void registerCommands(MinecraftServer server) {
		Reference.print(LogType.INFO, "Registering MoreCommands server sided commands.");
		ICommand[] nonRegistryCommands = new ICommand[] {new CommandfixTime(), new Commandenchant()};
		List<ICommand> commands = Reference.getCommandRegistry(CommandType.SERVER);
		Integer counter = 0;
		Integer fails = 0;
		List<String> failList = new ArrayList<>();
		for (ICommand command : commands)
			try {
				((CommandHandler) server.getCommandManager()).registerCommand(command);
				MinecraftForge.EVENT_BUS.register(command);
				counter += 1;
			} catch (Throwable e) {
				fails += 1;
				failList.add(command.getName());
				e.printStackTrace();
				continue;
			}
		for (ICommand command : nonRegistryCommands)
			try {
				((CommandHandler) server.getCommandManager()).registerCommand(command);
				MinecraftForge.EVENT_BUS.register(command);
				counter += 1;
			} catch (Throwable e) {
				fails += 1;
				failList.add(command.getName());
				e.printStackTrace();
				continue;
			}
		Reference.print(LogType.INFO, "Successfully registered " + counter.toString() + " server sided commands, with " + fails.toString() + " fails.");
		if (failList.size() != 0) Reference.print(LogType.INFO, "Failed to register " + net.minecraft.command.CommandBase.joinNiceString(failList.toArray(new String[0])));
	}

	@SideOnly(Side.CLIENT)
	public static void registerClientCommands() {
		Reference.print(LogType.INFO, "Registering MoreCommands client sided commands.");
		List<ICommand> commands = Lists.asList(new Commandchelp(), Reference.getCommandRegistry(CommandType.CLIENT).toArray(new ICommand[0]));
		Integer counter = 0;
		Integer fails = 0;
		List<String> failList = new ArrayList<>();
		for (ICommand command : commands)
			try {
				ClientCommandHandler.instance.registerCommand(command);
				MinecraftForge.EVENT_BUS.register(command);
				counter += 1;
			} catch (Throwable e) {
				fails += 1;
				failList.add(command.getName());
				e.printStackTrace();
				continue;
			}
		Reference.print(LogType.INFO, "Successfully registered " + counter.toString() + " client sided commands, with " + fails.toString() + " fails.");
		if (!(failList.size() == 0)) Reference.print(LogType.WARN, "Failed to register " + net.minecraft.command.CommandBase.joinNiceString(failList.toArray(new String[0])));
	}

	private static final boolean useReflections() {
		return false; // Literally breaks when using almost any mod alongside MoreCommands.
						// Reflections is the reason why commands rarely were registered.
	}

	public static void setupCommandRegistry() {
		List<Class<? extends CommandBase>> commandClasses = useReflections() ? Reference.getSubTypesOf(CommandBase.class)
				: Lists.newArrayList(
						Commandalias.class,
						Commandascend.class,
						Commandbarrier.class,
						Commandbind.class,
						CommandbiomeTeleport.class,
						Commandbreak.class,
						Commandbroadcast.class,
						Commandcalc.class,
						Commandcannon.class,
						CommandclearEffects.class,
						CommandclearInv.class,
						CommandclientScoreboard.class,
						Commandclone.class,
						Commandcmdblock.class,
						CommandcompareIPs.class,
						CommandconsoleCommand.class,
						Commandcopy.class,
						Commandcrash.class,
						Commandctopevents.class,
						CommandcurConvert.class,
						CommandcyclePainting.class,
						Commandday.class,
						Commanddefuse.class,
						CommanddelHome.class,
						CommanddelWarp.class,
						Commanddescend.class,
						Commanddimension.class,
						Commanddownload.class,
						Commanddropstore.class,
						Commandeasy.class,
						CommandenderChest.class,
						CommandevalJavaScript.class,
						Commandexplode.class,
						Commandextinguish.class,
						CommandfakePlayer.class,
						CommandfindEntity.class,
						Commandfireball.class,
						Commandfly.class,
						Commandfps.class,
						CommandfromBinary.class,
						Commandfullbright.class,
						Commandgenloot.class,
						Commandgm.class,
						Commandgma.class,
						Commandgmc.class,
						Commandgms.class,
						Commandgmsp.class,
						Commandgod.class,
						Commandhard.class,
						Commandhat.class,
						Commandheal.class,
						Commandhome.class,
						Commandinspect.class,
						Commandinvsee.class,
						Commanditem.class,
						Commandjump.class,
						CommandkillAll.class,
						Commandkys.class,
						Commandmacro.class,
						CommandmakeUnbreakable.class,
						Commandmore.class,
						CommandmorecommandsInfo.class,
						CommandmoreCommandsPermissions.class,
						Commandnarrate.class,
						Commandnight.class,
						CommandnoHunger.class,
						CommandnoRain.class,
						Commandnormal.class,
						CommandopTool.class,
						CommandpastNames.class,
						Commandpeaceful.class,
						Commandping.class,
						Commandpotion.class,
						CommandpowerTool.class,
						Commandptime.class,
						Commandreach.class,
						Commandrecipe.class,
						// Commandrecipes.class, // GUI only registers some clicks, most clicks don't
						// even go through to the handleInput method. No clue why.
						CommandreloadMoreCommands.class,
						Commandrename.class,
						Commandrepair.class,
						CommandresetNBT.class,
						Commandruncmd.class,
						Commandsave.class,
						Commandscreenshot.class,
						CommandserverStatus.class,
						CommandsetBuildLimit.class,
						CommandsetFOV.class,
						CommandsetHome.class,
						CommandsetRenderDistance.class,
						CommandsetWarp.class,
						CommandshowNBT.class,
						CommandshowTime.class,
						Commandsilence.class,
						CommandsilenceClient.class,
						Commandskull.class,
						Commandsmite.class,
						Commandspawn.class,
						CommandspawnClientEntity.class,
						CommandspawnMob.class,
						Commandspeed.class,
						Commandstructure.class,
						Commandsudo.class,
						CommandsuperPickaxe.class,
						Commandsysinfo.class,
						Commandthrough.class,
						CommandtimeIn.class,
						CommandtoBinary.class,
						CommandtoggleCheats.class,
						CommandtoggleOverlay.class,
						Commandtop.class,
						Commandtopevents.class,
						Commandtpa.class,
						Commandtpaccept.class,
						CommandtpChunk.class,
						Commandtpdeny.class,
						Commandtps.class,
						Commandtree.class,
						Commandurban.class,
						Commandvanish.class,
						Commandvillage.class,
						Commandwarp.class,
						CommandwhereAmI.class,
						Commandwhois.class,
						Commandwild.class);
		for (Class<? extends CommandBase> commandClass : commandClasses)
			try {
				CommandBase command = commandClass.newInstance();
				Reference.addCommandToRegistry(command.getCommandType(), command);
				command.getPermission(); // just so it's registered in the permissions.
				Reference.commands.add(command);
			} catch (Exception | NoClassDefFoundError e) {
				Reference.print(LogType.INFO, "Could not load command of class", commandClass.getName() + ".");
				e.printStackTrace();
			}
	}

	public static void setupGameRules(MinecraftServer server) {
		if (!Reference.gameRules.isEmpty()) return;
		List<Class<? extends IGameRule>> clazzes = useReflections() ? Reference.getSubTypesOf(IGameRule.class)
				: Lists.newArrayList(
						GameRuleCreateLavaSource.class,
						GameRuleDisableEntitySpawning.class,
						GameRuleDisableExplosions.class,
						GameRuleMeltBlocks.class,
						GameRuleNoDownFall.class,
						GameRuleSilkSpawners.class,
						GameRuleWildLimit.class);
		Reference.print(LogType.INFO, "Setting up gamerules...");
		Map<String, Map<IGameRule, List<Field>>> fieldsToInject = new HashMap();
		for (Class clazz : clazzes)
			try {
				IGameRule rule = (IGameRule) clazz.newInstance();
				if (rule.getName() == null || rule.getName().isEmpty()) continue;
				for (Field f : Reference.getFields(clazz))
					if (f.isAnnotationPresent(Inject.class)) {
						Inject inject = f.getAnnotation(Inject.class);
						if (inject.value() == null || inject.value().isEmpty()) {
							if (clazz.isAssignableFrom(f.getType())) {
								if (Modifier.isFinal(f.getModifiers())) Reference.removeFinalModifier(f);
								f.set(Modifier.isStatic(f.getModifiers()) ? null : rule, rule);
							}
						} else {
							if (!fieldsToInject.containsKey(inject.value())) fieldsToInject.put(inject.value(), new HashMap());
							if (!fieldsToInject.get(inject.value()).containsKey(rule)) fieldsToInject.get(inject.value()).put(rule, new ArrayList());
							fieldsToInject.get(inject.value()).get(rule).add(f);
						}
					}
				for (WorldServer world : server.worlds) {
					if (!world.getGameRules().hasRule(rule.getName())) {
						Reference.print(LogType.INFO, "Gamerule", rule.getName(), "has been created for world", world.getWorldInfo().getWorldName() + ".");
						rule.onCreateWorld(server, world);
						world.getGameRules().addGameRule(rule.getName(), "" + rule.getDefaultValue(), rule.getType());
					}
					rule.initWorld(server, world, rule.getType() == ValueType.NUMERICAL_VALUE ? world.getGameRules().getInt(rule.getName()) : rule.getType() == ValueType.BOOLEAN_VALUE ? world.getGameRules().getBoolean(rule.getName()) : world.getGameRules().getString(rule.getName()));
				}
				rule.initServer(server);
				MinecraftForge.EVENT_BUS.register(rule);
				Reference.gameRules.add(rule);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		try {
			for (Entry<String, Map<IGameRule, List<Field>>> entry : fieldsToInject.entrySet()) {
				IGameRule rule = Reference.getGameRule(entry.getKey());
				if (rule != null) for (Entry<IGameRule, List<Field>> entry0 : entry.getValue().entrySet())
					for (Field f1 : entry0.getValue()) {
						f1.setAccessible(true);
						if (Modifier.isFinal(f1.getModifiers())) Reference.removeFinalModifier(f1);
						f1.set(Modifier.isStatic(f1.getModifiers()) ? null : entry0.getKey(), rule);
					}
			}
			Reference.print(LogType.INFO, "Successfully registered and initialised", Reference.gameRules.size(), "gamerules.");
		} catch (Exception e) {
			e.printStackTrace();
			Reference.print(LogType.ERROR, "Something went wrong while setting up the gamerules.");
		}
	}

	@SideOnly(Side.CLIENT)
	public static void registerKeyBinds() {
		Map<String, KeyBinding> keyBindings = Reference.getKeyBindings();
		for (String keyBinding : keyBindings.keySet())
			ClientRegistry.registerKeyBinding(keyBindings.get(keyBinding));

	}

}