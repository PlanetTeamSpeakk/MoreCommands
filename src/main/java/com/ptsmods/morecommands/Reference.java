package com.ptsmods.morecommands;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class Reference {
	public static final String MOD_ID = "morecommands";
	public static final String MOD_NAME = "MoreCommands";
	public static final String VERSION = "1.7.2";
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }

	    return true;
	}
	
	public static World getWorld(MinecraftServer server, EntityPlayer player) {
		World world = null;
		WorldServer[] list = server.worlds;
		for (WorldServer ins : list) {
			if (ins.provider.getDimension() == player.world.provider.getDimension())
				world = ins;
		}
	
		if (world == null)
			world = list[0];
		return world;
	}
	
	public static String parseTime(long time, boolean isTimeRetarded) { // retarded time = 10AM and 10PM, non-retarded time = 10:00 and 22:00 
        long gameTime = time;
        long hours = gameTime / 1000 + 6;
        long minutes = (gameTime % 1000) * 60 / 1000;
        String ampm = "AM";
        if (isTimeRetarded == true) {
	        if (hours >= 12) {
	            hours -= 12; ampm = "PM"; 
	        }
	 
	        if (hours >= 12) {
	            hours -= 12; ampm = "AM"; 
	        }
	 
	        if (hours == 0) hours = 12;
        } else {
        	if (hours >= 24) hours -= 24;
        }
 
        String mm = "0" + minutes; 
        mm = mm.substring(mm.length() - 2, mm.length());
        
        if (isTimeRetarded == true) {
        	return hours + ":" + mm + " " + ampm;
        } else {
        	return hours + ":" + mm;	        
        }
    }
	
	public static void sendMessage(Object player, String message) {
		((EntityPlayer) player).sendMessage(new TextComponentString(message));
	}
	
	public static void sendServerMessage(MinecraftServer server, String message) {
		server.sendMessage(new TextComponentString(message));
	}
	
	public static FMLServerStartingEvent getServerStartingEvent() {
		return serverStartingEvent;
	}
	
	public static void setServerStartingEvent(FMLServerStartingEvent event) {
		serverStartingEvent = event;
	}
	
	public static void sendCommandUsage(Object player, String usage) {
		sendMessage(player, RED + "Usage: " + usage);
	}
	
	protected static FMLServerStartingEvent serverStartingEvent = null;
	public static String BLACK = "§0";
	public static String DARK_BLUE = "§1";
	public static String DARK_GREEN = "§2";
	public static String DARK_AQUA = "§3";
	public static String DARK_RED = "§4";
	public static String DARK_PURPLE = "§5";
	public static String GOLD = "§6";
	public static String GRAY = "§7";
	public static String DARK_GRAY = "§8";
	public static String BLUE = "§9";
	public static String GREEN = "§a";
	public static String AQUA = "§b";               // Can't use EnumChatFormating, so I'll use the second-best thing.
	public static String RED = "§c";
	public static String LIGHT_PURPLE = "§d";
	public static String YELLOW = "§e";
	public static String WHITE = "§f";
	public static String OBFUSCATED = "§k";
	public static String BOLD = "§l";
	public static String STRIKETHROUGH = "§m";
	public static String UNDERLINED = "§n";
	public static String ITALIC = "§o";
	public static String RESET = "§r";
	public static ArrayList tpRequesters = new ArrayList();
	public static ArrayList tpRequested = new ArrayList();
	
}
