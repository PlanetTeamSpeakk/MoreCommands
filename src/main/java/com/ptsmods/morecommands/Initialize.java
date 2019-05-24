package com.ptsmods.morecommands;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.ptsmods.morecommands.commands.chelp.Commandchelp;
import com.ptsmods.morecommands.commands.enchant.Commandenchant;
import com.ptsmods.morecommands.commands.fixTime.CommandfixTime;
import com.ptsmods.morecommands.miscellaneous.CommandBase;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.IGameRule;
import com.ptsmods.morecommands.miscellaneous.IGameRule.Inject;
import com.ptsmods.morecommands.miscellaneous.KeyBinding;
import com.ptsmods.morecommands.miscellaneous.Reference;
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
		ICommand[] commands = Reference.getCommandRegistry(CommandType.SERVER).toArray(new ICommand[0]);
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
		ICommand[] commands = Lists.asList(new Commandchelp(), Reference.getCommandRegistry(CommandType.CLIENT).toArray(new ICommand[0])).toArray(new ICommand[0]);
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

	public static void setupCommandRegistry() {
		for (Class<? extends CommandBase> commandClass : Reference.getSubTypesOf(CommandBase.class))
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
		List<Class<? extends IGameRule>> clazzes = Reference.getSubTypesOf(IGameRule.class);
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