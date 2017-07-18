package com.ptsmods.morecommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class Reference {
	public static final String MOD_ID = "morecommands";
	public static final String MOD_NAME = "MoreCommands";
	public static final String VERSION = "1.11";
	
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
	
	public static void teleportSafely(EntityPlayer player) {
		World world = player.getEntityWorld();
		float x = player.getPosition().getX();
		float z = player.getPosition().getZ();
		boolean found = false;
		if (!world.isRemote) {
			while (!found) {
				for (Integer y = 256; y != player.getPosition().getY(); y -= 1) {
					Block block = world.getBlockState(new BlockPos(x, y-1, z)).getBlock();
					Block tpblock = world.getBlockState(new BlockPos(x, y, z)).getBlock();
					if (!getBlockBlacklist().contains(block) && getBlockWhitelist().contains(tpblock)) {
						player.setPositionAndUpdate(x+0.5, y, z+0.5);
						found = true;
						break;
					}
				}
				x -= 1;
				z -= 1;
			}
		}
	}
	
	public static String getLookDirectionFromLookVec(Vec3d lookvec) {
		String direction = "unknown";
		Integer x = (int) Math.round(lookvec.x);
		Integer y = (int) Math.round(lookvec.y);
		Integer z = (int) Math.round(lookvec.z);
		if (y == 1) {
			direction = "up";
		} else if (y == -1) {
			direction = "down";
		} else if (x == 0 && z == 1) {
			direction = "south";
		} else if (x == 0 && z == -1) {
			direction = "north";
		} else if (x == 1 && z == 0) {
			direction = "east";
		} else if (x == -1 && z == 0) {
			direction = "west";
		} else if (x == 1 && z == 1) {
			direction = "south-east";
		} else if (x == -1 && z == -1) {
			direction = "north-west";
		} else if (x == 1 && z == -1) {
			direction = "north-east";
		} else if (x == -1 && z == 1) {
			direction = "south-west";
		}
		return direction;
	}
	
	private static ArrayList blockBlacklist = new ArrayList();
	
	public static ArrayList getBlockBlacklist() {
		return blockBlacklist;
	}

	public static boolean addBlockToBlacklist(Block block) {
		return blockBlacklist.add(block);
	}
	
	public static void resetBlockBlackAndWhitelist() {
		blockBlacklist = new ArrayList();
		blockWhitelist = new ArrayList();
	}
	
	private static ArrayList blockWhitelist = new ArrayList();
	
	public static ArrayList getBlockWhitelist() {
		return blockWhitelist;
	}

	public static boolean addBlockToWhitelist(Block block) {
		return blockWhitelist.add(block);
	}
	
	private static FMLServerStartingEvent serverStartingEvent = null;
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
	public static HashMap<String, String> tpRequests = new HashMap<String, String>();
	
}