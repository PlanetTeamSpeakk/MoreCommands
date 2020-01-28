package com.ptsmods.morecommands;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.ptsmods.morecommands.commands.vanish.Commandvanish;
import com.ptsmods.morecommands.miscellaneous.*;
import com.ptsmods.morecommands.miscellaneous.Reference.LogType;
import com.ptsmods.morecommands.miscellaneous.Reference.Random;
import com.ptsmods.morecommands.miscellaneous.Ticker.TickRunnable;
import com.ptsmods.morecommands.net.ClientCurrentItemUpdatePacket;
import com.ptsmods.morecommands.net.ServerRecipePacket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Type;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/*
 * Ngl I absolutely despise the way commands are handled in 1.13 and I will
 * refrain from modding in it for as long as I feel like I need.
 */
@SuppressWarnings("deprecation")
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, acceptedMinecraftVersions = Reference.MC_VERSIONS, updateJSON = Reference.UPDATE_URL)
public class MoreCommands {

	@Instance(Reference.MOD_ID)
	public static MoreCommands	INSTANCE				= null;
	public static boolean		modInstalledServerSide	= false;

	public MoreCommands() {
		Reference.init();
	}

	private int lastItem = -1;

	@NetworkCheckHandler
	public boolean onConnect(Map map, Side side) {
		if (side == Side.SERVER) {
			modInstalledServerSide = map.containsKey("morecommands");
			Reference.print(LogType.INFO, "Connected to a server with" + (!modInstalledServerSide ? "out" : "") + " MoreCommands installed.");
		}
		return true;
	}

	@EventHandler
	public void onConstruct(FMLConstructionEvent event) {
		setLogger();
		registerOnLoadController();
	}

	@EventHandler
	public void serverStart(FMLServerStartingEvent event) {
		Initialize.setupGameRules(event.getServer());
		for (ICommand command : event.getServer().getCommandManager().getCommands().values()) {
			String modName = "minecraft";
			String[] pkg = Reference.removeArg(command.getClass().getName().split("\\."), command.getClass().getName().split("\\.").length - 1);
			Outer: for (ModContainer mod : Loader.instance().getActiveModList()) {
				if (mod.getMod() == null) continue;
				String[] pkg0 = Reference.removeArg(mod.getMod().getClass().getName().split("\\."), mod.getMod().getClass().getName().split("\\.").length - 1);
				for (int i = 0; i < 3; i++)
					if (i < pkg0.length && i < pkg.length && pkg0[i].equals(pkg[i])) {
						// First three parts have to be the same.
						modName = mod.getModId();
						continue Outer;
					}
			}
			new Permission(modName.equals("FML") ? "minecraft" : modName, command.getName(), modName.equals("FML") ? "A vanilla Minecraft command." : I18n.translateToLocal(command.getUsage(event.getServer())), true, command);
		}
		Reference.resetCommandRegistry(CommandType.SERVER); // Making sure the commands don't get registered multiple times.
		Initialize.setupCommandRegistry();
		Initialize.registerCommands(event.getServer());
		Permission.setCommands();
		Reference.registerEventHandler(new ServerEventHandler());
		Reference.loadPackets();
		Map<World, Map<IGameRule, Object>> ruleValues = new HashMap();
		for (WorldServer world : event.getServer().worlds) {
			ruleValues.put(world, new HashMap());
			for (IGameRule rule : Reference.gameRules)
				ruleValues.get(world).put(rule, rule.getValue(world));
		}
		TickRunnable gameRuleRunnable = extraArgs -> {
			World world = (World) extraArgs[1];
			for (Entry<IGameRule, Object> entry : ruleValues.getOrDefault(world, new HashMap<>()).entrySet()) {
				IGameRule rule = entry.getKey();
				Object value = rule.getValue(world);
				if (!entry.getValue().equals(value)) {
					rule.onUpdate((WorldServer) world, ruleValues.get(world).get(rule), value);
					ruleValues.get(world).put(rule, value);
				}
			}
		};
		Ticker.INSTANCE.addRunnable(TickEvent.Type.WORLD, gameRuleRunnable.setRemoveWhenRan(false));
		if (!event.getServer().isSinglePlayer()) {
			CapabilityManager.INSTANCE.register(IReach.class, new ReachStorage(), () -> new Reach());
			CapabilityManager.INSTANCE.register(FP.class, new FPStorage(), () -> new FP());
		}
		for (int i : DimensionManager.getStaticDimensionIDs())
			Commandvanish.modEntityTracker(event.getServer().getWorld(i));
	}

