package com.ptsmods.morecommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

import com.ptsmods.morecommands.commands.chelp.Commandchelp;
import com.ptsmods.morecommands.commands.enchant.Commandenchant;
import com.ptsmods.morecommands.commands.fixTime.CommandfixTime;
import com.ptsmods.morecommands.miscellaneous.CommandBase;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.IncorrectCommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;
import com.ptsmods.morecommands.miscellaneous.Reference.LogType;

import net.minecraft.block.Block;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ICommand;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class Initialize {

	public static void registerCommands(FMLServerStartingEvent event) {
		Reference.print(LogType.INFO, "Registering MoreCommands server sided commands.");

		ICommand[] nonRegistryCommands = new ICommand[] {new CommandfixTime(), new Commandenchant()};
		ICommand[] commands;
		try {
			commands = Reference.getCommandRegistry(CommandType.SERVER).toArray(new ICommand[0]);
		} catch (IncorrectCommandType e1) {
			e1.printStackTrace();
			return;
		}

		Integer counter = 0;
		Integer fails = 0;
		List<String> failList = new ArrayList<>();

		for (ICommand command : commands)
			try {
				event.registerServerCommand(command);
				counter += 1;
			} catch (Exception e) {fails += 1; failList.add(command.getName()); continue;}

		for (ICommand command : nonRegistryCommands)
			try {
				event.registerServerCommand(command);
				counter += 1;
			} catch (Exception e) {
				fails += 1;
				failList.add(command.getName());
				e.printStackTrace();
				continue;
			}

		Reference.print(LogType.INFO, "Successfully registered " + counter.toString() + " server sided commands, with " + fails.toString() + " fails.");
		if (!(failList.size() == 0)) Reference.print(LogType.INFO, "Failed to register " + CommandBase.joinNiceString(failList.toArray(new String[0])));
	}

	@SideOnly(Side.CLIENT)
	public static void registerClientCommands() {
		Reference.print(LogType.INFO, "Registering MoreCommands client sided commands.");

		ICommand[] nonRegistryCommands = new ICommand[] {new Commandchelp()};
		ICommand[] commands;
		try {
			commands = Reference.getCommandRegistry(CommandType.CLIENT).toArray(new ICommand[0]);
		} catch (IncorrectCommandType e1) {
			e1.printStackTrace();
			return;
		}

		Integer counter = 0;
		Integer fails = 0;
		List<String> failList = new ArrayList<>();

		for (ICommand command : commands)
			try {
				ClientCommandHandler.instance.registerCommand(command);
				counter += 1;
			} catch (Exception e) {fails += 1; failList.add(command.getName()); continue;}

		for (ICommand command : nonRegistryCommands)
			try {
				ClientCommandHandler.instance.registerCommand(command);
				counter += 1;
			} catch (Exception e) {
				fails += 1;
				failList.add(command.getName());
				e.printStackTrace();
				continue;
			}

		Reference.print(LogType.INFO, "Successfully registered " + counter.toString() + " client sided commands, with " + fails.toString() + " fails.");
		if (!(failList.size() == 0)) Reference.print(LogType.INFO, "Failed to register " + CommandBase.joinNiceString(failList.toArray(new String[0])));
	}

	public static void setupBlockLists() {
		Reference.print(LogType.INFO, "Setting up the MoreCommands block blacklist.");

		Block[] blacklist = {Blocks.AIR, Blocks.LAVA, Blocks.CACTUS, Blocks.MAGMA, Blocks.ACACIA_FENCE, Blocks.ACACIA_FENCE_GATE, Blocks.BIRCH_FENCE, Blocks.BIRCH_FENCE_GATE, Blocks.DARK_OAK_FENCE, Blocks.DARK_OAK_FENCE_GATE,
				Blocks.JUNGLE_FENCE, Blocks.JUNGLE_FENCE_GATE, Blocks.NETHER_BRICK_FENCE, Blocks.OAK_FENCE, Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE, Blocks.SPRUCE_FENCE_GATE, Blocks.FIRE, Blocks.WEB, Blocks.MOB_SPAWNER,
				Blocks.END_PORTAL, Blocks.END_PORTAL_FRAME, Blocks.TNT, Blocks.IRON_TRAPDOOR, Blocks.TRAPDOOR, Blocks.BREWING_STAND};

		Integer counter = 0;

		for (Block element : blacklist) {
			Reference.addBlockToBlacklist(element);
			counter += 1;
		}

		Reference.print(LogType.INFO, "Successfully set up the block blacklist and added " + counter.toString() + " blocks.");
		Reference.print(LogType.INFO, "Setting up the MoreCommands block whitelist.");

		Block[] whitelist = {Blocks.AIR, Blocks.DEADBUSH, Blocks.VINE, Blocks.TALLGRASS, Blocks.ACACIA_DOOR, Blocks.BIRCH_DOOR, Blocks.DARK_OAK_DOOR, Blocks.IRON_DOOR, Blocks.JUNGLE_DOOR, Blocks.OAK_DOOR, Blocks.SPRUCE_DOOR,
				Blocks.DOUBLE_PLANT, Blocks.RED_FLOWER, Blocks.YELLOW_FLOWER, Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM, Blocks.WATERLILY, Blocks.BEETROOTS, Blocks.CARROTS, Blocks.WHEAT, Blocks.POTATOES, Blocks.PUMPKIN_STEM,
				Blocks.MELON_STEM, Blocks.SNOW_LAYER};

		counter = 0;

		for (Block element : whitelist) {
			Reference.addBlockToWhitelist(element);
			counter += 1;
		}

		Reference.print(LogType.INFO, "Successfully set up the block whitelist and added " + counter.toString() + " blocks.");
	}

	public static void setupCommandRegistry() {
		Set<Class<? extends CommandBase>> commands = new Reflections("com.ptsmods.morecommands.commands").getSubTypesOf(CommandBase.class);

		for (Class<? extends CommandBase> command : commands) {
			try {
				Reference.addCommandToRegistry(command.newInstance().getCommandType(), command.newInstance());
				command.newInstance().getPermission(); // just so it's registered in the permissions.
			} catch (IncorrectCommandType e) {
				e.printStackTrace();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoClassDefFoundError e) {};
		}
	}

	@SideOnly(Side.CLIENT)
	public static void registerKeyBinds() {
		HashMap<String, KeyBinding> keyBindings = Reference.getKeyBindings();
		for (String keyBinding : keyBindings.keySet())
			ClientRegistry.registerKeyBinding(keyBindings.get(keyBinding));
	}

}
