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
import com.ptsmods.morecommands.miscellaneous.ClientEventHandler;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.EEGenerator;
import com.ptsmods.morecommands.miscellaneous.FP;
import com.ptsmods.morecommands.miscellaneous.FPStorage;
import com.ptsmods.morecommands.miscellaneous.IGameRule;
import com.ptsmods.morecommands.miscellaneous.IReach;
import com.ptsmods.morecommands.miscellaneous.Reach;
import com.ptsmods.morecommands.miscellaneous.ReachStorage;
import com.ptsmods.morecommands.miscellaneous.Reference;
import com.ptsmods.morecommands.miscellaneous.Reference.LogType;
import com.ptsmods.morecommands.miscellaneous.Reference.Random;
import com.ptsmods.morecommands.miscellaneous.ServerEventHandler;
import com.ptsmods.morecommands.miscellaneous.Ticker;
import com.ptsmods.morecommands.miscellaneous.Ticker.TickRunnable;
import com.ptsmods.morecommands.net.ClientCurrentItemUpdatePacket;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Type;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/*
 * Ngl I absolutely despise the way commands are handled in 1.13 and I will
 * refrain from modding in it for as long as I feel like I need.
 */
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, acceptedMinecraftVersions = Reference.MC_VERSIONS, updateJSON = Reference.UPDATE_URL)
public class MoreCommands {

	@Instance(Reference.MOD_ID)
	public static MoreCommands	INSTANCE		= null;
	public static List<String>	loadedClasses	= new ArrayList();

	public MoreCommands() {
		try {
			ClassLoader.getSystemClassLoader().loadClass(Reference.class.getName()); // Reference used to have an initialise method which was called here, but that
																					 // has been moved to a static constructor which is called using this method.
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private int lastItem = -1;

	@EventHandler
	public void onConstruct(FMLConstructionEvent event) {
		setLogger();
		registerOnLoadController();
	}

	@EventHandler
	public void serverStart(FMLServerStartingEvent event) {
		Reference.setupGameRules(event.getServer());
		Reference.resetCommandRegistry(CommandType.SERVER); // Making sure the commands don't get registered multiple times.
		Initialize.setupCommandRegistry();
		Initialize.registerCommands(event.getServer());
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
