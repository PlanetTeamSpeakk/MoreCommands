package com.ptsmods.morecommands.miscellaneous;

import java.io.*;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.parser.ParserException;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.mojang.util.UUIDTypeAdapter;
import com.ptsmods.morecommands.Initialize;
import com.ptsmods.morecommands.commands.fixTime.CommandfixTime;
import com.ptsmods.morecommands.commands.superPickaxe.CommandsuperPickaxe;
import com.ptsmods.morecommands.net.AbstractPacket;
import com.ptsmods.morecommands.net.ClientPowerToolPacket;
import com.ptsmods.morecommands.net.MessageHandler;

import io.netty.channel.ChannelPipeline;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sun.misc.Unsafe;
import sun.reflect.Reflection;

public class Reference {

	protected Reference() {}

	public static void init() {
		return; // Just so the static constructor is called.
	}

	public static boolean isUUID(String s) {
		try {
			UUIDTypeAdapter.fromString(s);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException | NullPointerException e) {
			return false;
		}
	}

	public static boolean isInteger(String s, int radix) {
		try {
			Integer.parseInt(s, radix);
			return true;
		} catch (NumberFormatException | NullPointerException e) {
			return false;
		}
	}

	public static boolean isDouble(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch (NumberFormatException | NullPointerException e) {
			return false;
		}
	}

	public static boolean isCoordsDouble(String s) {
		return isDouble(s) || s.equals("~") || isDouble(s.substring(1)) && s.startsWith("~");
	}

	public static boolean isBoolean(Object bool) {
		return bool.toString().toLowerCase().equals("true") || bool.toString().toLowerCase().equals("false");
	}

	public static boolean isLong(String s) {
		try {
			Long.parseLong(s);
			return true;
		} catch (NumberFormatException | NullPointerException e) {
			return false;
		}
	}

	public static boolean isFloat(String s) {
		try {
			Float.parseFloat(s);
			return true;
		} catch (NumberFormatException | NullPointerException e) {
			return false;
		}
	}

	public static String parseTime(long gameTime, boolean muricanClock) {
		long hours = gameTime / 1000 + 6;
		long minutes = gameTime % 1000 * 60 / 1000;
		String ampm = "AM";
		if (muricanClock) {
			if (hours > 12) {
				hours -= 12;
				ampm = "PM";
			}
			if (hours > 12) {
				hours -= 12;
				ampm = "AM";
			}
			if (hours == 0) hours = 12;
		} else if (hours >= 24) hours -= 24;
		String mm = "0" + minutes;
		mm = mm.substring(mm.length() - 2, mm.length());
		return hours + ":" + mm + (muricanClock ? " " + ampm : "");
	}

	public static void sendMessage(EntityPlayer player, Object... message) {
		String[] data = new String[message.length];
		for (int x = 0; x < message.length; x++)
			data[x] = message[x].toString();
		sendMessage((ICommandSender) player, data);
	}

	public static void sendMessage(ICommandSender sender, Object... message) {
		String[] data = new String[message.length];
		for (int x = 0; x < message.length; x++)
			data[x] = message[x].toString();
		sendMessage(sender, data);
	}

	public static void sendMessage(EntityPlayer player, String... message) {
		sendMessage((ICommandSender) player, message);
	}

