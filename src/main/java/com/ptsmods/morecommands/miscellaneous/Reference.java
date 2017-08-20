package com.ptsmods.morecommands.miscellaneous;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nullable;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.lwjgl.opengl.Display;

import com.mojang.authlib.GameProfile;
import com.ptsmods.morecommands.commands.fixTime.CommandfixTime;
import com.ptsmods.morecommands.commands.superPickaxe.CommandsuperPickaxe;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.actors.threadpool.Arrays;

public abstract class Reference {
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch (NumberFormatException e) { 
	        return false; 
	    } catch (NullPointerException e) {
	        return false;
	    }

	    return true;
	}
	
	public static boolean isBoolean(Object bool) {
		return bool.toString().toLowerCase().equals("true") || bool.toString().toLowerCase().equals("false");
	}
	
	public static boolean isLong(String s) {
	    try { 
	        Long.parseLong(s); 
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
	        if (hours > 12) {
	            hours -= 12; ampm = "PM"; 
	        } 

	        if (hours > 12) {
	        	hours -= 12; ampm = "AM";
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
	
	public static void sendMessage(EntityPlayer player, String message) {
		if (message == null) message = "";
		try {
			((EntityPlayer) player).sendMessage(new TextComponentString(message));
		} catch (NullPointerException e) {
			try {
				Minecraft.getMinecraft().player.sendMessage(new TextComponentString(message));
			} catch (NullPointerException e1) {
				Minecraft.getMinecraft().ingameGUI.addChatMessage(ChatType.CHAT, new TextComponentString(message));
			}
		}
	}
	
	public static void sendMessage(ICommandSender player, String message) {
		if (message == null) message = "";
		try {
			sendMessage((EntityPlayer) player, message);
		} catch (ClassCastException e) {
			System.out.println(message);
		}
	}
	
	public static void sendChatMessage(EntityPlayerSP player, String message) {
		player.sendChatMessage(message);
	}
	
	public static void sendServerMessage(MinecraftServer server, String message) {
		server.getPlayerList().sendMessage(new TextComponentString(message));
	}
	
	public static FMLServerStartingEvent getServerStartingEvent() {
		return serverStartingEvent;
	}
	
	public static void setServerStartingEvent(FMLServerStartingEvent event) {
		serverStartingEvent = event;
	}
	
	public static void sendCommandUsage(EntityPlayer player, String usage) {
		sendMessage(player, TextFormatting.RED + "Usage: " + usage);
	}
	
	public static void sendCommandUsage(ICommandSender player, String usage) {
		try {
			sendCommandUsage((EntityPlayer) player, usage);
		} catch (ClassCastException e) {
			System.out.println(usage);
		}
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
		return getLookDirectionFromLookVec(lookvec, true);
	}
	
	public static String getLookDirectionFromLookVec(Vec3d lookvec, Boolean includeY) {
		String direction = "unknown";
		Integer x = (int) Math.round(lookvec.x);
		Integer y = (int) Math.round(lookvec.y);
		Integer z = (int) Math.round(lookvec.z);
		if (y == 1 && includeY) {
			direction = "up";
		} else if (y == -1 && includeY) {
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
	
	private static ArrayList<Block> blockBlacklist = new ArrayList<Block>();
	
	public static ArrayList getBlockBlacklist() {
		return blockBlacklist;
	}

	public static boolean addBlockToBlacklist(Block block) {
		return blockBlacklist.add(block);
	}
	
	public static void resetBlockBlackAndWhitelist() {
		blockBlacklist.clear();
		blockWhitelist.clear();
	}
	
	private static ArrayList<Block> blockWhitelist = new ArrayList<Block>();
	
	public static ArrayList getBlockWhitelist() {
		return blockWhitelist;
	}

	public static boolean addBlockToWhitelist(Block block) {
		return blockWhitelist.add(block);
	}
	
	@SideOnly(Side.CLIENT)
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
					return;
				} catch (NullPointerException e) {
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
	
	@SideOnly(Side.CLIENT)
	public static void superPickaxeBreak(EntityPlayer player, EnumHand hand) throws CommandException {
		MinecraftServer server = Minecraft.getMinecraft().getIntegratedServer();
		EntityPlayer player2;
		try {
			player2 = CommandBase.getPlayer(server, (ICommandSender) player, player.getName());
		} catch (PlayerNotFoundException e) {
			return;
		} catch (NullPointerException e) {return;} // why do these even occur?
		server = player2.getServer();
		World world = server.getWorld(player2.dimension);
		BlockPos lookingAt = Minecraft.getMinecraft().objectMouseOver.getBlockPos();
		if (CommandsuperPickaxe.enabled && player.getHeldItem(hand).getItem() instanceof ItemPickaxe) world.destroyBlock(lookingAt, true);
	}
	
	public static String getLocalizedName(Item item) {
		return item.getRegistryName().toString().split(":")[1].replaceAll("_", " ");
	}
	
	public static String getLocalizedName(Block block) {
		return block.getRegistryName().toString().split(":")[1].replaceAll("_", " ");
	}
	
	public static String evalJavaScript(String script) throws ScriptException {
		return evalCode(script, "nashorn");
	}
	
	public static String evalCode(String script, String language) throws ScriptException {
		return new ScriptEngineManager(null).getEngineByName(language).eval(script).toString();
	}
	
	public static TextFormatting getRandomColor(String... exceptions) {
		TextFormatting[] colors = {TextFormatting.AQUA, TextFormatting.BLACK, TextFormatting.BLUE, TextFormatting.DARK_AQUA, TextFormatting.DARK_BLUE, TextFormatting.DARK_GRAY, TextFormatting.DARK_GREEN,
				TextFormatting.DARK_PURPLE, TextFormatting.DARK_RED, TextFormatting.GOLD, TextFormatting.GRAY, TextFormatting.GREEN, TextFormatting.LIGHT_PURPLE, TextFormatting.RED, TextFormatting.WHITE,
				TextFormatting.YELLOW};
		TextFormatting color = colors[ThreadLocalRandom.current().nextInt(0, colors.length+1)];
		while (Arrays.asList(exceptions).contains(getColorName(color))) {
			color = colors[ThreadLocalRandom.current().nextInt(0, colors.length+1)];
		}
		
		return color;
		
	}
	
	public static String getColorName(TextFormatting color) {
		if (color == TextFormatting.AQUA) return "AQUA";
		else if (color == TextFormatting.BLACK) return "BLACK";
		else if (color == TextFormatting.BLUE) return "BLUE";
		else if (color == TextFormatting.BOLD) return "BOLD";
		else if (color == TextFormatting.DARK_AQUA) return "DARK_AQUA";
		else if (color == TextFormatting.DARK_BLUE) return "DARK_BLUE";
		else if (color == TextFormatting.DARK_GRAY) return "DARK_GRAY";
		else if (color == TextFormatting.DARK_GREEN) return "DARK_GREEN";
		else if (color == TextFormatting.DARK_PURPLE) return "DARK_PURPLE";
		else if (color == TextFormatting.DARK_RED) return "DARK_RED";
		else if (color == TextFormatting.GOLD) return "GOLD";
		else if (color == TextFormatting.GRAY) return "GRAY";
		else if (color == TextFormatting.GREEN) return "GREEN";
		else if (color == TextFormatting.ITALIC) return "ITALIC";
		else if (color == TextFormatting.LIGHT_PURPLE) return "LIGHT_PURPLE";
		else if (color == TextFormatting.OBFUSCATED) return "OBFUSCATED";
		else if (color == TextFormatting.RED) return "RED";
		else if (color == TextFormatting.RESET) return "RESET";
		else if (color == TextFormatting.STRIKETHROUGH) return "STRIKETHROUGH";
		else if (color == TextFormatting.UNDERLINE) return "UNDERLINE";
		else if (color == TextFormatting.WHITE) return "WHITE";
		else if (color == TextFormatting.YELLOW) return "YELLOW";
		else return "UNKNOWN";
	}
	
	public static TextFormatting getColorByName(String name) {
		if (name.toLowerCase().equals("aqua")) return TextFormatting.AQUA;
		else if (name.toLowerCase().equals("black")) return TextFormatting.BLACK;
		else if (name.toLowerCase().equals("blue")) return TextFormatting.BLUE;
		else if (name.toLowerCase().equals("bold")) return TextFormatting.BOLD;
		else if (name.toLowerCase().equals("dark_aqua")) return TextFormatting.DARK_AQUA;
		else if (name.toLowerCase().equals("dark_blue")) return TextFormatting.DARK_BLUE;
		else if (name.toLowerCase().equals("dark_gray")) return TextFormatting.DARK_GRAY;
		else if (name.toLowerCase().equals("dark_green")) return TextFormatting.DARK_GREEN;
		else if (name.toLowerCase().equals("dark_purple")) return TextFormatting.DARK_PURPLE;
		else if (name.toLowerCase().equals("dark_red")) return TextFormatting.DARK_RED;
		else if (name.toLowerCase().equals("gold")) return TextFormatting.GOLD;
		else if (name.toLowerCase().equals("gray")) return TextFormatting.GRAY;
		else if (name.toLowerCase().equals("green")) return TextFormatting.GREEN;
		else if (name.toLowerCase().equals("italic")) return TextFormatting.ITALIC;
		else if (name.toLowerCase().equals("light_purple")) return TextFormatting.LIGHT_PURPLE;
		else if (name.toLowerCase().equals("obfuscated")) return TextFormatting.OBFUSCATED;
		else if (name.toLowerCase().equals("red")) return TextFormatting.RED;
		else if (name.toLowerCase().equals("reset")) return TextFormatting.RESET;
		else if (name.toLowerCase().equals("strikethrough")) return TextFormatting.STRIKETHROUGH;
		else if (name.toLowerCase().equals("underline")) return TextFormatting.UNDERLINE;
		else if (name.toLowerCase().equals("white")) return TextFormatting.WHITE;
		else if (name.toLowerCase().equals("yellow")) return TextFormatting.YELLOW;
		else return TextFormatting.BLACK;
	}
	
	public static TextFormatting getColorByCode(String code) {
		if (code.equals("0")) return TextFormatting.BLACK;
		else if (code.equals("1")) return TextFormatting.DARK_BLUE;
		else if (code.equals("2")) return TextFormatting.DARK_GREEN;
		else if (code.equals("3")) return TextFormatting.AQUA;
		else if (code.equals("4")) return TextFormatting.DARK_RED;
		else if (code.equals("5")) return TextFormatting.DARK_PURPLE;
		else if (code.equals("6")) return TextFormatting.GOLD;
		else if (code.equals("7")) return TextFormatting.GRAY;
		else if (code.equals("8")) return TextFormatting.DARK_GRAY;
		else if (code.equals("9")) return TextFormatting.BLUE;
		else if (code.toLowerCase().equals("a")) return TextFormatting.GREEN;
		else if (code.toLowerCase().equals("b")) return TextFormatting.AQUA;
		else if (code.toLowerCase().equals("c")) return TextFormatting.RED;
		else if (code.toLowerCase().equals("d")) return TextFormatting.LIGHT_PURPLE;
		else if (code.toLowerCase().equals("e")) return TextFormatting.YELLOW;
		else if (code.toLowerCase().equals("f")) return TextFormatting.WHITE;
		else if (code.toLowerCase().equals("k")) return TextFormatting.OBFUSCATED;
		else if (code.toLowerCase().equals("l")) return TextFormatting.BOLD;
		else if (code.toLowerCase().equals("m")) return TextFormatting.STRIKETHROUGH;
		else if (code.toLowerCase().equals("n")) return TextFormatting.UNDERLINE;
		else if (code.toLowerCase().equals("o")) return TextFormatting.ITALIC;
		else return TextFormatting.RESET;
	}
	
	public static boolean isConsole(ICommandSender sender) {
		try {
			EntityPlayer player = (EntityPlayer) sender;
		} catch (ClassCastException e) {return true;}
		return false;
	}

	public static void setAllWorldTimes(MinecraftServer server, Integer time) {
		CommandfixTime.time = -1;
		new CommandfixTime().setAllWorldTimes(server, time);
	}
	
	public static void setWorldTime(World world, Integer time) {
		CommandfixTime.time = -1;
		world.setWorldTime(time);
	}
	
	public static String getArrayAsString(Object[] array) {
		String arrayString = "";
		for (int x = 0; x < array.length; x += 1) {
			arrayString += array[x].toString();
			if (x+1 == array.length);
			else if (x+2 != array.length) arrayString += ", ";
			else if (x+2 == array.length) arrayString += " and ";
		}
		return arrayString;
	}
	
	public static String getHTML(String url) throws IOException {
		StringBuilder result = new StringBuilder();
		java.net.URL URL = new java.net.URL(url);
		HttpURLConnection connection = (HttpURLConnection) URL.openConnection();
		connection.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();
		return result.toString();
	}
	
	public static String getServerStatus() throws IOException {
		String statuses = getHTML("https://status.mojang.com/check");
		String[] statusesArray = statuses.split(",");
		for (int x = 0; x < statusesArray.length; x += 1) {
			statusesArray[x] = statusesArray[x].substring(1, statusesArray[x].length()-1).replaceAll("\"", "");
		}
		HashMap<String, String> statusesMap = new HashMap<String, String>();
		for (int x = 0; x < statusesArray.length; x += 1) {
			statusesMap.put(statusesArray[x].split(":")[0], statusesArray[x].split(":")[1]);
		}
		String statusesMapString = statusesMap.toString();
		statusesMapString = statusesMapString.replaceAll("\\{", "");
		statusesMapString = statusesMapString.replaceAll("\\}", "");
		String[] statusesMapArray = statusesMapString.split(", ");
		String statusesFinal = "";
		for (int x = 0; x < statusesMapArray.length; x += 1) {
			statusesFinal += statusesMapArray[x].split("=")[0] + " = " + getColorByName(statusesMapArray[x].split("=")[1]) + statusesMapArray[x].split("=")[1] + TextFormatting.RESET + (x+1 != statusesMapArray.length ? "\n" : "");
		}
		return statusesFinal;
	}
	
	@Nullable
	public static String getUUIDFromName(String name) throws IOException {
		String data = getHTML("https://api.mojang.com/users/profiles/minecraft/" + name);
		if (data.split(",").length == 1) return null;
		else {
			String[] dataArray = data.substring(1, data.length()-1).split(",");
			HashMap<String, String> dataMap = new HashMap<String, String>();
			for (int x = 0; x < dataArray.length; x += 1) {
				dataMap.put(dataArray[x].split(":")[0].substring(1, dataArray[x].split(":")[0].length()-1), dataArray[x].split(":")[1].substring(1, dataArray[x].split(":")[1].length()-1));
			}
			if (dataMap.get("name") != null && dataMap.get("name").equals(name)) return dataMap.get("id");
			else return null;
		}
	}
	
	public static HashMap getPastNamesFromUUID(String UUID) throws IOException {
		String data = getHTML("https://api.mojang.com/user/profiles/" + UUID + "/names");
		String[] dataArray;
		try {
			dataArray = data.substring(1, data.length()-1).split("},");
		} catch (StringIndexOutOfBoundsException e) {
			return new HashMap<String, Long>();
		}
		String firstName = "";
		HashMap<String, Long> dataMap = new HashMap<String, Long>();
		for (int x = 0; x < dataArray.length; x += 1) {
			if (dataArray[x].split(",").length == 1) {firstName = dataArray[x].split(":")[1].substring(1, dataArray[x].split(":")[1].length()-1); dataMap.put(firstName, null);}
			else {
				String name = dataArray[x].split(",")[0].split(":")[1];
				name = name.substring(1, name.length()-1);
				Long changedAt = Long.parseLong(dataArray[x].split(",")[1].split(":")[1].substring(0, dataArray[x].split(",")[1].split(":")[1].length()-1));
				dataMap.put(name, changedAt);
			}
		}
		return dataMap;
	}
	
	public static void sitOnStairs(RightClickBlock event, EntityPlayer player, BlockPos pos, @Nullable MinecraftServer server) throws CommandException {
		World world = player.getEntityWorld();
		Block block = world.getBlockState(pos).getBlock();
		if (server == null) {
			EntityPlayer player1;
			try {
				player1 = CommandBase.getPlayer(server, (ICommandSender) player, player.getName());
			} catch (PlayerNotFoundException e) {
				return;
			} catch (NullPointerException e) {return;}
			event.setCanceled(true);
			server = player1.getServer();
			world = player1.getEntityWorld();
			player = player1;
		}
		if (block instanceof BlockStairs) {
			event.setCanceled(true);
			NBTTagCompound nbt = new NBTTagCompound();
			try {
				nbt = JsonToNBT.getTagFromJson("{id:\"minecraft:arrow\",NoGravity:1b,pickup:0}");
			} catch (NBTException e) {
				e.printStackTrace();
				return;
			}
			double d0 = pos.getX() + 0.5;
			double d1 = pos.getY();
			double d2 = pos.getZ() + 0.5;
			Entity entity = AnvilChunkLoader.readWorldEntityPos(nbt, world, d0, d1, d2, true);
			entity.setLocationAndAngles(d0, d1, d2, entity.rotationYaw, entity.rotationPitch);
			entity.setInvisible(true); // for some reason this function exists but doesn't work, I'll just leave it be.
			player.startRiding(entity);
			isSittingOnChair = true;
			arrow = entity;
			Reference.player = player;
		}
	}
	
	public static void dismountStairs() {
		if (arrow != null && isSittingOnChair) {
			player.dismountRidingEntity();
			arrow.onKillCommand();
			isSittingOnChair = false;
		}
	}
	
	/**
	 * Replaces \r\n with a line break, removes all backslashes and removes spaces at the beginning and end.
	 */
	public static String getCleanString(String dirtyString) {
		return dirtyString.replaceAll("(\\\\r|\\\\n)+", "\n").replaceAll("\\\\\"", "").trim();
	}
	
	
	public static BlockPos roundBlockPos(BlockPos blockpos) {
		double x = blockpos.getX();
		double y = blockpos.getY();
		double z = blockpos.getZ();
		return new BlockPos(Math.round(x), Math.round(y), Math.round(z));
	}
	
	public static TextFormatting getColorFromBoolean(Object bool) {
		if (isBoolean(bool) && Boolean.parseBoolean(bool.toString())) return TextFormatting.GREEN;
		else return TextFormatting.RED;
	}
	
	public static void addTextToNarratorMessage(String text) {
		narratorMessage += text + " ";
	}
	
	public static void resetNarratorMessage() {
		narratorMessage = "";
	}
	
	public static String getNarratorMessage() {
		return narratorMessage;
	}
	
	public static String getIPAddress() {
	    try {
			return getHTML("http://checkip.amazonaws.com/");
		} catch (IOException e) {
			return "unknown"; // occurs when there's no internet connection.
		}
	}
	
	public static double roundDouble(double dbl) {
		return Double.parseDouble(((Long) Math.round(dbl*10)).toString()) / 10.0;
	}
	
	public static void addCommandToRegistry(CommandType type, ICommand command) throws IncorrectCommandType {
		if (type == CommandType.CLIENT) {
			clientCommands.add(command);
		} else if (type == CommandType.SERVER) {
			serverCommands.add(command);
		} else if (type == CommandType.UNKNOWN) { // the only command that should have a command type of UNKNOWN should be the dummy command.
		} else throwIncorrectCommandType();
	}
	
	public static List<ICommand> getCommandRegistry(CommandType type) throws IncorrectCommandType {
		if (type == CommandType.CLIENT) return clientCommands;
		else if (type == CommandType.SERVER) return serverCommands;
		else throwIncorrectCommandType(); return new ArrayList<ICommand>();
	}
	
	public static void resetCommandRegistry(CommandType type) throws IncorrectCommandType {
		if (type == CommandType.SERVER) serverCommands = new ArrayList<ICommand>();
		else if (type == CommandType.CLIENT) clientCommands = new ArrayList<ICommand>();
		else if (type == CommandType.UNKNOWN) {}
		else throwIncorrectCommandType();
	}
	
	public static void throwIncorrectCommandType() throws IncorrectCommandType {
		throw new IncorrectCommandType("The given command type has to be either com.ptsmods.morecommands.miscellaneous.CommandType.CLIENT or com.ptsmods.morecommands.miscellaneous.CommandType.SERVER");
	}
	
	/**
	 * 
	 * @param url The url of the file to be downloaded.
	 * @param fileLocation A string of the location where the file should be downloaded to, this must include a file suffix.
	 * @return Map<String, String> Contains keys fileLocation and success, fileLocation will contain the location where the file was downloaded to, success will be a boolean in a string which shows if the download was successful.
	 * @throws NullPointerException
	 * @throws MalformedURLException 
	 */
	public static Map<String, String> downloadFile(String url, String fileLocation) throws NullPointerException, MalformedURLException {
		String[] fileLocationParts = fileLocation.split("/");
		String fileLocation2 = "";
		for (int x = 0; x < fileLocationParts.length; x += 1) {
			if (x+1 != fileLocationParts.length) {
				fileLocation2 += "/" + fileLocationParts[x];
				new File(fileLocation2.substring(1)).mkdirs();
			}
		}
		if (new File(fileLocation).exists()) fileLocation = fileLocation.split("\\.")[0] + "-1" + (fileLocation.split("\\.").length != 1 ? "." + fileLocation.split("\\.")[fileLocation.split("\\.").length-1] : "");
		while (new File(fileLocation).exists()) fileLocation = addNextDigit(fileLocation);
		java.net.URL website = null;
		try {
			website = new java.net.URL(url); // getting the URL
		} catch (MalformedURLException e) {
			throw e;
		}
		ReadableByteChannel rbc = null;
		try {
			rbc = Channels.newChannel(website.openStream()); // getting the data
		} catch (IOException e1) {
			throw new MalformedURLException("URL does not exist.");
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fileLocation); // creating a new FileOutputStream
		} catch (FileNotFoundException e) {}
		try {
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE); // writing data to a file
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			new File(fileLocation).delete();
			throw new MalformedURLException("URL does not exist.");
		}
		Map<String, String> data = new HashMap<String, String>();
		data.put("fileLocation", fileLocation);
		data.put("success", Boolean.toString(new File(fileLocation).exists()));
		return data;
	}
	
	private static String addNextDigit(String string) {
		Long digit = Long.parseLong(string.split("-")[string.split("-").length-1].split("\\.")[0]);
		digit += 1;
		return string.split("\\.")[0].split("-")[0] + "-" + digit.toString() + "." + string.split("\\.")[string.split("\\.").length-1];
	}
	
	public static String getMinecraftVersion() {
		return new GuiOverlayDebug(Minecraft.getMinecraft()).call().toArray(new String[0])[0].split(" ")[1]; // not the most beautiful way, but doing Minecraft.getVersion() on 1.11.2 returns 1.12.
	}
	
	public static String join(String... stringArray) {
		String data = "";
		for (String part : stringArray) {
			data += part + " ";
		}
		return data.trim();
	}
	
	/**
	 * Sets the title of the window.
	 * @param title
	 * @return True if the title is the same as the given one afterwards, false otherwise.
	 */
	public static boolean setDisplayTitle(String title) {
		Display.setTitle(title);
		System.out.println("The display title has been set to " + Display.getTitle());
		return Display.getTitle().equals(title);
	}
	
	public static String getDefaultDisplayTitle() {
		return join(Display.getTitle().split(" ")[0], Display.getTitle().split(" ")[1]); // on Minecraft 1.12 this will return Minecraft 1.12
	}
	
	public static void registerEventHandler(CommandType side, EventHandler handler) throws IncorrectCommandType {
		if (side == CommandType.CLIENT) registerClientEventHandler(handler);
		else if (side == CommandType.SERVER) registerServerEventHandler(handler);
		else throwIncorrectCommandType();
	}
	
	@SideOnly(Side.CLIENT)
	private static void registerClientEventHandler(EventHandler handler) {
		MinecraftForge.EVENT_BUS.register(handler);
	}
	
	private static void registerServerEventHandler(EventHandler handler) {
		MinecraftForge.EVENT_BUS.register(handler);
	}
	
	public static void downloadDependency(String url, String name) {
		String fileLocation = "mods/" + name;
		if (!new File(fileLocation).exists()) {
			System.out.println("Could not find " + name + " file, download it now...");
			Map<String, String> downloaded = new HashMap<String, String>();
			downloaded.put("fileLocation", "");
			downloaded.put("success", "false");
			try {
				downloaded = Reference.downloadFile(url, fileLocation);
			} catch (NullPointerException | MalformedURLException e) {
				System.err.println(name + " could not be downloaded, thus MoreCommands cannot be used.");
			}
			if (!Boolean.parseBoolean(downloaded.get("success"))) {
				System.err.println(name + " could not be downloaded, thus MoreCommands cannot be used.");
			} else {
				System.out.println("Successfully download " + name + ".");
			}
			shouldRegisterCommands = false; // The game has to be restarted for the mod to see the files.
		}
	}
	
	public static void setupBiomeList() {
		BiomeDictionary.Type[] types = new BiomeDictionary.Type[] {BiomeDictionary.Type.BEACH, BiomeDictionary.Type.COLD, BiomeDictionary.Type.CONIFEROUS, BiomeDictionary.Type.DEAD, BiomeDictionary.Type.DENSE,
				BiomeDictionary.Type.DRY, BiomeDictionary.Type.DRY, BiomeDictionary.Type.END, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.HILLS, BiomeDictionary.Type.HOT, BiomeDictionary.Type.JUNGLE,
				BiomeDictionary.Type.LUSH, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.MESA, BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.MUSHROOM, BiomeDictionary.Type.NETHER, 
				BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.PLAINS, BiomeDictionary.Type.RARE, BiomeDictionary.Type.RIVER, BiomeDictionary.Type.SANDY, BiomeDictionary.Type.SAVANNA, BiomeDictionary.Type.SNOWY,
				BiomeDictionary.Type.SPARSE, BiomeDictionary.Type.SPOOKY, BiomeDictionary.Type.SWAMP, BiomeDictionary.Type.VOID, BiomeDictionary.Type.WASTELAND, BiomeDictionary.Type.WATER, BiomeDictionary.Type.WET};
		for (BiomeDictionary.Type type : types) {
			for (Biome biome : BiomeDictionary.getBiomes(type)) {
				if (!biomes.contains(biome)) biomes.add(biome);
			}
		}
	}
	
	public static List<Biome> getBiomes() {
		return biomes;
	}
	
	public static List<String> getBiomeNames() {
		List<String> names = new ArrayList<String>();
		for (Biome biome : biomes) {
			names.add(biome.getBiomeName().replaceAll(" ", "_")); // just because otherwise everything will get messed up if you'd press tab.
		}
		return names;
	}
	
	@Nullable
	public static Biome getBiomeByName(String name) {
		Biome biome = null;
		for (Biome biome2 : biomes) {
			if (biome2.getBiomeName().toLowerCase().equals(name.toLowerCase())) biome = biome2;
		}
		return biome;
	}
	
	public static boolean isOp(EntityPlayer player) {
		if (player.getServer().isSinglePlayer()) return player.canUseCommand(player.getServer().getOpPermissionLevel(), "barrier");
		else return Arrays.asList(player.getServer().getPlayerList().getOppedPlayerNames()).contains(player.getName());
	}
	
    public static void removeExperience(EntityPlayer player, Integer amount) {
    	System.out.println("Removing " + amount.toString() + " experience.");
        player.addScore(-1 * amount);
        int i = Integer.MAX_VALUE - player.experienceTotal;

        if (amount > i)
        {
            amount = i;
        }

        player.experience -= (float)amount / (float)player.xpBarCap();

        for (player.experienceTotal -= amount; player.experience <= 1.0F; player.experience /= (float)player.xpBarCap())
        {
            player.experience = (player.experience + 1.0F) * (float)player.xpBarCap();
            player.addExperienceLevel(-1);
        }
    }
    
    public static String convertColorCodes(String string) {
    	for (Integer x = 0; x <= 9; x ++) {
    		string = string.replaceAll("&" + x, getColorByCode(x.toString()).toString());
    	}
    	String[] nonNumericColorCodes = new String[] {"a", "b", "c", "d", "e", "f", "k", "l", "m", "n", "o", "r"};
    	for (String code : nonNumericColorCodes) {
    		string = string.replaceAll("&" + code, getColorByCode(code).toString());
    	}
    	return string;
    }
	
	public static final String MOD_ID = "morecommands";
	public static final String MOD_NAME = "MoreCommands";
	public static final String VERSION = "1.24";
	public static final String MC_VERSIONS = "[1.11,1.12.1]";
	public static final String UPDATE_URL = "https://raw.githubusercontent.com/PlanetTeamSpeakk/MoreCommands/master/version.json";
	public static final String BUILD_DATE = "August 20th";
	public static final String[] AUTHORS = new String[] {"PlanetTeamSpeak"}; 
	public static boolean warnedUnregisteredCommands = false;
	public static boolean shouldRegisterCommands = true; // only this very variable has to be set to false to disable the mod's functionality entirely.
	public static boolean narratorActive = false;
	public static Entity arrow = null;
	public static boolean isSittingOnChair = false;
	public static EntityPlayer player = null;
	public static HashMap<String, String> tpRequests = new HashMap<String, String>();
	public static HashMap<String, HashMap<ICommandSender, Long>> cooldowns = new HashMap<String, HashMap<ICommandSender, Long>>();
	public static HashMap<EntityPlayer, NBTTagList> inventories = new HashMap<EntityPlayer, NBTTagList>();
	public static HashMap<EntityPlayer, Vec3d> locations = new HashMap<EntityPlayer, Vec3d>();
	public static HashMap<EntityPlayer, Integer> experiencePoints = new HashMap<EntityPlayer, Integer>();
	public static HashMap<EntityPlayer, HashMap<String, Float>> pitchNYaws = new HashMap<EntityPlayer, HashMap<String, Float>>();
	private static List<Biome> biomes = new ArrayList<Biome>();
	private static List<ICommand> serverCommands = new ArrayList<ICommand>();
	private static List<ICommand> clientCommands = new ArrayList<ICommand>();
	private static String narratorMessage = "";
	private static FMLServerStartingEvent serverStartingEvent = null;
	private static int powerToolCounter = 0; // every event gets called twice except for leftclickempty.
	
	public static abstract class Random {
		private static ThreadLocalRandom tlr = ThreadLocalRandom.current();
		
		public static int randInt() {
			return randInt(0, Integer.MAX_VALUE);
		}
		
		public static int randInt(int max) {
			return randInt(0, max);
		}
		
		public static int randInt(int min, int max) {
			return tlr.nextInt(min, max);
		}
		
		public static long randLong() {
			return randLong(0, Long.MAX_VALUE);
		}
		
		public static long randLong(long max) {
			return randLong(0, max);
		}
		
		public static long randLong(long min, long max) {
			return tlr.nextLong(min, max);
		}
		
		public static short randShort() {
			return randShort((short) 0, Short.MAX_VALUE);
		}
		
		public static short randShort(short max) {
			return randShort((short) 0, max);
		}
		
		public static short randShort(short min, short max) {
			return (short) randInt(min, max);
		}
		
		public static double randDouble() {
			return randDouble(0D, Double.MAX_VALUE);
		}
		
		public static double randDouble(double max) {
			return randDouble(0D, max);
		}
		
		public static double randDouble(double min, double max) {
			return tlr.nextDouble(min, max);
		}
		
		public static float randFloat() {
			return randFloat(0F, Float.MAX_VALUE);
		}
		
		public static float randFloat(float max) {
			return randFloat(0F, max);
		}
		
		public static float randFloat(float min, float max) {
			return (float) randDouble(min, max);
		}
		
	}
	
}
