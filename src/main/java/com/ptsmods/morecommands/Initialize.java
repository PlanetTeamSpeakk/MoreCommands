package com.ptsmods.morecommands;

import java.util.Set;

import org.reflections.Reflections;

import com.ptsmods.morecommands.commands.enchant.Commandenchant;
import com.ptsmods.morecommands.commands.fixTime.CommandfixTime;
import com.ptsmods.morecommands.miscellaneous.ClientEventHandler;
import com.ptsmods.morecommands.miscellaneous.CommandBase;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.IncorrectCommandType;
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

public abstract class Initialize {

	public static void registerCommands(FMLServerStartingEvent event) {
		System.out.println("Registering MoreCommands commands.");
		
		ICommand[] nonRegistryCommands = {new CommandfixTime(), new Commandenchant()}; // these commands do not extend com.ptsmods.morecommands.miscellaneous.CommandBase and thus aren't seen by the Reflections.getSubTypesOf(CommandBase.class)
		ICommand[] commands;
		try {
			commands = Reference.getCommandRegistry(CommandType.SERVER).toArray(new ICommand[0]);
		} catch (IncorrectCommandType e1) {
			e1.printStackTrace();
			return;
		}
		
		Integer counter = 0;
		Integer failed = 0;
		
		for (ICommand command : commands) {
			try {
				event.registerServerCommand(command);
			} catch (Exception e) {failed += 1; continue;}
			counter += 1;
		}
		
		for (ICommand command : nonRegistryCommands) {
			try {
				event.registerServerCommand(command);
			} catch (Exception e) {failed += 1; continue;}
			counter += 1;
		}
		
		System.out.println("Successfully registered " + counter.toString() + " MoreCommands commands and failed to register " + failed.toString() + " commands.");
	}
	
	@SideOnly(Side.CLIENT)
	public static void registerClientCommands() {
		System.out.println("Registering MoreCommands client sided commands.");

		ICommand[] commands;
		try {
			commands = Reference.getCommandRegistry(CommandType.CLIENT).toArray(new ICommand[0]);
		} catch (IncorrectCommandType e1) {
			e1.printStackTrace();
			return;
		}

		Integer counter = 0;
		Integer fails = 0;
		
		for (int x = 0; x < commands.length; x += 1) {
			try {
				ClientCommandHandler.instance.registerCommand(commands[x]);
				counter += 1;
			} catch (Exception e) {fails += 1; continue;}
		}
		
		System.out.println("Successfully registered " + counter.toString() + " client sided commands, with " + fails.toString() + " fails.");
	}
	
	public static void registerEvenHandlers() {
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
		MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
	}
	
	public static void setupBlockLists() {
		System.out.println("Setting up the MoreCommands block blacklist.");
		
		Block[] blacklist = {Blocks.AIR, Blocks.LAVA, Blocks.CACTUS, Blocks.MAGMA, Blocks.ACACIA_FENCE, Blocks.ACACIA_FENCE_GATE, Blocks.BIRCH_FENCE, Blocks.BIRCH_FENCE_GATE, Blocks.DARK_OAK_FENCE, Blocks.DARK_OAK_FENCE_GATE,
				Blocks.JUNGLE_FENCE, Blocks.JUNGLE_FENCE_GATE, Blocks.NETHER_BRICK_FENCE, Blocks.OAK_FENCE, Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE, Blocks.SPRUCE_FENCE_GATE, Blocks.FIRE, Blocks.WEB, Blocks.MOB_SPAWNER,
				Blocks.END_PORTAL, Blocks.END_PORTAL_FRAME, Blocks.TNT, Blocks.IRON_TRAPDOOR, Blocks.TRAPDOOR, Blocks.BREWING_STAND};

		Integer counter = 0;
		
		for (int x = 0; x < blacklist.length; x += 1) {
			Reference.addBlockToBlacklist(blacklist[x]);
			counter += 1;
		}
		
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
	
	public static void setupCommandRegistry() {
		Set<Class<? extends CommandBase>> commands = new Reflections("com.ptsmods.morecommands.commands").getSubTypesOf(CommandBase.class);
		
		for (Class<? extends CommandBase> command : commands) {
			try {
				Reference.addCommandToRegistry(command.newInstance().getCommandType(), command.newInstance());
			} catch (IncorrectCommandType e) {
				e.printStackTrace();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