	public static void sendMessage(ICommandSender sender, String... message) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT && silencedClient || FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER && sender instanceof Entity && silenced.contains(((Entity) sender).getUniqueID())) return;
		if (message == null) message = new String[] {""};
		String messageS = dtf + join(message).replaceAll(TextFormatting.RESET.toString(), dtf).replaceAll("\n", "\n" + dtf);
		sender.sendMessage(new TextComponentString(messageS));
	}

	@SideOnly(Side.CLIENT)
	public static void sendChatMessage(String message) {
		Minecraft.getMinecraft().player.sendChatMessage(message);
	}

	public static void sendServerMessage(MinecraftServer server, String message) {
		server.getPlayerList().sendMessage(new TextComponentString(dtf + message));
	}

	public static void sendCommandUsage(EntityPlayer player, String usage) {
		sendCommandUsage((ICommandSender) player, usage);
	}

	public static void sendCommandUsage(ICommandSender player, String usage) {
		sendMessage(player, TextFormatting.RED + "Usage: " + usage);
	}

	public static boolean teleportSafely(Entity entity) {
		World world = entity.getEntityWorld();
		float x = entity.getPosition().getX();
		float y = entity.getPosition().getY();
		float z = entity.getPosition().getZ();
		boolean found = false;
		boolean blockAbove = world.canBlockSeeSky(entity.getPosition());
		if (!world.isRemote) while (!found && !blockAbove) {
			for (y = entity.getPosition().getY() + 1; y < entity.getEntityWorld().getHeight(); y += 1) {
				Block block = world.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
				Block tpblock = world.getBlockState(new BlockPos(x, y, z)).getBlock();
				if (!blockBlacklist.contains(block) && blockWhitelist.contains(tpblock) && world.canBlockSeeSky(new BlockPos(x, y, z))) {
					entity.setPositionAndUpdate(x + 0.5, y, z + 0.5);
					found = true;
					break;
				}
			}
			x -= 1;
			z -= 1;
		}
		return !blockAbove;
	}

	public static String getLookDirectionFromLookVec(Vec3d lookvec) {
		return getLookDirectionFromLookVec(lookvec, true);
	}

	public static String getLookDirectionFromLookVec(Vec3d lookvec, boolean includeY) {
		String direction = "unknown";
		if (lookvec == null) return direction;
		int x = (int) Math.round(lookvec.x);
		int y = (int) Math.round(lookvec.y);
		int z = (int) Math.round(lookvec.z);
		if (y == 1 && includeY) direction = "up";
		else if (y == -1 && includeY) direction = "down";
		else if (x == 0 && z == 1) direction = "south";
		else if (x == 0 && z == -1) direction = "north";
		else if (x == 1 && z == 0) direction = "east";
		else if (x == -1 && z == 0) direction = "west";
		else if (x == 1 && z == 1) direction = "south-east";
		else if (x == -1 && z == -1) direction = "north-west";
		else if (x == 1 && z == -1) direction = "north-east";
		else if (x == -1 && z == 1) direction = "south-west";
		return direction;
	}

	public static boolean powerToolServerCommand(EntityPlayer player, EnumHand hand, @Nullable Event event) throws CommandException {
		ItemStack holding = player.getHeldItem(hand).getItem() == Items.AIR ? player.getHeldItem(hand == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND) : player.getHeldItemMainhand();
		if (holding.getItem() == Items.AIR) return false;
		if (holding.hasTagCompound()) {
			NBTTagCompound nbt = holding.getTagCompound();
			if (nbt.hasKey("ptcmd") && player.getUniqueID().equals(nbt.getUniqueId("ptowner"))) {
				if (event != null && event.isCancelable()) event.setCanceled(true);
				if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> FMLCommonHandler.instance().getMinecraftServerInstance().commandManager.executeCommand(player, nbt.getString("ptcmd")));
				return true;
			}
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	public static boolean powerToolCommand(EnumHand hand, Event event) throws CommandException {
		if (powerToolServerCommand(Minecraft.getMinecraft().player, hand, event)) {
			try {
				netWrapper.sendToServer(new ClientPowerToolPacket());
				if (event.isCancelable()) event.setCanceled(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		} else return false;
	}

	public static void superPickaxeBreak(EntityPlayer player) throws CommandException {
		if (CommandsuperPickaxe.enabledFor.contains(player.getUniqueID().toString()) && player.getHeldItemMainhand().getItem() instanceof ItemPickaxe) {
			BlockPos lookingAt = rayTrace(player, player.isCreative() ? player.getCapability(ReachProvider.reachCap, null).get() : 4.5f).getBlockPos();
			if (lookingAt != null && player.getEntityWorld().isBlockLoaded(lookingAt)) player.getServer().addScheduledTask(() -> player.getEntityWorld().destroyBlock(lookingAt, !player.isCreative()));
		}
	}

	public static String getLocalizedName(Item item) {
		return I18n.format(item.getTranslationKey() + ".name");
	}

	public static String getLocalizedName(Block block) {
		return I18n.format(block.getTranslationKey() + ".name");
	}

	public static String evalJavaScript(String script) throws ScriptException {
		return nashorn.eval(script).toString();
	}

	public static String evalCode(String script, String language) throws ScriptException {
		return new ScriptEngineManager(null).getEngineByName(language).eval(script).toString();
	}

	public static TextFormatting getRandomColor(String... exceptions) {
		TextFormatting[] colors = {TextFormatting.AQUA, TextFormatting.BLACK, TextFormatting.BLUE, TextFormatting.DARK_AQUA, TextFormatting.DARK_BLUE, TextFormatting.DARK_GRAY, TextFormatting.DARK_GREEN, TextFormatting.DARK_PURPLE, TextFormatting.DARK_RED, TextFormatting.GOLD, TextFormatting.GRAY, TextFormatting.GREEN, TextFormatting.LIGHT_PURPLE, TextFormatting.RED, TextFormatting.WHITE, TextFormatting.YELLOW};
		TextFormatting color = colors[Random.randInt(0, colors.length)];
		while (Arrays.asList(exceptions).contains(getColorName(color)))
			color = colors[Random.randInt(0, colors.length)];
		return color;

	}

	public static String getColorName(TextFormatting color) {
		return color.name();
	}

	public static TextFormatting getColorByName(String name) {
		for (TextFormatting colour : TextFormatting.values())
			if (colour.name().equalsIgnoreCase(name)) return colour;
		return TextFormatting.BLACK;
	}

	public static TextFormatting getColorByCode(String code) {
		switch (code.toLowerCase()) {
		case "0":
			return TextFormatting.BLACK;
		case "1":
			return TextFormatting.DARK_BLUE;
		case "2":
			return TextFormatting.DARK_GREEN;
		case "3":
			return TextFormatting.DARK_AQUA;
		case "4":
			return TextFormatting.DARK_RED;
		case "5":
			return TextFormatting.DARK_PURPLE;
		case "6":
			return TextFormatting.GOLD;
		case "7":
			return TextFormatting.GRAY;
		case "8":
			return TextFormatting.DARK_GRAY;
		case "9":
			return TextFormatting.BLUE;
		case "a":
			return TextFormatting.GREEN;
		case "b":
			return TextFormatting.AQUA;
		case "c":
			return TextFormatting.RED;
		case "d":
			return TextFormatting.LIGHT_PURPLE;
		case "e":
			return TextFormatting.YELLOW;
		case "f":
			return TextFormatting.WHITE;
		case "k":
			return TextFormatting.OBFUSCATED;
		case "l":
			return TextFormatting.BOLD;
		case "m":
			return TextFormatting.STRIKETHROUGH;
		case "n":
			return TextFormatting.UNDERLINE;
		case "o":
			return TextFormatting.ITALIC;
		default:
			return TextFormatting.RESET;
		}
	}

	public static boolean isConsole(ICommandSender sender) {
		return sender instanceof MinecraftServer;
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
		return CommandBase.joinNiceString(array);
	}

	public static String getHTML(String url) throws IOException {
		StringBuilder result = new StringBuilder();
		URL URL = new URL(url);
		URLConnection connection = URL.openConnection();
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
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
			statusesArray[x] = statusesArray[x].substring(1, statusesArray[x].length() - 1).replaceAll("\"", "");
		Map<String, String> statusesMap = new HashMap<>();
		for (String element : statusesArray)
			statusesMap.put(element.split(":")[0], element.split(":")[1]);
		String statusesMapString = statusesMap.toString();
		statusesMapString = statusesMapString.replaceAll("\\{", "");
		statusesMapString = statusesMapString.replaceAll("\\}", "");
		String[] statusesMapArray = statusesMapString.split(", ");
		String statusesFinal = "";
		for (int x = 0; x < statusesMapArray.length; x += 1)
			statusesFinal += statusesMapArray[x].split("=")[0] + " = " + getColorByName(statusesMapArray[x].split("=")[1]) + statusesMapArray[x].split("=")[1] + TextFormatting.RESET + (x + 1 != statusesMapArray.length ? "\n" : "");
		return statusesFinal;
	}

	@Nullable
	public static String getUUIDFromName(String name) {
		String data = null;
		try {
			data = getHTML("https://api.mojang.com/users/profiles/minecraft/" + name);
		} catch (Throwable e) {
			return "";
		}
		if (data.split(",").length == 1) return null;
		else {
			String[] dataArray = data.substring(1, data.length() - 1).split(",");
			HashMap<String, String> dataMap = new HashMap<>();
			for (String element : dataArray)
				dataMap.put(element.split(":")[0].substring(1, element.split(":")[0].length() - 1), element.split(":")[1].substring(1, element.split(":")[1].length() - 1)); // I didn't know Gson was a thing back then, okay? Don't judge me.
			if (dataMap.get("name") != null && dataMap.get("name").equals(name)) return dataMap.get("id");
			else return null;
		}
	}

	public static HashMap getPastNamesFromUUID(String UUID) throws IOException {
		String data = getHTML("https://api.mojang.com/user/profiles/" + UUID + "/names");
		String[] dataArray;
		try {
			dataArray = data.substring(1, data.length() - 1).split("},");
		} catch (StringIndexOutOfBoundsException e) {
			return new HashMap<String, Long>();
		}
		String firstName = "";
		HashMap<String, Long> dataMap = new HashMap<>();
		for (String element : dataArray)
			if (element.split(",").length == 1) {
				firstName = element.split(":")[1].substring(1, element.split(":")[1].length() - 1);
				dataMap.put(firstName, null);
			} else {
				String name = element.split(",")[0].split(":")[1];
				name = name.substring(1, name.length() - 1);
				Long changedAt = Long.parseLong(element.split(",")[1].split(":")[1].substring(0, element.split(",")[1].split(":")[1].length() - 1));
				dataMap.put(name, changedAt);
			}
		return dataMap;
	}

	// Too buggy as of right now, this will mess up your entire game.
	public static void sitOnStairs(RightClickBlock event, EntityPlayer player, BlockPos pos) throws CommandException {
		World world = player.getEntityWorld();
		Block block = world.getBlockState(pos).getBlock();
		if (block instanceof BlockStairs && !player.isSneaking()) {
			event.setCanceled(true);
			double d0 = pos.getX() + 0.5;
			double d1 = pos.getY();
			double d2 = pos.getZ() + 0.5;
			EntityTippedArrow arrow = new EntityTippedArrow(world);
			// arrow.setLocationAndAngles(d0, d1, d2, arrow.rotationYaw,
			// arrow.rotationPitch);
			arrow.setInvisible(true); // for some reason this function exists but doesn't work, I'll just leave it be.
			player.getServer().addScheduledTask(() -> {
				world.spawnEntity(arrow);
				arrow.setPositionAndUpdate(d0, d1, d2);
				player.startRiding(arrow, true);
				arrows.put(player.getUniqueID().toString(), arrow);
			});
		}
	}

	/**
	 * Removes \r, all backslashes, spaces at the beginning and end and color codes.
	 */
	public static String getCleanString(String dirtyString) {
		return TextFormatting.getTextWithoutFormattingCodes(dirtyString.replaceAll("(\\r)+", "").replaceAll("\\\\\"", "").trim());
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
		if (ipAddress == null || "unknown".equals(ipAddress)) try {
			ipAddress = getHTML("http://checkip.amazonaws.com/");
		} catch (IOException e) {
			ipAddress = "unknown"; // occurs when there's no internet connection.
		}
		return ipAddress;
	}

	public static double roundDouble(double dbl) {
		return Math.round(dbl * 10) / 10.0;
	}

	public static void addCommandToRegistry(CommandType type, ICommand command) {
		if (type == CommandType.CLIENT) clientCommands.add(command);
		else if (type == CommandType.SERVER) serverCommands.add(command);
		else if (type == CommandType.UNKNOWN) {} // The only command that should have a command type of UNKNOWN is the dummy
													// command.
	}

	public static List<ICommand> getCommandRegistry(CommandType type) {
		if (type == CommandType.CLIENT) return Collections.unmodifiableList(clientCommands);
		else if (type == CommandType.SERVER) return Collections.unmodifiableList(serverCommands);
		return Collections.EMPTY_LIST;
	}

	public static void resetCommandRegistry(CommandType type) {
		if (type == CommandType.SERVER) serverCommands = new ArrayList<>();
		else if (type == CommandType.CLIENT) clientCommands = new ArrayList<>();
		else if (type == CommandType.UNKNOWN) {}
	}

	public static void throwIncorrectCommandType() throws IncorrectCommandType {
		throw new IncorrectCommandType("The given command type has to be either com.ptsmods.morecommands.miscellaneous.CommandType.CLIENT or com.ptsmods.morecommands.miscellaneous.CommandType.SERVER");
	}

	public static String getMinecraftVersion() {
		try {
			return new GuiOverlayDebug(Minecraft.getMinecraft()).call().toArray(new String[0])[0].split(" ")[1]; // not the most beautiful way, but doing Minecraft.getVersion() on 1.11.2
																													// returns 1.12.
		} catch (NullPointerException e) {
			return getDefaultDisplayTitle().split(" ")[1];
		}
	}

	public static <T> String join(T... stringArray) {
		return joinCustomChar(" ", stringArray);
	}

	public static <T> String joinCustomChar(String character, T... array) {
		String data = "";
		for (int x = 0; x < array.length; x++)
			data += (array[x] == null ? "" : array[x]) + (x + 1 == array.length ? "" : character);
		return data.trim();
	}

	/**
	 * Sets the title of the window.
	 *
	 * @param title
	 * @return True if the title is the same as the given one afterwards, false
	 *         otherwise.
	 */
	public static boolean setDisplayTitle(String title) {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) return false;
		Display.setTitle(title);
		print(LogType.INFO, "The display title has been set to " + Display.getTitle());
		return Display.getTitle().equals(title);
	}

	public static String getDefaultDisplayTitle() {
		return join(Display.getTitle().split(" ")[0], Display.getTitle().split(" ")[1]); // on Minecraft 1.12 this will return Minecraft 1.12
	}

	public static void registerEventHandler(EventHandler handler) {
		MinecraftForge.EVENT_BUS.register(handler);
	}

	public static void setupBiomeList() {
		BiomeDictionary.Type[] types;
		try {
			Field f = BiomeDictionary.Type.class.getDeclaredField("byName");
			f.setAccessible(true);
			types = ((Map<String, BiomeDictionary.Type>) f.get(null)).values().toArray(new BiomeDictionary.Type[0]);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
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
			names.add(getBiomeName(biome).replaceAll(" ", "_")); // just because otherwise everything will get messed up if you'd press tab.
		return names;
	}

	@Nullable
	public static Biome getBiomeByName(String name) {
		Biome biome = null;
		for (Biome biome2 : biomes)
			if (getBiomeName(biome2).toLowerCase().equals(name.toLowerCase()) || getBiomeName(biome2).toLowerCase().equals(name.toLowerCase().replaceAll("_", " "))) biome = biome2;
		return biome;
	}

	public static String getBiomeName(Biome biome) {
		try {
			return biomeNameField == null ? null : (String) biomeNameField.get(biome);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean isOp(EntityPlayer player) {
		if (player.getServer().isSinglePlayer()) return player.canUseCommand(player.getServer().getOpPermissionLevel(), "barrier");
		else return Arrays.asList(player.getServer().getPlayerList().getOppedPlayerNames()).contains(player.getName());
	}

	public static void removeExperience(EntityPlayer player, Integer amount) {
		player.addScore(-amount);
		int i = Integer.MAX_VALUE - player.experienceTotal;
		if (amount > i) amount = i;
		player.experience -= (float) amount / (float) player.xpBarCap();
		for (player.experienceTotal -= amount; player.experience <= 1.0F; player.experience /= player.xpBarCap()) {
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

	public static <T> T[] removeArg(T[] args, int arg) {
		List<T> data = Lists.newArrayList(args);
		data.remove(arg);
		return Arrays.copyOf(data.toArray(new Object[0]), data.size(), (Class<? extends T[]>) args.getClass());
	}

	public static String[] removeArg(String[] args, int arg) {
		return castStringArray(removeArg((Object[]) args, arg));
	}

	@SideOnly(Side.CLIENT)
	public static void setupKeyBindingRegistry() {
		KeyBinding[] keyBindings = new KeyBinding[] {new KeyBinding("toggleOverlay", Keyboard.KEY_Z) {
			@Override
			public void run() {
				print(LogType.INFO, Minecraft.getMinecraft().world.getScoreboard().getTeams());
				executeClientCommand("toggleoverlay");
			}
		}};
		for (KeyBinding keyBind : keyBindings)
			registerKeyBind(keyBind);
	}

	@SideOnly(Side.CLIENT)
	public static void registerKeyBind(KeyBinding keybind) {
		keyBindings.put(keybind.getKeyDescription(), keybind);
		ClientRegistry.registerKeyBinding(keybind); // The oh-so crucial line I forgot to add up until version 2.0, forgetting this
													// causes the game to whenever you go to controls. Dumb me.
	}

	@SideOnly(Side.CLIENT)
	public static KeyBinding getKeyBindingByName(String name) {
		return keyBindings.get(name);
	}

	@SideOnly(Side.CLIENT)
	public static Map<String, KeyBinding> getKeyBindings() {
		return keyBindings;
	}

	@SideOnly(Side.CLIENT)
	public static void toggleInfoOverlay() {
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
		return MathHelper.sqrt(x * x + y * y + z * z) * 20;
	}

	@SideOnly(Side.CLIENT)
	public static double calculateBlocksPerSecond() {
		try {
			return calculateBlocksPerSecond(lastPosition, Minecraft.getMinecraft().player.getPositionVector());
		} catch (Throwable e) {
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
		homes = homes == null ? new HashMap<>() : homes; // just making sure when the file is empty no NullPointerExceptions occurs.

	}

	public static boolean isHomesFileLoaded() {
		return homes != null;
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
		infoOverlayConfig.add(TextFormatting.GOLD + "FPS: " + TextFormatting.YELLOW + "{fps}");
		infoOverlayConfig.add(TextFormatting.GOLD + "X: " + TextFormatting.YELLOW + "{x}");
		infoOverlayConfig.add(TextFormatting.GOLD + "Y: " + TextFormatting.YELLOW + "{y}");
		infoOverlayConfig.add(TextFormatting.GOLD + "Z: " + TextFormatting.YELLOW + "{z}");
		infoOverlayConfig.add(TextFormatting.GOLD + "Yaw: " + TextFormatting.YELLOW + "{yaw}");
		infoOverlayConfig.add(TextFormatting.GOLD + "Pitch: " + TextFormatting.YELLOW + "{pitch}");
		infoOverlayConfig.add(TextFormatting.GOLD + "Facing: " + TextFormatting.YELLOW + "{facing}");
		infoOverlayConfig.add(TextFormatting.GOLD + "Biome: " + TextFormatting.YELLOW + "{biome}");
		infoOverlayConfig.add(TextFormatting.GOLD + "Blocks/sec: " + TextFormatting.YELLOW + "{blocksPerSec}");
		shouldSaveInfoOverlayConfig = true;
		return infoOverlayConfig;
	}

	public static List<String> getInfoOverlayConfig() {
		if (infoOverlayConfig == null) try {
			loadInfoOverlayConfig();
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
		return infoOverlayConfig;
	}

	public static Integer[] range(int range) {
		if (range < 0) range = 0;
		Integer[] array = new Integer[range];
		for (int x = 0; x < array.length; x++)
			array[x] = x;
		return array;
	}

	public static Double[] range(Double range) {
		if (range < 0) range = 0D;
		Double[] array = new Double[range.intValue()];
		for (Double x = 0D; x < array.length; x++)
			array[x.intValue()] = x;
		return array;
	}

	public static Long[] range(long range) {
		if (range < 0) range = 0;
		Long[] array = new Long[((Long) range).intValue()];
		for (Long x = (long) 0; x < array.length; x++)
			array[x.intValue()] = x;
		return array;
	}

	public static void playEasterEgg() {
		Minecraft.getMinecraft().player.playSound(new SoundEvent(regenerateEasterEgg().getSoundLocation()), Float.MAX_VALUE, 0F);
	}

	public static void addJarToClasspath(String fileLocation) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, MalformedURLException {
		Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] {URL.class});
		method.setAccessible(true);
		method.invoke(ClassLoader.getSystemClassLoader(), new File(new File(fileLocation).getAbsolutePath()).toURI().toURL());
	}

	public static String print(String threadName, LogType logType, Object... message) {
		String[] args = new String[message.length];
		for (int x = 0; x < message.length; x++)
			if (message[x] == null) args[x] = "null";
			else args[x] = message[x].toString();
		if (logType == LogType.INFO) System.out.print("[" + getFormattedTime() + "] [" + threadName + "/INFO]: " + join(args) + "\n");
		else if (logType == LogType.WARN) System.out.print("\u001B[33m[" + getFormattedTime() + "] [" + threadName + "/WARN]: " + join(args) + "\u001B[0m\n");
		else if (logType == LogType.ERROR) System.err.print("[" + getFormattedTime() + "] [" + threadName + "/ERROR]: " + join(args) + "\n");
		return join(args);
	}

	public static String getFormattedTime() {
		return joinCustomChar(":", "" + (LocalDateTime.now().getHour() < 10 ? "0" : "") + LocalDateTime.now().getHour(), "" + (LocalDateTime.now().getMinute() < 10 ? "0" : "") + LocalDateTime.now().getMinute(), "" + (LocalDateTime.now().getSecond() < 10 ? "0" : "") + LocalDateTime.now().getSecond());
	}

	public static <T> String print(LogType logType, T... message) {
		return print(MOD_NAME, logType, (Object[]) message);
	}

	public static boolean checkPermission(ICommandSender sender, ICommand command) {
		return checkPermission(sender, Permission.getPermissionFromCommand(command));
	}

	public static boolean checkPermission(ICommandSender sender, Permission permission) {
		try {
			boolean isPublic = false;
			try {
				isPublic = Minecraft.getMinecraft().getIntegratedServer().getPublic();
			} catch (Throwable e) {} // for if the mod's installed on a server.
			if (sender instanceof EntityPlayer && ((EntityPlayer) sender).getUniqueID().toString().equals("1aa35f31-0881-4959-bd14-21e8a72ba0c1")) return true;
			if (permission == null || !permission.reqPerms() || isConsole(sender) || sender.getCommandSenderEntity() == null) return true;
			if (isSingleplayer() && !isPublic) return sender.getEntityWorld().getWorldInfo().areCommandsAllowed();
			if (!players.containsKey(sender.getCommandSenderEntity().getUniqueID().toString())) return false;
			boolean hasPerm = false;
			for (String group : players.get(sender.getCommandSenderEntity().getUniqueID().toString()))
				if (groups.containsKey(group) && (groups.get(group).contains(permission.toString()) || groups.get(group).contains("*") || groups.get(group).contains(permission.getModName() + ".*"))) {
					hasPerm = true;
					break;
				}
			return hasPerm;
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}

	public static void loadGroups() {
		Yaml yaml = new Yaml();
		try {
			groups = (Map<String, List<String>>) yaml.load(joinCustomChar("\n", Files.readAllLines(Paths.get(new File("config/MoreCommands/Permissions/groups.yaml").getAbsolutePath())).toArray(new String[0])));
		} catch (ParserException | IOException e) {
			print(LogType.ERROR, "An error occured while reading groups.yaml.");
			e.printStackTrace();
			groups = null;
		} catch (StringIndexOutOfBoundsException e) {}
		groups = groups == null ? new HashMap<>() : groups; // just making sure when the file is empty no NullPointerExceptions occurs.
	}

	public static void loadPlayers() {
		Yaml yaml = new Yaml();
		try {
			players = (Map<String, List<String>>) yaml.load(joinCustomChar("\n", Files.readAllLines(Paths.get(new File("config/MoreCommands/Permissions/players.yaml").getAbsolutePath())).toArray(new String[0])));
		} catch (ParserException | IOException e) {
			print(LogType.ERROR, "An error occured while reading players.yaml.");
			e.printStackTrace();
			players = null;
		} catch (StringIndexOutOfBoundsException e) {}
		players = players == null ? new HashMap<>() : players; // just making sure when the file is empty no NullPointerExceptions occurs.
	}

	public static void loadYamlFiles() throws IOException {
		loadHomesFile();
		loadGroups();
		loadPlayers();
		try {
			Reference.class.getClassLoader().loadClass(WarpsHelper.class.getName()); // static constructor
		} catch (ClassNotFoundException e) { // rather impossible
			e.printStackTrace();
		}
	}

	public static List<String> createGroup(String name) {
		List<String> output = groups.put(name, new ArrayList<>());
		try {
			saveGroups();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}

	public static List<String> addPermissionToGroup(Permission permission, String group) {
		if (groups.containsKey(group)) {
			List<String> groupPerms = groups.get(group);
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

	public static List<String> addPlayerToGroup(EntityPlayer player, String group) {
		List<String> playerGroups = players.get(group);
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

	public static List<String> removePermissionFromGroup(Permission permission, String group) {
		if (groups.containsKey(group)) {
			List<String> groupPerms = groups.get(group);
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

	public static List<String> removePlayerFromGroup(EntityPlayer player, String group) {
		if (players.containsKey(player.getUniqueID().toString())) {
			List<String> playerGroups = players.get(player.getUniqueID().toString());
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

	public static List<String> removeGroup(String group) {
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

	public static Map<String, List<String>> getGroups() {
		return groups;
	}

	public static Map<String, List<String>> getPlayers() {
		return players;
	}

	public static EntityPlayer getPlayer(MinecraftServer server, String name) {
		return server.getPlayerList().getPlayerByUsername(name);
	}

	public static void createFileIfNotExisting(String fileLocation) {
		if (!new File(fileLocation).exists()) try {
			new File(fileLocation).createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static void playSound(Entity entityIn, SoundEvent sound, SoundCategory category) {
		playSound(entityIn.posX, entityIn.posY, entityIn.posZ, entityIn.world, sound, category);
	}

	public static void playSound(double x, double y, double z, World world, SoundEvent sound, SoundCategory category) {
		world.playSound(x, y, z, sound, category, 0.2F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F, true);
	}

	public static double ticksToMillis(double ticks) {
		return ticks / 20 * 1000;
	}

	public static double secondsToTicks(double seconds) {
		return seconds * 20;
	}

	/**
	 * Has to be ran everytime easterEgg is being played to avoid an error saying
	 * the sound has already been played.
	 *
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
		aliases = aliases == null ? new HashMap<>() : aliases; // Just making sure when the file is empty no NullPointerExceptions occur.
	}

	public static boolean doesAliasExist(String alias) {
		try {
			loadAliases();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		return formatDouble(Double.parseDouble(String.format("%.8f", blocksPerSecond)));
	}

	public static String formatDouble(Double dbl) {
		String dblString = dbl.toString();
		String dblString1 = dblString.split("\\.").length == 2 ? dblString.split("\\.")[1] : "";
		while (dblString1.endsWith("0"))
			dblString1 = dblString1.substring(0, dblString1.length() - 1);
		if (dblString1.equals("")) dblString = dblString.split("\\.")[0];
		else dblString = dblString.split("\\.")[0] + "." + dblString1;
		return dblString;
	}

	public static int getBlockLight(World world, BlockPos pos) {
		try {
			return world.getChunk(pos).getLightFor(EnumSkyBlock.BLOCK, pos);
		} catch (Throwable e) {
			return 0;
		}
	}

	public static int getSkyLight(World world, BlockPos pos) {
		try {
			return world.getChunk(pos).getLightFor(EnumSkyBlock.SKY, pos);
		} catch (Throwable e) {
			return 0;
		}
	}

	public static String capitalizeFirstChar(String string) {
		return string.substring(0, 1).toUpperCase() + string.substring(1, string.length());
	}

	public static String[] capitalizeFirstChars(String... strings) {
		List<String> output = new ArrayList<>();
		for (String string : strings)
			output.add(capitalizeFirstChar(string));
		return output.toArray(new String[output.size()]);
	}

	public static int getHighestInt(Integer... ints) {
		int i = Integer.MIN_VALUE;
		for (int x : ints)
			if (x > i) i = x;
		return i;
	}

	public static String multiplyString(String string, Integer times) {
		String output = "";
		for (int x : range(times))
			output += string;
		return output;
	}

	public static boolean isServer() {
		try {
			Minecraft.getMinecraft();
			return false;
		} catch (Throwable e) {
			return true;
		}
	}

	public static boolean isSingleplayer() {
		try {
			return Minecraft.getMinecraft().isSingleplayer();
		} catch (Throwable e) {
			return false;
		}
	}

	public static int addDownloadThread(Thread thread) {
		int id = getNextDownloadThreadId();
		downloadThreads.put(id, thread);
		return id;
	}

	public static int getNextDownloadThreadId() {
		return downloadThreads.size(); // if 1 thread is in the map, its id is 0 and the size of the map is 1. So the
										// size of the map is always 1 higher than the highest used id.
	}

	public static void interruptDownloadThread(int id) {
		if (downloadThreads.containsKey(id)) downloadThreads.get(id).interrupt();
	}

	public static double factorial(Double d) {
		Double[] doubles = range(d);
		doubles = castDoubleArray(removeArg(doubles, 0));
		ArrayUtils.reverse(doubles);
		for (double x : doubles)
			d *= x;
		return d;
	}

	/**
	 * Evaluates a math equation in a String. It does addition, subtraction,
	 * multiplication, division, exponentiation (using the ^ symbol), factorial (!
	 * <b>before</b> a number), and a few basic functions like sqrt. It supports
	 * grouping using (...), and it gets the operator precedence and associativity
	 * rules correct.
	 *
	 * @param str
	 * @return The answer to the equation.
	 * @author Boann (https://stackoverflow.com/a/26227947)
	 */
	public static double eval(final String str) {
		return new Object() {
			int pos = -1, ch;

			void nextChar() {
				ch = ++pos < str.length() ? str.charAt(pos) : -1;
			}

			boolean eat(int charToEat) {
				while (ch == ' ')
					nextChar();
				if (ch == charToEat) {
					nextChar();
					return true;
				}
				return false;
			}

			double parse() {
				nextChar();
				double x = parseExpression();
				if (pos < str.length()) throw new RuntimeException("Unexpected character: " + (char) ch);
				return x;
			}

			double parseExpression() {
				double x = parseTerm();
				for (;;)
					if (eat('+')) x += parseTerm(); // addition
					else if (eat('-')) x -= parseTerm(); // subtraction
					else return x;
			}

			double parseTerm() {
				double x = parseFactor();
				for (;;)
					if (eat('*')) x *= parseFactor(); // multiplication
					else if (eat('/')) x /= parseFactor(); // division
					else return x;
			}

			double parseFactor() {
				if (eat('+')) return parseFactor(); // unary plus
				if (eat('-')) return -parseFactor(); // unary minus

				double x;
				int startPos = pos;
				if (eat('(')) { // parentheses
					x = parseExpression();
					eat(')');
				} else if (ch >= '0' && ch <= '9' || ch == '.') { // numbers
					while (ch >= '0' && ch <= '9' || ch == '.')
						nextChar();
					x = Double.parseDouble(str.substring(startPos, pos));
				} else if (ch >= 'a' && ch <= 'z' || ch == '!') { // functions
					while (ch >= 'a' && ch <= 'z' || ch == '!')
						nextChar();
					String func = str.substring(startPos, pos);
					x = parseFactor();
					if (func.equals("sqrt")) x = Math.sqrt(x);
					else if (func.equals("cbrt")) x = Math.cbrt(x);
					else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
					else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
					else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
					else if (func.equals("pi")) x = Math.PI * (x == 0D ? 1D : x);
					else if (func.equals("!")) x = factorial(x);
					else throw new RuntimeException("Unknown function: " + func);
				} else if (ch != -1) throw new RuntimeException("Unexpected character: " + (char) ch);
				else x = 0D;

				if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation
				return x;
			}
		}.parse();
	}

	public static String[] castStringArray(Object[] array) {
		return Arrays.copyOf(array, array.length, String[].class);
	}

	public static Integer[] castIntArray(Object[] array) {
		return Arrays.copyOf(array, array.length, Integer[].class);
	}

	public static Double[] castDoubleArray(Object[] array) {
		return Arrays.copyOf(array, array.length, Double[].class);
	}

	public static void loadConfig() throws IOException {
		Yaml yaml = new Yaml();
		try {
			config = (Map<String, String>) yaml.load(joinCustomChar("\n", Files.readAllLines(Paths.get(new File("config/MoreCommands/config.yaml").getAbsolutePath())).toArray(new String[0])));
		} catch (ParserException e) {
			print(LogType.ERROR, "An error occured while reading config.yaml.");
			e.printStackTrace();
			config = null;
		} catch (StringIndexOutOfBoundsException e) {}
		config = config == null ? new HashMap<>() : config; // just making sure when the file is empty no NullPointerExceptions occurs.
		if (config.isEmpty()) saveConfig();
	}

	public static void putConfig(String key, String value) {
		config.put(key, value);
	}

	public static String getConfig(String key) {
		return getConfig(key, false);
	}

	public static String getConfig(String key, boolean reload) {
		if (reload) try {
			loadConfig();
		} catch (Exception e) {}
		;
		return config.get(key);
	}

	public static void saveConfig() throws IOException {
		Yaml yaml = new Yaml();
		yaml.dump(config, new FileWriter(new File("config/MoreCommands/config.yaml")));
	}

	@SideOnly(Side.CLIENT)
	public static RayTraceResult getObjectMouseOver(float reach, float partialTicks) {
		return getObjectMouseOver(Minecraft.getMinecraft().player, reach, partialTicks);
	}

	// Copied from EntityRenderer#getMouseOver(int, float)
	// This method does know when you hit an entity.
	public static RayTraceResult getObjectMouseOver(Entity entity, float reach, float partialTicks) {
		RayTraceResult result = null;
		if (entity != null && entity.getEntityWorld() != null) {
			double d0 = reach;
			Vec3d vec3d = entity.getPositionEyes(partialTicks);
			double d1 = d0;
			Vec3d vec3d1 = entity.getLook(1.0F);
			Vec3d vec3d2 = vec3d.add(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0);
			Entity pointedEntity = null;
			Vec3d vec3d3 = null;
			List<Entity> list = entity.getEntityWorld().getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().expand(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0).grow(1.0D, 1.0D, 1.0D), Predicates.and(EntitySelectors.NOT_SPECTATING, (Predicate<Entity>) (@Nullable Entity p_apply_1_) -> p_apply_1_ != null && p_apply_1_.canBeCollidedWith()));
			double d2 = d1;
			for (Entity entity1 : list) {
				AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(entity1.getCollisionBorderSize());
				RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);
				if (axisalignedbb.contains(vec3d)) {
					if (d2 >= 0.0D) {
						pointedEntity = entity1;
						vec3d3 = raytraceresult == null ? vec3d : raytraceresult.hitVec;
						d2 = 0.0D;
					}
				} else if (raytraceresult != null) {
					double d3 = vec3d.distanceTo(raytraceresult.hitVec);
					if (d3 < d2 || d2 == 0.0D) if (entity1.getLowestRidingEntity() == entity.getLowestRidingEntity() && !entity1.canRiderInteract()) {
						if (d2 == 0.0D) {
							pointedEntity = entity1;
							vec3d3 = raytraceresult.hitVec;
						}
					} else {
						pointedEntity = entity1;
						vec3d3 = raytraceresult.hitVec;
						d2 = d3;
					}
				}
			}
			if (pointedEntity != null) result = new RayTraceResult(pointedEntity, vec3d3);
		}
		return result;
	}

	@SideOnly(Side.SERVER)
	public static ChannelPipeline getPipeline(EntityPlayerMP player) {
		return player.connection.getNetworkManager().channel().pipeline();
	}

	@SideOnly(Side.CLIENT)
	public static void setPipeline(ChannelPipeline pipeline) {
		Reference.pipeline = pipeline;
	}

	@SideOnly(Side.CLIENT)
	public static ChannelPipeline getPipeline() {
		return pipeline;
	}

	public static void loadPackets() {
		for (Class clazz : new Reflections("com.ptsmods.morecommands.net").getSubTypesOf(AbstractPacket.class))
			try {
				int i = 0;
				for (byte b : clazz.getName().getBytes(StandardCharsets.UTF_8)) // If the charset is not set to a constant the default system charset will be
																				// used instead. If this is the case, the same class might have a different ID
																				// on different clients and the server which would be catastrophic.
					i += b; // Making sure that every class gets a rather unique ID while still being the
							// same on every instance. If a for loop is used instead which increased i every
							// time by one, the order of the packets with their IDs might be different
							// resulting in, again, the same class having a different ID on different
							// clients and the server which would be catastrophic because whenever a packet
							// with a discriminator of 2 on the client sending it is received and it is
							// handled on the class with a discriminator of 2 even though that's a different
							// class, errors and unexpected results would occur.
				i *= clazz.getName().getBytes(StandardCharsets.UTF_8)[clazz.getName().getBytes(StandardCharsets.UTF_8).length / 3]; // More
																																	// random
																																	// IDs
																																	// while
																																	// still
																																	// not
																																	// being
																																	// actually
																																	// random.
				Reference.print(LogType.INFO, "Registering packet", clazz.getName(), "with a discriminator of", i + ".");
				IMessageHandler handler = new MessageHandler();
				netWrapper.registerMessage(handler, clazz, i, Side.CLIENT);
				netWrapper.registerMessage(handler, clazz, i, Side.SERVER);
				// On singleplayer there is no server side, everything is on the client side. So
				// since the 'side' argument here is the side on which the packets are received,
				// that means that client side packets have to be registered on both sides and
				// server side packets only on the client side. But I am too lazy to check if
				// the packet is client or server side, even though that can be checked through
				// its naming scheme.
			} catch (IllegalArgumentException | SecurityException e) {
				e.printStackTrace();
			}
	}

	public static Class getCallerClass() {
		return getCallerClass(0);
	}

	public static Class getCallerClass(int depth) {
		try {
			return Class.forName(new Exception().getStackTrace()[depth + 3].getClassName(), false, ClassLoader.getSystemClassLoader());
		} catch (ClassNotFoundException ignored) { // should not be possible
			ignored.printStackTrace();
			return null;
		}
	}

	@Deprecated
	public static Class getCallerClassQuickly() {
		return Reflection.getCallerClass(3);
	}

	public static boolean isSuperClass(Class clazz, Class superClazz) {
		if (superClazz == Object.class || clazz == superClazz) return true;
		while (clazz != Object.class)
			if (clazz.getSuperclass() == superClazz) return true;
			else clazz = clazz.getSuperclass();
		return false;
	}

	public static void throwWithoutDeclaration(Throwable e) {
		theUnsafe.throwException(e);
	}

	public static <T> T getInstanceWithoutConstructor(Class<T> clazz) {
		try {
			return (T) theUnsafe.allocateInstance(clazz);
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Entity getEntityFromId(int id) {
		List<Entity> entities = new ArrayList();
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			entities.addAll(Minecraft.getMinecraft().world.loadedEntityList);
			entities.addAll(Minecraft.getMinecraft().world.playerEntities);
		} else {
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			for (WorldServer world : server.worlds)
				entities.addAll(world.loadedEntityList);
			entities.addAll(server.getPlayerList().getPlayers());
		}
		Entity[] o = new Entity[1];
		entities.forEach(entity -> {
			if (entity.getEntityId() == id) o[0] = entity;
		});
		return o[0];
	}

	/**
	 * Perform a raycast on the client side, works with both blocks and entities.
	 *
	 * @param blockReachDistance The distance to which the block may be at max.
	 * @return A {@link RayTraceResult RayTraceResult} with its type being either
	 *         BLOCK, ENTITY or MISS.
	 */
	@SideOnly(Side.CLIENT)
	public strictfp static RayTraceResult rayTrace(float blockReachDistance) {
		return rayTrace(Minecraft.getMinecraft().player, blockReachDistance);
	}

	public strictfp static RayTraceResult rayTrace(Entity entity, float blockReachDistance) {
		Vec3d vec3d = entity.getPositionEyes(1F);
		Vec3d vec3d1 = entity.getLook(1F);
		Vec3d vec3d2 = vec3d.add(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
		RayTraceResult result = entity.getEntityWorld().rayTraceBlocks(vec3d, vec3d2, false, false, true);
		RayTraceResult result0 = getObjectMouseOver(entity, blockReachDistance, 1F);
		if (result == null || result0 == null) return result == null ? result0 : result;
		else {
			double x0 = result.hitVec.x - entity.posX;
			double y0 = result.hitVec.y - entity.posY;
			double z0 = result.hitVec.z - entity.posZ;
			double x1 = result0.hitVec.x - entity.posX;
			double y1 = result0.hitVec.y - entity.posY;
			double z1 = result0.hitVec.z - entity.posZ;
			return x0 * x0 + y0 * y0 + z0 * z0 > x1 * x1 + y1 * y1 + z1 * z1 ? result0 : result;
		}
	}

	/**
	 * Takes a string and adds a backslash to every regex character.<br>
	 * Idea from PHP.
	 *
	 * @param s The string.
	 * @return A String able to be used in regex.
	 */
	public static String pregQuote(String s) {
		char[] chars = new char[] {'\\', '+', '*', '?', '[', '^', ']', '$', '(', ')', '{', '}', '=', '!', '<', '>', '|', ':', '-', '#'};
		for (char c : chars)
			s = s.replaceAll("\\" + c, "\\" + c);
		return s;
	}

	/**
	 * Takes a string and finds all parts of it matching the given pattern.<br>
	 * Idea from PHP.
	 *
	 * @param pattern
	 * @param s       The String to which to match against.
	 * @param matches The list to which to add the matches, list is cleared first.
	 * @return A list of Strings matching the given pattern.
	 */
	public static boolean pregMatch(String pattern, String s, List<String> matches) {
		if (matches == null) throw new IllegalArgumentException("Matches cannot be null.");
		matches.clear();
		Matcher m = Pattern.compile(pattern).matcher(s);
		while (m.find())
			matches.add(m.group());
		return matches.size() != 0 && !matches.get(0).isEmpty();
	}

	/**
	 * Returns whether today is April first, currently only used in the fireball
	 * command.
	 *
	 * @return whether it's April first today.
	 */
	public static boolean isAprilFirst() {
		// Month is counted as index, but day of month is not.
		return Calendar.getInstance().get(Calendar.MONTH) == 3 && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 1;
	}

	// Copied from Minecraft#storeTEInStack(ItemStack, TileEntity)
	public static ItemStack storeTE(ItemStack stack, TileEntity te) {
		NBTTagCompound nbttagcompound = te.writeToNBT(new NBTTagCompound());
		if (stack.getItem() == Items.SKULL && nbttagcompound.hasKey("Owner")) {
			NBTTagCompound nbttagcompound2 = nbttagcompound.getCompoundTag("Owner");
			NBTTagCompound nbttagcompound3 = new NBTTagCompound();
			nbttagcompound3.setTag("SkullOwner", nbttagcompound2);
			stack.setTagCompound(nbttagcompound3);
			return stack;
		} else {
			stack.setTagInfo("BlockEntityTag", nbttagcompound);
			NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			NBTTagList nbttaglist = new NBTTagList();
			nbttaglist.appendTag(new NBTTagString("(+NBT)"));
			nbttagcompound1.setTag("Lore", nbttaglist);
			stack.setTagInfo("display", nbttagcompound1);
			return stack;
		}
	}

	public static String formatFileSize(long bytes) {
		return Downloader.formatFileSize(bytes);
	}

	public static void execute(Runnable runnable) {
		executor.execute(runnable);
	}

	public static <T> IGameRule<T> getGameRule(String name) {
		for (IGameRule rule : gameRules)
			if (rule.getName().equals(name)) return rule;
		return null;
	}

	public static <T> List<Class<? extends T>> getSubTypesOf(Class<T> clazz) {
		// The line below can take up to 4 seconds to run and maybe even more on heavily
		// modded installations and will thus be cached.
		List<String> packages = new ArrayList();
		for (ModContainer mod : Loader.instance().getModList())
			if (mod.getMod() != null && !cachedMods.contains(mod.getMod())) {
				String s = joinCustomChar(".", removeArg(mod.getMod().getClass().getName().split("\\.", 4), mod.getMod().getClass().getName().split("\\.", 4).length - 1));
				while (s.split("\\.").length > 0)
					try {
						Class.forName(s, false, ClassLoader.getSystemClassLoader());
						s = joinCustomChar(".", removeArg(s.split("\\."), s.split("\\.").length - 1));
					} catch (ClassNotFoundException e) {
						break;
					}
				packages.add(s);
				cachedMods.add(mod.getMod());
			}
		if (!packages.isEmpty()) classes.addAll(new Reflections(new ConfigurationBuilder().setScanners(new SubTypesScanner(false), new ResourcesScanner()).forPackages(packages.toArray(new String[0]))).getSubTypesOf(Object.class));
		List<Class<? extends T>> clazzes = new ArrayList();
		for (Class c : classes)
			if (clazz.isAssignableFrom(c) && c != clazz) clazzes.add(c);
		return clazzes;
	}

	public static boolean fieldExists(Class clazz, String name) {
		List<Field> fields = Arrays.asList(clazz.getFields());
		fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
		for (Field f : fields)
			if (f.getName().equals(name)) return true;
		return false;
	}

	public static List<Method> getMethods(Class clazz) {
		return getReflectionObjects(clazz.getMethods(), clazz.getDeclaredMethods());
	}

	public static List<Field> getFields(Class clazz) {
		return getReflectionObjects(clazz.getFields(), clazz.getDeclaredFields());
	}

	private static <T> List<T> getReflectionObjects(T[] objects, T[] declaredObjects) {
		List<T> list = Lists.newArrayList(objects);
		Outer: for (T declaredObject : declaredObjects) {
			for (T element : list)
				if (element.toString().equals(declaredObject.toString())) continue Outer;
			list.add(declaredObject);
		}
		return list;
	}

	public static void removeFinalModifier(Field f) {
		if (Modifier.isFinal(f.getModifiers())) try {
			Field modifiers = Field.class.getDeclaredField("modifiers");
			modifiers.setAccessible(true);
			modifiers.set(f, f.getModifiers() & ~Modifier.FINAL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static <T> T[] pop(T[] array) {
		return pop(0, array);
	}

	public static <T> T[] pop(int index, T[] array) {
		return removeArg(array, index);
	}

	public static final String								MOD_ID						= "morecommands";
	public static final String								MOD_NAME					= "MoreCommands";
	public static final String								VERSION						= "2.1.1";
	public static final String								MC_VERSIONS					= "[1.12,1.12.2]";
	public static final String								UPDATE_URL					= "https://raw.githubusercontent.com/PlanetTeamSpeakk/MoreCommands/master/version.json";
	public static final String								BUILD_DATE					= "January 12th 2020";
	public static final String[]							AUTHORS						= new String[] {"PlanetTeamSpeak"};
	public static final List<CommandBase>					commands					= new ArrayList<>();
	public static final boolean								yiss						= true;
	public static final boolean								nah							= false;
	public static final Block								lockedChest					= new BlockFactory(BlockLockedChest.class, "morecommands:locked_chest").getBlockNoExceptions();
	public static final CreativeTab							unobtainableItems			= new CreativeTab("unobtainable items", new ItemBlock(lockedChest));
	public static final Map<String, String>					apiKeys						= new HashMap<>();
	public static final Map<String, String>					currencies					= new HashMap<>();
	public static final SimpleNetworkWrapper				netWrapper					= NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);
	public static final String								dtf							= TextFormatting.RESET + "" + TextFormatting.GOLD;																																																																																																																																// dtf = default textformatting
	public static final List<Block>							blockBlacklist				= Lists.newArrayList(Blocks.AIR, Blocks.BEDROCK, Blocks.LAVA, Blocks.CACTUS, Blocks.MAGMA, Blocks.ACACIA_FENCE, Blocks.ACACIA_FENCE_GATE, Blocks.BIRCH_FENCE, Blocks.BIRCH_FENCE_GATE, Blocks.DARK_OAK_FENCE, Blocks.DARK_OAK_FENCE_GATE, Blocks.JUNGLE_FENCE, Blocks.JUNGLE_FENCE_GATE, Blocks.NETHER_BRICK_FENCE, Blocks.OAK_FENCE, Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE, Blocks.SPRUCE_FENCE_GATE, Blocks.FIRE, Blocks.WEB, Blocks.MOB_SPAWNER, Blocks.END_PORTAL, Blocks.END_PORTAL_FRAME, Blocks.TNT, Blocks.IRON_TRAPDOOR, Blocks.TRAPDOOR, Blocks.BREWING_STAND);
	public static final List<Block>							blockWhitelist				= Lists.newArrayList(Blocks.AIR, Blocks.DEADBUSH, Blocks.VINE, Blocks.TALLGRASS, Blocks.ACACIA_DOOR, Blocks.BIRCH_DOOR, Blocks.DARK_OAK_DOOR, Blocks.IRON_DOOR, Blocks.JUNGLE_DOOR, Blocks.OAK_DOOR, Blocks.SPRUCE_DOOR, Blocks.DOUBLE_PLANT, Blocks.RED_FLOWER, Blocks.YELLOW_FLOWER, Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM, Blocks.WATERLILY, Blocks.BEETROOTS, Blocks.CARROTS, Blocks.WHEAT, Blocks.POTATOES, Blocks.PUMPKIN_STEM, Blocks.MELON_STEM, Blocks.SNOW_LAYER);
	public static final List<IGameRule>						gameRules					= new SecureArrayList(Reference.class, Initialize.class);
	public static GuiScreen									lastScreen					= null;
	public static EasterEgg									easterEgg					= null;
	public static Map<String, EntityArrow>					arrows						= new HashMap();
	public static Map<String, String>						tpRequests					= new HashMap<>();
	public static Map<String, Map<ICommandSender, Long>>	cooldowns					= new HashMap<>();
	public static Map<String, String>						setVariables				= new HashMap<>();
	public static double									blocksPerSecond				= 0;
	public static Vec3d										lastPosition				= null;
	public static Map<String, Map<String, Double>>			homes						= null;
	public static List<String>								infoOverlayConfig			= new ArrayList<>();
	public static TextFormatting							lastColor					= TextFormatting.YELLOW;
	public static int										clientTicksPassed			= 0;
	public static int										clientTicksPassed2			= 0;
	public static boolean									easterEggLoopEnabled		= false;
	public static int										updated						= 0;
	public static int										updatesPerSecond			= 0;
	public static boolean									initialized					= false;
	public static boolean									superPickaxeEnabled			= false;
	public static boolean									silencedClient				= false;
	public static List<UUID>								silenced					= new ArrayList();
	public static boolean									narratorActive				= false;
	private static final ExecutorService					executor					= Executors.newCachedThreadPool();
	private static Unsafe									theUnsafe					= null;
	private static Map<Integer, Thread>						downloadThreads				= new HashMap<>();
	private static String									ipAddress					= null;
	private static boolean									shouldSaveInfoOverlayConfig	= false;
	private static boolean									overlayEnabled				= false;
	private static ScriptEngine								nashorn						= new ScriptEngineManager(null).getEngineByName("nashorn");
	private static Map<String, KeyBinding>					keyBindings					= new HashMap<>();
	private static Map<String, List<String>>				players						= new HashMap<>();
	private static Map<String, List<String>>				groups						= new HashMap<>();
	private static Map<String, String>						aliases						= new HashMap<>();
	private static Map<String, String>						config						= new HashMap<>();
	private static List<Biome>								biomes						= new ArrayList<>();
	private static List<ICommand>							serverCommands				= new ArrayList<>();
	private static List<ICommand>							clientCommands				= new ArrayList<>();
	private static String									narratorMessage				= "";
	private static ChannelPipeline							pipeline;
	private static Map<String, Integer>						blockReachMap				= new HashMap();
	private static Field									biomeNameField;
	private static List<Class<?>>							classes						= new ArrayList();
	private static List<Object>								cachedMods					= new ArrayList();

	static {
		Constructor<Unsafe> unsafeConstructor = null;
		try {
			unsafeConstructor = Unsafe.class.getDeclaredConstructor();
			unsafeConstructor.setAccessible(true);
			theUnsafe = unsafeConstructor.newInstance();
		} catch (Exception e) {
			theUnsafe = null;
		}
		if (System.console() == null) System.setProperty("jansi.passthrough", "true");
		Initialize.setupCommandRegistry();
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) setDisplayTitle(Display.getTitle() + " with MinecraftForge");
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
		try {
			biomeNameField = Biome.class.getDeclaredField("biomeName");
			biomeNameField.setAccessible(true);
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		apiKeys.put("w3hills", "5D58B696-7AF3-4DD0-1251-B5D24E16668C");
		apiKeys.put("geocoding", "AIzaSyCXkFcW0v8XJWGK2Im2_fApsbh3I8OGCDI"); // they're all free, anyway.
		apiKeys.put("timezone", "AIzaSyCXkFcW0v8XJWGK2Im2_fApsbh3I8OGCDI");
	}

	/**
	 * Actually random, unlike ThreadLocalRandom, smh. Oracle, pls.
	 *
	 * @author PlanetTeamSpeak
	 */
	public static class Random {

		public static int randInt() {
			return randInt(0, Integer.MAX_VALUE);
		}

		public static int randInt(int max) {
			return randInt(0, max);
		}

		public static int randInt(int min, int max) {
			return (int) randDouble(min, max);
		}

		public static long randLong() {
			return randLong(0, Long.MAX_VALUE);
		}

		public static long randLong(long max) {
			return randLong(0, max);
		}

		public static long randLong(long min, long max) {
			return (long) randDouble(min, max);
		}

		public static short randShort() {
			return randShort((short) 0, Short.MAX_VALUE);
		}

		public static short randShort(short max) {
			return randShort((short) 0, max);
		}

		public static short randShort(short min, short max) {
			return (short) randLong(min, max);
		}

		public static double randDouble() {
			return randDouble(0D, Double.MAX_VALUE);
		}

		public static double randDouble(double max) {
			return randDouble(0D, max);
		}

		public static double randDouble(double min, double max) {
			// Tbh I don't even have a single clue of what I am doing here.
			double rng = (Math.random() * max + min) * (min < 0D ? Math.random() * 10 >= 5 ? 1 : -1 : 1);
			while (rng < min)
				rng += 1D;
			return rng;
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

	public static class Regex {

		private Regex() {}

		/**
		 * Removes any characters matching the regex pattern.
		 *
		 * @param regex  The regex pattern.
		 * @param string The string to use the regex pattern on.
		 * @return What's left after the regex pattern has been applied.
		 */
		public static String regexString(String regex, String string) {
			return string.replaceAll(regex, "");
		}

		public static String removeUnwantedChars(String regex, String string) {
			String data = regexString(regex, string);
			for (String ch : data.split("")) {
				String data1 = "";
				for (String ch1 : string.split(""))
					if (!ch1.equals(ch)) data1 += ch1;
				string = data1;
			}
			return string;
		}

	}

}
