package com.ptsmods.morecommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;
import com.ptsmods.morecommands.commands.chelp.Commandchelp;
import com.ptsmods.morecommands.commands.enchant.Commandenchant;
import com.ptsmods.morecommands.commands.fixTime.CommandfixTime;
import com.ptsmods.morecommands.miscellaneous.CommandBase;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.KeyBinding;
import com.ptsmods.morecommands.miscellaneous.Reference;
import com.ptsmods.morecommands.miscellaneous.Reference.LogType;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;
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

	@SideOnly(Side.CLIENT)
	public static void registerKeyBinds() {
		HashMap<String, KeyBinding> keyBindings = Reference.getKeyBindings();
		for (String keyBinding : keyBindings.keySet())
			ClientRegistry.registerKeyBinding(keyBindings.get(keyBinding));

	}

}