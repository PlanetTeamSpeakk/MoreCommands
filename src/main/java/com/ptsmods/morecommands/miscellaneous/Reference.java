package com.ptsmods.morecommands.miscellaneous;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

public class Reference {
	public static final String MOD_ID = "morecommands";
	public static final String MOD_NAME = "MoreCommands";
	public static final String VERSION = "1.13";
	public static final String MC_VERSIONS = "[1.11,1.12]";
	public static final String UPDATE_URL = "https://raw.githubusercontent.com/PlanetTeamSpeakk/MoreCommands/master/version.json";
	
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
	
	public static String parseTime(int time, boolean isTimeRetarded) { // retarded time = 10AM and 10PM, non-retarded time = 10:00 and 22:00 
        int gameTime = time;
        int hours = gameTime / 1000 + 6;
        int minutes = (gameTime % 1000) * 60 / 1000;
        String ampm = "AM";
        if (isTimeRetarded) {
	        if (hours >= 12) {
	            hours -= 12; ampm = "PM"; 
	        }
	 
	        if (hours < 12) {
	            ampm = "AM";
	        }
	 
	        if (hours == 0) hours = 12;
        } else {
        	if (hours >= 24) hours -= 24;
        }
 
        String mm = "0" + minutes; 
        mm = mm.substring(mm.length() - 2, mm.length());
        
        if (isTimeRetarded) {
        	return hours + ":" + mm + " " + ampm;
        } else {
        	return hours + ":" + mm;
        }
    }
	
	public static void sendMessage(Object player, String message) {
		((EntityPlayer) player).sendMessage(new TextComponentString(message));
	}
	
	public static void sendServerMessage(MinecraftServer server, ICommandSender sender, String message) {
		for (int x = 0; x < server.getOnlinePlayerNames().length; x += 1) {
			try {
				EntityPlayer player = CommandBase.getPlayer(server, sender, server.getOnlinePlayerNames()[x]);
				sendMessage(player, message);
			} catch (CommandException e) {}
		}
	}
	
	public static FMLServerStartingEvent getServerStartingEvent() {
		return serverStartingEvent;
	}
	
	public static void setServerStartingEvent(FMLServerStartingEvent event) {
		serverStartingEvent = event;
	}
	
	public static void sendCommandUsage(Object player, String usage) {
		sendMessage(player, TextFormatting.RED + "Usage: " + usage);
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
	
	public static void powerToolCommand(EntityPlayer player, EnumHand hand, Event event, Boolean isLeftClick) throws CommandException {
		ItemStack holding = player.getHeldItem(hand);
		if (holding.getItem() == Items.AIR) return;
		if (holding.hasTagCompound()) {
			NBTTagCompound nbt = holding.getTagCompound();
			if (nbt.hasKey("ptcmd")) {
				MinecraftServer server = Minecraft.getMinecraft().getIntegratedServer();
				EntityPlayer player1;
				try {
					player1 = CommandBase.getPlayer(server, (ICommandSender) player, player.getName());
				} catch (PlayerNotFoundException e) {
					Reference.sendMessage(player, "You could not be found, what kind of black magic are you using that makes code unable to target you?");
					return;
				}
				ICommandSender sender = (ICommandSender) player1;
				server = player1.getServer();
				if (!isLeftClick) {
					powerToolCounter += 1;
				} else if (powerToolCounter%2 != 0) {
					powerToolCounter += 1;
				}
				if (powerToolCounter%2 == 0 && player1.getUniqueID().equals(nbt.getUniqueId("ptowner"))) {
					try {event.setCanceled(true);} catch (UnsupportedOperationException e) {} catch (IllegalArgumentException e) {} // UnsupportedOperationException is for 1.12+, IllegalArgumentException for 1.11.2-
					server.getCommandManager().executeCommand(sender, nbt.getString("ptcmd"));
				}
			}
		}
	}
	
	private static FMLServerStartingEvent serverStartingEvent = null;
	private static int powerToolCounter = 0; // every event gets called twice except for leftclickempty.
	public static HashMap<String, String> tpRequests = new HashMap<String, String>();
	
}
