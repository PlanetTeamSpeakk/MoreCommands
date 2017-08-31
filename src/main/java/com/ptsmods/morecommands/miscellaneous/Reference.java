package com.ptsmods.morecommands.miscellaneous;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nullable;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.io.IOUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.parser.ParserException;

import com.ptsmods.morecommands.Initialize;
import com.ptsmods.morecommands.commands.fixTime.CommandfixTime;
import com.ptsmods.morecommands.commands.superPickaxe.CommandsuperPickaxe;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
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
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.FMLCommonHandler;
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

	public static String parseTime(long gameTime, boolean isTimeRetarded) { // retarded time = 10AM and 10PM, non-retarded time = 10:00 and 22:00
		long hours = gameTime / 1000 + 6;
		long minutes = gameTime % 1000 * 60 / 1000;
		String ampm = "AM";
		if (isTimeRetarded) {
			if (hours > 12) {
				hours -= 12; ampm = "PM";
			}

			if (hours > 12) {
				hours -= 12; ampm = "AM";
			}

			if (hours == 0) hours = 12;
		} else if (hours >= 24) hours -= 24;

		String mm = "0" + minutes;
		mm = mm.substring(mm.length() - 2, mm.length());

		if (isTimeRetarded)
			return hours + ":" + mm + " " + ampm;
		else
			return hours + ":" + mm;
	}

	public static void sendMessage(EntityPlayer player, String message) {
		if (message == null) message = "";
		try {
			player.sendMessage(new TextComponentString(message));
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
			print(LogType.INFO,message);
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
			print(LogType.INFO,usage);
		}
	}

	public static void teleportSafely(EntityPlayer player) {
		World world = player.getEntityWorld();
		float x = player.getPosition().getX();
		float z = player.getPosition().getZ();
		boolean found = false;
		if (!world.isRemote)
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

	public static String getLookDirectionFromLookVec(Vec3d lookvec) {
		return getLookDirectionFromLookVec(lookvec, true);
	}

	public static String getLookDirectionFromLookVec(Vec3d lookvec, Boolean includeY) {
		String direction = "unknown";
		Integer x = (int) Math.round(lookvec.x);
		Integer y = (int) Math.round(lookvec.y);
		Integer z = (int) Math.round(lookvec.z);
		if (y == 1 && includeY)
			direction = "up";
		else if (y == -1 && includeY)
			direction = "down";
		else if (x == 0 && z == 1)
			direction = "south";
		else if (x == 0 && z == -1)
			direction = "north";
		else if (x == 1 && z == 0)
			direction = "east";
		else if (x == -1 && z == 0)
			direction = "west";
		else if (x == 1 && z == 1)
			direction = "south-east";
		else if (x == -1 && z == -1)
			direction = "north-west";
		else if (x == 1 && z == -1)
			direction = "north-east";
		else if (x == -1 && z == 1)
			direction = "south-west";
		return direction;
	}

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
					player1 = CommandBase.getPlayer(server, player, player.getName());
				} catch (PlayerNotFoundException e) {
					return;
				} catch (NullPointerException e) {
					return;
				}
				ICommandSender sender = player1;
				server = player1.getServer();
				if (!isLeftClick)
					powerToolCounter += 1;
				else if (powerToolCounter%2 != 0)
					powerToolCounter += 1;
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
			player2 = CommandBase.getPlayer(server, player, player.getName());
		} catch (PlayerNotFoundException e) {
			return;
		} catch (NullPointerException e) {return;} // why do these even occur?
		server = player2.getServer();
		World world = server.getWorld(player2.dimension);
		BlockPos lookingAt = Minecraft.getMinecraft().objectMouseOver.getBlockPos();
		if (CommandsuperPickaxe.enabled && player.getHeldItem(hand).getItem() instanceof ItemPickaxe) world.destroyBlock(lookingAt, true);
	}

	public static String getLocalizedName(Item item) {
		String translation = new TextComponentTranslation("item." + item.getUnlocalizedName().substring(5) + ".name").getUnformattedText();
		if (translation.startsWith("item.") && translation.endsWith(".name")) translation = item.getRegistryName().toString().split(":")[1].replaceAll("_", " ");
		return translation;
	}

	public static String getLocalizedName(Block block) {
		String translation = new TextComponentTranslation("block." + block.getUnlocalizedName().substring(5) + ".name").getUnformattedText();
		if (translation.startsWith("block.") && translation.endsWith(".name")) translation = block.getRegistryName().toString().split(":")[1].replaceAll("_", " ");
		return translation == null ? "air" : translation;
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
		TextFormatting color = colors[Random.randInt(0, colors.length)];
		while (Arrays.asList(exceptions).contains(getColorName(color)))
			color = colors[Random.randInt(0, colors.length)];

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
		BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null)
			result.append(line);
		rd.close();
		return result.toString();
	}

	public static String getServerStatus() throws IOException {
		String statuses = getHTML("https://status.mojang.com/check");
		String[] statusesArray = statuses.split(",");
		for (int x = 0; x < statusesArray.length; x += 1)
			statusesArray[x] = statusesArray[x].substring(1, statusesArray[x].length()-1).replaceAll("\"", "");
		HashMap<String, String> statusesMap = new HashMap<>();
		for (String element : statusesArray)
			statusesMap.put(element.split(":")[0], element.split(":")[1]);
		String statusesMapString = statusesMap.toString();
		statusesMapString = statusesMapString.replaceAll("\\{", "");
		statusesMapString = statusesMapString.replaceAll("\\}", "");
		String[] statusesMapArray = statusesMapString.split(", ");
		String statusesFinal = "";
		for (int x = 0; x < statusesMapArray.length; x += 1)
			statusesFinal += statusesMapArray[x].split("=")[0] + " = " + getColorByName(statusesMapArray[x].split("=")[1]) + statusesMapArray[x].split("=")[1] + TextFormatting.RESET + (x+1 != statusesMapArray.length ? "\n" : "");
		return statusesFinal;
	}

	@Nullable
	public static String getUUIDFromName(String name) throws IOException {
		String data = getHTML("https://api.mojang.com/users/profiles/minecraft/" + name);
		if (data.split(",").length == 1) return null;
		else {
			String[] dataArray = data.substring(1, data.length()-1).split(",");
			HashMap<String, String> dataMap = new HashMap<>();
			for (String element : dataArray)
				dataMap.put(element.split(":")[0].substring(1, element.split(":")[0].length()-1), element.split(":")[1].substring(1, element.split(":")[1].length()-1));
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
		HashMap<String, Long> dataMap = new HashMap<>();
		for (String element : dataArray)
			if (element.split(",").length == 1) {firstName = element.split(":")[1].substring(1, element.split(":")[1].length()-1); dataMap.put(firstName, null);}
			else {
				String name = element.split(",")[0].split(":")[1];
				name = name.substring(1, name.length()-1);
				Long changedAt = Long.parseLong(element.split(",")[1].split(":")[1].substring(0, element.split(",")[1].split(":")[1].length()-1));
				dataMap.put(name, changedAt);
			}
		return dataMap;
	}

	public static void sitOnStairs(RightClickBlock event, EntityPlayer player, BlockPos pos, @Nullable MinecraftServer server) throws CommandException {
		World world = player.getEntityWorld();
		Block block = world.getBlockState(pos).getBlock();
		if (server == null) {
			EntityPlayer player1;
			try {
				player1 = CommandBase.getPlayer(server, player, player.getName());
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
		if (ipAddress == null)
			try {
				ipAddress = getHTML("http://checkip.amazonaws.com/");
			} catch (IOException e) {
				ipAddress = "unknown"; // occurs when there's no internet connection.
			}
		return ipAddress;
	}

	public static double roundDouble(double dbl) {
		return Double.parseDouble(((Long) Math.round(dbl*10)).toString()) / 10.0;
	}

	public static void addCommandToRegistry(CommandType type, ICommand command) throws IncorrectCommandType {
		if (type == CommandType.CLIENT)
			clientCommands.add(command);
		else if (type == CommandType.SERVER)
			serverCommands.add(command);
		else if (type == CommandType.UNKNOWN) { // the only command that should have a command type of UNKNOWN should be the dummy command.
		} else throwIncorrectCommandType();
	}

	public static List<ICommand> getCommandRegistry(CommandType type) throws IncorrectCommandType {
		if (type == CommandType.CLIENT) return clientCommands;
		else if (type == CommandType.SERVER) return serverCommands;
		else throwIncorrectCommandType(); return new ArrayList<>();
	}

	public static void resetCommandRegistry(CommandType type) throws IncorrectCommandType {
		if (type == CommandType.SERVER) serverCommands = new ArrayList<>();
		else if (type == CommandType.CLIENT) clientCommands = new ArrayList<>();
		else if (type == CommandType.UNKNOWN) {}
		else throwIncorrectCommandType();
	}

	public static void throwIncorrectCommandType() throws IncorrectCommandType {
		throw new IncorrectCommandType("The given command type has to be either com.ptsmods.morecommands.miscellaneous.CommandType.CLIENT or com.ptsmods.morecommands.miscellaneous.CommandType.SERVER");
	}

	public static String getMinecraftVersion() {
		try {
			return new GuiOverlayDebug(Minecraft.getMinecraft()).call().toArray(new String[0])[0].split(" ")[1]; // not the most beautiful way, but doing Minecraft.getVersion() on 1.11.2 returns 1.12.
		} catch (NullPointerException e) {
			return getDefaultDisplayTitle().split(" ")[1];
		}
	}

	public static String join(String... stringArray) {
		return joinCustomChar(" ", stringArray);
	}

	public static String joinCustomChar(String character, String... stringArray) {
		return joinCustomChar(character, (Object[]) stringArray);
	}

	public static String joinCustomChar(String character, Object... array) {
		String data = "";
		for (int x = 0; x < array.length; x++)
			data += array[x] + (x+1 == array.length ? "" : character);
		return data.trim();
	}

	/**
	 * Sets the title of the window.
	 * @param title
	 * @return True if the title is the same as the given one afterwards, false otherwise.
	 */
	public static boolean setDisplayTitle(String title) {
		try {
			Display.setTitle(title);
		} catch (Throwable e) {
			return false;
		}
		print(LogType.INFO,"The display title has been set to " + Display.getTitle());
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

	public static void setupBiomeList() {
		BiomeDictionary.Type[] types = new BiomeDictionary.Type[] {BiomeDictionary.Type.BEACH, BiomeDictionary.Type.COLD, BiomeDictionary.Type.CONIFEROUS, BiomeDictionary.Type.DEAD, BiomeDictionary.Type.DENSE,
				BiomeDictionary.Type.DRY, BiomeDictionary.Type.DRY, BiomeDictionary.Type.END, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.HILLS, BiomeDictionary.Type.HOT, BiomeDictionary.Type.JUNGLE,
				BiomeDictionary.Type.LUSH, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.MESA, BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.MUSHROOM, BiomeDictionary.Type.NETHER,
				BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.PLAINS, BiomeDictionary.Type.RARE, BiomeDictionary.Type.RIVER, BiomeDictionary.Type.SANDY, BiomeDictionary.Type.SAVANNA, BiomeDictionary.Type.SNOWY,
				BiomeDictionary.Type.SPARSE, BiomeDictionary.Type.SPOOKY, BiomeDictionary.Type.SWAMP, BiomeDictionary.Type.VOID, BiomeDictionary.Type.WASTELAND, BiomeDictionary.Type.WATER, BiomeDictionary.Type.WET};
		for (BiomeDictionary.Type type : types)
			for (Biome biome : BiomeDictionary.getBiomes(type))
				if (!biomes.contains(biome)) biomes.add(biome);
	}

	public static List<Biome> getBiomes() {
		return biomes;
	}

	public static List<String> getBiomeNames() {
		List<String> names = new ArrayList<>();
		for (Biome biome : biomes)
			names.add(biome.getBiomeName().replaceAll(" ", "_")); // just because otherwise everything will get messed up if you'd press tab.
		return names;
	}

	@Nullable
	public static Biome getBiomeByName(String name) {
		Biome biome = null;
		for (Biome biome2 : biomes)
			if (biome2.getBiomeName().toLowerCase().equals(name.toLowerCase())) biome = biome2;
		return biome;
	}

	public static boolean isOp(EntityPlayer player) {
		if (player.getServer().isSinglePlayer()) return player.canUseCommand(player.getServer().getOpPermissionLevel(), "barrier");
		else return Arrays.asList(player.getServer().getPlayerList().getOppedPlayerNames()).contains(player.getName());
	}

	public static void removeExperience(EntityPlayer player, Integer amount) {
		player.addScore(-1 * amount);
		int i = Integer.MAX_VALUE - player.experienceTotal;

		if (amount > i)
			amount = i;

		player.experience -= (float)amount / (float)player.xpBarCap();

		for (player.experienceTotal -= amount; player.experience <= 1.0F; player.experience /= player.xpBarCap())
		{
			player.experience = (player.experience + 1.0F) * player.xpBarCap();
			player.addExperienceLevel(-1);
		}
	}

	public static String convertColorCodes(String string) {
		String[] colorCodes = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "k", "l", "m", "n", "o", "r"};
		for (String code : colorCodes)
			string = string.replaceAll("&" + code, getColorByCode(code).toString());
		return string;
	}

	public static String[] removeArg(String[] args, int arg) {
		List<String> data = new ArrayList<>();
		for (int x = 0; x < args.length; x++)
			if (x != arg) data.add(args[x]);
		return data.toArray(new String[0]);
	}

	@SideOnly(Side.CLIENT)
	public static void setupKeyBindingRegistry() {
		KeyBinding[] keyBindings = new KeyBinding[] {new KeyBinding("Toggle overlay", Keyboard.KEY_C, "MoreCommands"), new KeyBinding("Fireball", Keyboard.KEY_V, "MoreCommands")};
		String[] names = new String[] {"toggleOverlay", "fireball"};
		for (int x = 0; x < keyBindings.length; x++)
			Reference.keyBindings.put(names[x], keyBindings[x]);
	}

	@SideOnly(Side.CLIENT)
	public static KeyBinding getKeyBindingByName(String name) {
		return keyBindings.get(name);
	}

	@SideOnly(Side.CLIENT)
	public static HashMap<String, KeyBinding> getKeyBindings() {
		return keyBindings;
	}

	@SideOnly(Side.CLIENT)
	public static void keyBindPressed(String keyBindName) {
		if (keyBindName.equals("toggleOverlay")) executeClientCommand("toggleoverlay");
		else if (keyBindName.equals("fireball")) executeClientCommand("fireball");
	}

	@SideOnly(Side.CLIENT)
	public static void toggleCoordinatesOverlay() {
		overlayEnabled = !overlayEnabled;
	}

	@SideOnly(Side.CLIENT)
	public static boolean isInfoOverlayEnabled() {
		return overlayEnabled;
	}

	public static double calculateBlocksPerSecond(Vec3d before, Vec3d after) {
		double x = after.x - before.x;
		double y = after.y - before.y;
		double z = after.z - before.z;
		return MathHelper.sqrt(x*x+y*y+z*z) * 10;
	}

	public static double calculateBlocksPerSecond() {
		try {
			return calculateBlocksPerSecond(lastPosition, Minecraft.getMinecraft().player.getPositionVector());
		} catch (NullPointerException e) {
			return 0D;
		}
	}

	@SideOnly(Side.CLIENT)
	public static void executeClientCommand(String command) {
		ClientCommandHandler.instance.executeCommand(Minecraft.getMinecraft().player, command);
	}

	public static void executeServerCommand(String command, Entity entity, MinecraftServer server) {
		server.getCommandManager().executeCommand(entity, command);
	}

	public static void addHome(EntityPlayer owner, Vec3d location, Float yaw, Float pitch) throws IOException {
		if (!isHomesFileLoaded()) loadHomesFile();
		Map<String, Double> data = new HashMap<>();
		data.put("x", location.x);
		data.put("y", location.y);
		data.put("z", location.z);
		data.put("yaw", (double) yaw);
		data.put("pitch", (double) pitch);
		homes.put(owner.getName(), data);
	}

	public static void removeHome(EntityPlayer owner) throws IOException {
		if (!isHomesFileLoaded()) loadHomesFile();
		homes.remove(owner.getName());
	}

	public static boolean doesPlayerHaveHome(EntityPlayer player) throws IOException {
		if (!isHomesFileLoaded()) loadHomesFile();
		return homes.containsKey(player.getName());
	}

	public static void saveHomesFile() throws IOException {
		if (!isHomesFileLoaded()) loadHomesFile();
		Yaml yaml = new Yaml();
		yaml.dump(homes, new FileWriter(new File("config/MoreCommands/homes.yaml")));

	}

	public static void loadHomesFile() throws IOException {
		Yaml yaml = new Yaml();
		try {
			homes = (Map<String, Map<String, Double>>) yaml.load(joinCustomChar("\n", Files.readAllLines(Paths.get(new File("config/MoreCommands/homes.yaml").getAbsolutePath())).toArray(new String[0])));
		} catch (ParserException e) {
			print(LogType.ERROR, "An error occured while reading homes.yaml.");
			e.printStackTrace();
			homes = null;
		} catch (StringIndexOutOfBoundsException e) {}
		homes = homes == null ? new HashMap<>() : homes; // just making sure when the file is empty no NullPointerExceptions occur.

	}

	public static boolean isHomesFileLoaded() {
		return !(homes == null);
	}

	public static void addWarp(String name, Vec3d location, Float yaw, Float pitch) throws IOException {
		if (!isHomesFileLoaded()) loadHomesFile();
		Map<String, Double> data = new HashMap<>();
		data.put("x", location.x);
		data.put("y", location.y);
		data.put("z", location.z);
		data.put("yaw", (double) yaw);
		data.put("pitch", (double) pitch);
		warps.put(name, data);
	}

	public static void removeWarp(String name) throws IOException {
		if (!isWarpsFileLoaded()) loadWarpsFile();
		warps.remove(name);
	}

	public static boolean doesWarpExist(String name) throws IOException {
		if (!isWarpsFileLoaded()) loadWarpsFile();
		return warps.containsKey(name);
	}

	public static void saveWarpsFile() throws IOException {
		if (!isWarpsFileLoaded()) loadWarpsFile();
		Yaml yaml = new Yaml();
		yaml.dump(warps, new FileWriter(new File("config/MoreCommands/warps.yaml")));
	}


	public static void loadWarpsFile() throws IOException {
		Yaml yaml = new Yaml();
		try {
			warps = (Map<String, Map<String, Double>>) yaml.load(joinCustomChar("\n", Files.readAllLines(Paths.get(new File("config/MoreCommands/warps.yaml").getAbsolutePath())).toArray(new String[0])));
		} catch (ParserException e) {
			print(LogType.ERROR, "An error occured while reading warps.yaml.");
			e.printStackTrace();
			warps = null;
		} catch (StringIndexOutOfBoundsException e) {}
		warps = warps == null ? new HashMap<>() : warps; // just making sure when the file is empty no NullPointerExceptions occurs.

	}

	public static boolean isWarpsFileLoaded() {
		return warps != null;
	}

	public static String getWarpsString() {
		String warps = joinCustomChar(TextFormatting.YELLOW + ", " + TextFormatting.GOLD, getWarps());
		return TextFormatting.GOLD + warps.substring(0, warps.length()-6);
	}

	public static String[] getWarps() {
		return warps.keySet().toArray(new String[0]);
	}

	public static float doubleToFloat(Double d) {
		return Float.parseFloat(d.toString());
	}

	public static void loadInfoOverlayConfig() throws IOException {
		try {
			infoOverlayConfig = Files.readAllLines(Paths.get(new File("config/MoreCommands/infoOverlay.txt").getAbsolutePath()));
		} catch (FileNotFoundException | NoSuchFileException e) {
			if (!shouldSaveInfoOverlayConfig) {
				shouldSaveInfoOverlayConfig = true;
				loadInfoOverlayConfig();
				return;
			}
		}
		infoOverlayConfig = infoOverlayConfig.size() == 0 ? setupDefaultInfoOverlayConfig() : infoOverlayConfig; // just making sure when the file is empty no NullPointerExceptions occurs.
		if (shouldSaveInfoOverlayConfig) {
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream("config/MoreCommands/infoOverlay.txt"), StandardCharsets.UTF_8));
			try {
				for (String line : infoOverlayConfig)
					writer.println(line);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(writer);
			}
			shouldSaveInfoOverlayConfig = false;
		}

	}

	public static List<String> setupDefaultInfoOverlayConfig() {
		infoOverlayConfig.add(TextFormatting.GOLD + "Player: " + TextFormatting.YELLOW + "{playerName}");
		infoOverlayConfig.add(TextFormatting.GOLD + "X: " + TextFormatting.YELLOW + "{x}");
		infoOverlayConfig.add(TextFormatting.GOLD + "Y: " + TextFormatting.YELLOW + "{y}");
		infoOverlayConfig.add(TextFormatting.GOLD + "Z: " + TextFormatting.YELLOW + "{z}");
		infoOverlayConfig.add(TextFormatting.GOLD + "Yaw: " + TextFormatting.YELLOW + "{yaw}");
		infoOverlayConfig.add(TextFormatting.GOLD + "Pitch: " + TextFormatting.YELLOW + "{pitch}");
		infoOverlayConfig.add(TextFormatting.GOLD + "Facing: " + TextFormatting.YELLOW + "{facing}");
		infoOverlayConfig.add(TextFormatting.GOLD + "Biome: " + TextFormatting.YELLOW + "{biome}");
		infoOverlayConfig.add(TextFormatting.GOLD + "Difficulty: " + TextFormatting.YELLOW + "{difficulty}");
		infoOverlayConfig.add(TextFormatting.GOLD + "Blocks/sec: " + TextFormatting.YELLOW + "{blocksPerSec}");
		infoOverlayConfig.add(TextFormatting.GOLD + "Toggle key: " + TextFormatting.YELLOW + "{toggleKey}");
		shouldSaveInfoOverlayConfig = true;
		return infoOverlayConfig;
	}

	public static List<String> getInfoOverlayConfig() {
		if (infoOverlayConfig == null)
			try {
				loadInfoOverlayConfig();
			} catch (IOException e) {
				e.printStackTrace();
				return new ArrayList<>();
			}
		return infoOverlayConfig;
	}

	public static Integer[] range(int range) {
		Integer[] array = new Integer[range];
		for (int x = 0; x < array.length; x++)
			array[x] = x;
		return array;
	}

	public static BlockPos centerBlockPos(BlockPos pos) {
		return centerBlockPos(pos, false);
	}

	public static BlockPos centerBlockPos(BlockPos pos, Boolean centerY) {
		return new BlockPos(pos.getX()+0.5, pos.getY()+(centerY ? 0.5 : 0), pos.getZ()+0.5);
	}

	public static void playEasterEgg() {
		Minecraft.getMinecraft().player.playSound(new SoundEvent(regenerateEasterEgg().getSoundLocation()), Float.MAX_VALUE, 0F);
	}

	public static void loopEasterEgg() throws InterruptedException {
		while (easterEggLoopEnabled) {
			playEasterEgg();
			sleep(ticksToMillis(secondsToTicks(9.5)));
		}
	}

	public static void addJarToClasspath(String fileLocation) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, MalformedURLException {
		Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
		method.setAccessible(true);
		method.invoke(ClassLoader.getSystemClassLoader(), new File(new File(fileLocation).getAbsolutePath()).toURI().toURL());
	}

	public static void print(String threadName, LogType logType, Object... message) {
		String[] args = new String[message.length];
		for (int x = 0; x < message.length; x++)
			if (message[x] == null) args[x] = "null";
			else args[x] = message[x].toString();
		if (logType == LogType.INFO) System.out.print("[" + getFormattedTime() + "] [" + threadName + "/INFO]: " + join(args) + "\n");
		else if (logType == LogType.WARN) System.out.print("\u001B[33m[" + getFormattedTime() + "] [" + threadName + "/WARN]: " + join(args) + "\u001B[0m\n");
		else if (logType == LogType.ERROR) System.err.print("[" + getFormattedTime() + "] [" + threadName + "/ERROR]: " + join(args) + "\n");
	}

	public static String getFormattedTime() {
		return joinCustomChar(":", "" + (LocalDateTime.now().getHour() < 10 ? "0" : "") + LocalDateTime.now().getHour(),
				"" + (LocalDateTime.now().getMinute() < 10 ? "0" : "") + LocalDateTime.now().getMinute(),
				"" + (LocalDateTime.now().getSecond() < 10 ? "0" : "") + LocalDateTime.now().getSecond());
	}

	public static void print(LogType logType, Object... message) {
		print(MOD_NAME, logType, message);
	}

	public static boolean checkPermission(ICommandSender sender, Permission permission) {
		if (FMLCommonHandler.instance().getMinecraftServerInstance().isSinglePlayer()) return true;
		if (!permission.reqPerms()) return true;
		if (!players.containsKey(((EntityPlayer) sender).getUniqueID().toString())) return false;
		boolean hasPerm = false;
		for (String group : players.get(sender.getCommandSenderEntity().getUniqueID().toString()))
			if (groups.containsKey(group) && (groups.get(group).contains(permission.toString()) || groups.get(group).contains("*") || groups.get(group).contains(permission.getModName() + ".*"))) {
				hasPerm = true;
				break;
			}
		return hasPerm;
	}

	public static void loadGroups() {
		Yaml yaml = new Yaml();
		try {
			groups = (HashMap<String, ArrayList<String>>) yaml.load(joinCustomChar("\n", Files.readAllLines(Paths.get(new File("config/MoreCommands/Permissions/groups.yaml").getAbsolutePath())).toArray(new String[0])));
		} catch (ParserException | IOException e) {
			print(LogType.ERROR, "An error occured while reading groups.yaml.");
			e.printStackTrace();
			groups = null;
		} catch (StringIndexOutOfBoundsException e) {}
		groups = groups == null ? new HashMap<>() : groups; // just making sure when the file is empty no NullPointerExceptions occur.

	}

	public static void loadPlayers() {
		Yaml yaml = new Yaml();
		try {
			players = (HashMap<String, ArrayList<String>>) yaml.load(joinCustomChar("\n", Files.readAllLines(Paths.get(new File("config/MoreCommands/Permissions/players.yaml").getAbsolutePath())).toArray(new String[0])));
		} catch (ParserException | IOException e) {
			print(LogType.ERROR, "An error occured while reading players.yaml.");
			e.printStackTrace();
			players = null;
		} catch (StringIndexOutOfBoundsException e) {}
		players = players == null ? new HashMap<>() : players; // just making sure when the file is empty no NullPointerExceptions occur.

	}

	public static void loadYamlFiles() throws IOException {

		loadHomesFile();
		loadWarpsFile();
		loadGroups();
		loadPlayers();

	}

	public static ArrayList<String> createGroup(String name) {
		ArrayList<String> output = groups.put(name, new ArrayList<>());
		try {
			saveGroups();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}

	public static ArrayList<String> addPermissionToGroup(Permission permission, String group) {
		if (groups.containsKey(group)) {
			ArrayList<String> groupPerms = groups.get(group);
			groupPerms.add(permission.toString());
			groups.remove(group);
			groups.put(group, groupPerms);
			try {
				saveGroups();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return groupPerms;
		} else return new ArrayList<>();
	}

	public static ArrayList<String> addPlayerToGroup(EntityPlayer player, String group) {
		ArrayList<String> playerGroups = players.get(group);
		playerGroups = playerGroups == null ? new ArrayList<>() : playerGroups;
		playerGroups.add(group);
		players.remove(player.getUniqueID().toString());
		players.put(player.getUniqueID().toString(), playerGroups);
		try {
			savePlayers();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return playerGroups;
	}

	public static ArrayList<String> removePermissionFromGroup(Permission permission, String group) {
		if (groups.containsKey(group)) {
			ArrayList<String> groupPerms = groups.get(group);
			if (groupPerms.contains(permission.toString())) groupPerms.remove(permission.toString());
			groups.remove(group);
			groups.put(group, groupPerms);
			try {
				saveGroups();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return groupPerms;
		} else return new ArrayList<>();
	}

	public static ArrayList<String> removePlayerFromGroup(EntityPlayer player, String group) {
		if (players.containsKey(player.getUniqueID().toString())) {
			ArrayList<String> playerGroups = players.get(player.getUniqueID().toString());
			if (playerGroups.contains(group)) playerGroups.remove(group);
			players.remove(player.getUniqueID().toString());
			players.put(player.getUniqueID().toString(), playerGroups);
			try {
				savePlayers();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return playerGroups;
		} else return new ArrayList<>();
	}

	public static ArrayList<String> removeGroup(String group) {
		if (doesGroupExist(group)) return groups.remove(group);
		else return new ArrayList<>();
	}

	public static boolean doesGroupExist(String group) {
		return groups.containsKey(group);
	}

	public static void saveGroups() throws IOException {
		Yaml yaml = new Yaml();
		yaml.dump(groups, new FileWriter(new File("config/MoreCommands/Permissions/groups.yaml")));
	}

	public static void savePlayers() throws IOException {
		Yaml yaml = new Yaml();
		yaml.dump(players, new FileWriter(new File("config/MoreCommands/Permissions/players.yaml")));
	}

	public static HashMap<String, ArrayList<String>> getGroups() {
		return groups;
	}

	public static HashMap<String, ArrayList<String>> getPlayers() {
		return players;
	}

	public static EntityPlayer getPlayer(MinecraftServer server, String name) {
		return server.getPlayerList().getPlayerByUsername(name);
	}

	public static void createFileIfNotExisting(String fileLocation) {
		if (!new File(fileLocation).exists())
			try {
				new File(fileLocation).createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	}

	public static void playSound(SoundEvent sound) {
		Minecraft.getMinecraft().player.playSound(sound, 0.2F, ((Minecraft.getMinecraft().player.getRNG().nextFloat() - Minecraft.getMinecraft().player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
	}

	public static void initialize() {
		if (!initialized) {
			if (System.console() == null) System.setProperty("jansi.passthrough", "true");
			try {
				registerEventHandler(CommandType.CLIENT, new RegistryEventHandler());
			} catch (IncorrectCommandType e1) {
				e1.printStackTrace();
			}
			Initialize.setupCommandRegistry();
			try {
				setDisplayTitle(Display.getTitle() + " with MinecraftForge");
			} catch (NoClassDefFoundError e) {}
			setupBiomeList();
			if (!new File("config/MoreCommands/").isDirectory()) new File("config/MoreCommands/").mkdirs();
			if (!new File("config/MoreCommands/Permissions/").isDirectory()) new File("config/MoreCommands/Permissions/").mkdirs();
			createFileIfNotExisting("config/MoreCommands/homes.yaml");
			createFileIfNotExisting("config/MoreCommands/warps.yaml");
			createFileIfNotExisting("config/MoreCommands/Permissions/groups.yaml");
			createFileIfNotExisting("config/MoreCommands/Permissions/players.yaml");
			try {
				loadYamlFiles();
			} catch (IOException e) {
				e.printStackTrace();
			}
			initialized = true;
		}
	}

	public static void sleep(double millis) throws InterruptedException {
		double time = System.currentTimeMillis();
		while (true)
			if (System.currentTimeMillis()-time == millis) return;
	}

	public static double ticksToMillis(double ticks) {
		return ticks / 20 * 1000;
	}

	public static double secondsToTicks(double seconds) {
		return seconds * 20;
	}

	/**
	 * Has to be ran everytime easterEgg is being played to avoid an error saying the sound has already been played.
	 * @return The easterEgg EasterEgg extending PositionedSoundRecord.
	 */
	public static EasterEgg regenerateEasterEgg() {
		easterEgg = new EasterEgg();
		return easterEgg;
	}

	public static void loadAliases() throws IOException {
		Yaml yaml = new Yaml();
		try {
			aliases = (Map<String, String>) yaml.load(joinCustomChar("\n", Files.readAllLines(Paths.get(new File("config/MoreCommands/aliases.yaml").getAbsolutePath())).toArray(new String[0])));
		} catch (ParserException e) {
			print(LogType.ERROR, "An error occured while reading aliases.yaml.");
			e.printStackTrace();
			aliases = null;
		} catch (StringIndexOutOfBoundsException e) {}
		aliases = aliases == null ? new HashMap<>() : aliases; // just making sure when the file is empty no NullPointerExceptions occur.
	}

	public static boolean doesAliasExist(String alias) {
		return aliases.containsKey(alias);
	}

	public static String getCommandFromAlias(String alias) {
		if (doesAliasExist(alias)) return aliases.get(alias);
		else return null;
	}

	public static void createAlias(String alias) {
		aliases.put(alias, "");
		try {
			saveAliases();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void editAlias(String alias, String command) {
		if (aliases.containsKey(alias)) aliases.remove(alias);
		aliases.put(alias, command);
		try {
			saveAliases();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void removeAlias(String alias) {
		if (doesAliasExist(alias)) aliases.remove(alias);
		try {
			saveAliases();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveAliases() throws IOException {
		Yaml yaml = new Yaml();
		yaml.dump(aliases, new FileWriter(new File("config/MoreCommands/aliases.yaml")));
	}

	public static Map<String, String> getAliases() {
		return aliases;
	}

	public static String[] removeArgs(String[] stringArray, Integer... args) {
		List<String> output = new ArrayList<>();
		for (int x = 0; x < stringArray.length; x++)
			if (!Arrays.asList(args).contains(x)) output.add(stringArray[x]);
		return output.toArray(new String[output.size()]);
	}

	public static String formatBlocksPerSecond() {
		String blocksPerSec = String.format("%.8f", blocksPerSecond);
		while (blocksPerSec.endsWith("0"))
			blocksPerSec = blocksPerSec.substring(0, blocksPerSec.length()-1);
		return blocksPerSec.endsWith(".") ? blocksPerSec.substring(0, blocksPerSec.length()-1) : blocksPerSec;
	}

	public static int getBlockLight(World world, BlockPos pos) {
		try {
			return world.getChunkFromBlockCoords(centerBlockPos(pos)).getLightFor(EnumSkyBlock.BLOCK, pos);
		} catch (Throwable e) {
			return 0;
		}
	}

	public static int getSkyLight(World world, BlockPos pos) {
		try {
			return world.getChunkFromBlockCoords(centerBlockPos(pos)).getLightFor(EnumSkyBlock.SKY, pos);
		} catch (Throwable e) {
			return 0;
		}
	}

	public static final String MOD_ID = "morecommands";
	public static final String MOD_NAME = "MoreCommands";
	public static final String VERSION = "1.27";
	public static final String MC_VERSIONS = "[1.11,1.12.1]";
	public static final String UPDATE_URL = "https://raw.githubusercontent.com/PlanetTeamSpeakk/MoreCommands/master/version.json";
	public static final String BUILD_DATE = "August 31st";
	public static final String[] AUTHORS = new String[] {"PlanetTeamSpeak"};
	public static EasterEgg easterEgg = null;
	public static boolean narratorActive = false;
	public static boolean sittingEnabled = true;
	public static Entity arrow = null;
	public static boolean isSittingOnChair = false;
	public static EntityPlayer player = null;
	public static HashMap<String, String> tpRequests = new HashMap<>();
	public static HashMap<String, HashMap<ICommandSender, Long>> cooldowns = new HashMap<>();
	public static HashMap<EntityPlayer, NBTTagList> inventories = new HashMap<>();
	public static HashMap<EntityPlayer, Vec3d> locations = new HashMap<>();
	public static HashMap<EntityPlayer, Integer> experiencePoints = new HashMap<>();
	public static HashMap<EntityPlayer, HashMap<String, Float>> pitchNYaws = new HashMap<>();
	public static Map<String, String> setVariables = new HashMap<>();
	public static double blocksPerSecond = 0;
	public static Vec3d lastPosition = null;
	public static Map<String, Map<String, Double>> homes = null;
	public static Map<String, Map<String, Double>> warps = null;
	public static List<String> infoOverlayConfig = new ArrayList<>();
	public static TextFormatting lastColor = TextFormatting.YELLOW;
	public static int clientTicksPassed = 0;
	public static int clientTicksPassed2 = 0;
	public static boolean easterEggLoopEnabled = false;
	public static int updated = 0;
	public static int updatesPerSecond = 0;
	private static ArrayList<Block> blockBlacklist = new ArrayList<>();
	private static ArrayList<Block> blockWhitelist = new ArrayList<>();
	private static String ipAddress = null;
	private static boolean shouldSaveInfoOverlayConfig = false;
	private static boolean overlayEnabled = false;
	public static boolean initialized = false;
	private static HashMap<String, KeyBinding> keyBindings = new HashMap<>();
	private static HashMap<String, ArrayList<String>> players = new HashMap<>();
	private static HashMap<String, ArrayList<String>> groups = new HashMap<>();
	private static Map<String, String> aliases = new HashMap<>();
	private static List<Biome> biomes = new ArrayList<>();
	private static List<ICommand> serverCommands = new ArrayList<>();
	private static List<ICommand> clientCommands = new ArrayList<>();
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

	public enum LogType {
		INFO(), ERROR(), WARN();
	}

}