	@EventHandler
	@SideOnly(Side.CLIENT)
	public void postInit(FMLPostInitializationEvent event) {
		Initialize.registerClientCommands();
		try {
			Field f = Reference.getFieldMapped(EntityRenderer.class, "resourceManager", "field_110451_am", "field_110582_d", "field_147695_g", "field_147711_ac", "field_148033_b", "field_177598_f");
			f.setAccessible(true);
			Minecraft.getMinecraft().entityRenderer = new ReachingEntityRenderer(Minecraft.getMinecraft(), (IResourceManager) f.get(Minecraft.getMinecraft().entityRenderer));
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@EventHandler()
	@SideOnly(Side.CLIENT)
	public void preInit(FMLPreInitializationEvent event) {
		Reference.createFileIfNotExisting("config/MoreCommands/infoOverlay.txt");
		Reference.createFileIfNotExisting("config/MoreCommands/aliases.yaml");
		try {
			Reference.loadInfoOverlayConfig();
			Reference.loadAliases();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Reference.setupKeyBindingRegistry();
		Reference.registerEventHandler(new ClientEventHandler());
		Reference.loadPackets();
		GameRegistry.registerWorldGenerator(new EEGenerator(), 0);
		CapabilityManager.INSTANCE.register(IReach.class, new ReachStorage(), () -> new Reach());
		CapabilityManager.INSTANCE.register(FP.class, new FPStorage(), () -> new FP());
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new IGuiHandler() {

			@Override
			public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
				switch (ID) {
				case 0:
					return new AddRecipeBenchContainer(player.inventory);
				case 1:
					return new AddRecipeFurnaceContainer(player.inventory);
				case 2:
					return new AddRecipeBrewingContainer(player.inventory);
				default:
					return null;
				}
			}

			@Override
			public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
				switch (ID) {
				case 0:
				case 1:
				case 2:
					return new AddRecipeGUI(ServerRecipePacket.Type.values()[ID], player.inventory);
				default:
					return null;
				}
			}
		});
		Field f = Reference.getFieldMapped(ClientCommandHandler.class, "instance");
		Reference.removeFinalModifier(f);
		try {
			f.set(null, new ClientCommandHandler() {
				@Override
				public int executeCommand(ICommandSender sender, String message) {
					// For some reason, plain messages can also be seen as commands.
					// E.g. when you say chelp rather than /chelp, this method gets called either
					// way.
					return message != null && message.startsWith("/") ? super.executeCommand(sender, message) : 0;
				}
			});
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		AtomicInteger rainbowCounter = new AtomicInteger();
		TickRunnable runnable = extraArgs -> {
			if (ClientEventHandler.rainbowHighlight && rainbowCounter.getAndIncrement() % 2 == 0) {
				rainbowCounter.set(0);
				ClientEventHandler.redAmountHighlight = Random.randInt(255);
				ClientEventHandler.greenAmountHighlight = Random.randInt(255);
				ClientEventHandler.blueAmountHighlight = Random.randInt(255);
			}
		};
		Ticker.INSTANCE.addRunnable(Type.CLIENT, runnable.setRemoveWhenRan(false));
		TickRunnable runnable0 = extraArgs -> {
			if (Minecraft.getMinecraft().player != null) {
				if (Minecraft.getMinecraft().player.inventory.currentItem != lastItem) {
					Reference.netWrapper.sendToServer(new ClientCurrentItemUpdatePacket(Minecraft.getMinecraft().player.inventory.currentItem));
					lastItem = Minecraft.getMinecraft().player.inventory.currentItem;
				}
			} else lastItem = -1;
		};
		Ticker.INSTANCE.addRunnable(Type.CLIENT, runnable0.setRemoveWhenRan(false));
		List<EntityPlayer> howled = new ArrayList();
		TickRunnable runnable1 = extraArgs -> {
			MinecraftServer server = (MinecraftServer) extraArgs[0];
			double angle = server.getWorld(0).getCelestialAngle(0) * 360;
			if (angle > 90 && angle < 270 && server.getWorld(0).provider.getMoonPhase(server.getWorld(0).getWorldTime()) == 0) for (EntityPlayerMP player : server.getPlayerList().getPlayers())
				if (Reference.isLookingAtMoon(player, angle) && player.isSneaking() && player.getEntityWorld().provider.getDimension() == 0) {
					if (!howled.contains(player)) {
						player.getEntityWorld().playSound(null, player.getPosition().getX() + 0.5D, player.getPosition().getY() + player.getEyeHeight(), player.getPosition().getZ() + 0.5D, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.wolf.howl")), SoundCategory.PLAYERS, 4, 1);
						howled.add(player);
					}
				} else howled.remove(player);
		};
		Ticker.INSTANCE.addRunnable(Type.SERVER, runnable1.setRemoveWhenRan(false));
	}

	@Subscribe
	public void onStateChange(FMLStateEvent event) {}

	private static final boolean setLogger() {
		try {
			Field f = FMLLog.class.getDeclaredField("log");
			Field modifiers = Field.class.getDeclaredField("modifiers");
			modifiers.setAccessible(true);
			modifiers.set(f, f.getModifiers() & ~Modifier.FINAL);
			Logger orig = (Logger) FMLLog.log;
			f.set(null, new Logger(orig.getContext(), orig.getName(), orig.getMessageFactory()) {
				@Override
				public void warn(String message, Object p0, Object p1, Object p2) {
					if (message != null && !message.startsWith("Potentially Dangerous alternative prefix")) super.warn(message, p0, p1, p2);
				}

				@Override
				public void logMessage(String fqcn, Level level, Marker marker, Message message, Throwable t) {
					if (level == Level.ERROR && (message.getFormattedMessage().startsWith("Exception loading model for variant") || message.getFormattedMessage().startsWith("Exception loading blockstate for the variant"))) return;
					else if (level == Level.FATAL && message.getFormattedMessage().startsWith("Suppressed additional ")) return;
					else super.logMessage(fqcn, level, marker, message, t);
					// A proper mod should not print this error, but since this one loads vanilla
					// blocks that have no models, I'll have to suppress them anyway.
				}
			});
			Reference.print(LogType.INFO, "Successfully hacked the FML log to our log.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return FMLLog.log.getClass().getName().startsWith(MoreCommands.class.getName());
	}

	private final void registerOnLoadController() {
		try {
			Field f = LoadController.class.getDeclaredField("masterChannel");
			f.setAccessible(true);
			Field f0 = Loader.class.getDeclaredField("modController");
			f0.setAccessible(true);
			((EventBus) f.get(f0.get(Loader.instance()))).register(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
