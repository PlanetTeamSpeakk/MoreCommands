package com.ptsmods.morecommands.miscellaneous;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.util.ReflectionHelper;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;

import static com.ptsmods.morecommands.MoreCommands.*;

public abstract class Command {
	public static Formatting DF = MoreCommands.DF;
	public static Formatting SF = MoreCommands.SF;
	public static Style DS = MoreCommands.DS;
	public static Style SS = MoreCommands.SS;
	public static final Logger log = MoreCommands.log;
	public static final Predicate<ServerCommandSource> IS_OP = source -> source.hasPermissionLevel(source.getServer().getOpPermissionLevel());
	private static final Map<Class<?>, Command> activeInstances = new HashMap<>();
	private static final Map<Class<?>, Map<Event<?>, Object>> registeredCallbacks = new HashMap<>();

	/**
	 * Gets called once when the command is initialised.
	 * Mostly useless now as the no-arg constructor achieves the same thing.
	 * @throws Exception Can be anything
	 */
	public void preinit() throws Exception {}

	/**
	 * Gets called every time a new server is created.
	 * So also whenever the player joins a singleplayer world.
	 * @param server The server that was created
	 * @throws Exception Can be anything
	 */
	public void init(MinecraftServer server) throws Exception {}

	public abstract void register(CommandDispatcher<ServerCommandSource> dispatcher) throws Exception;

	public void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) throws Exception {
		register(dispatcher);
	}

	public boolean forDedicated() {
		return false;
	}

	public static int sendMsg(CommandContext<ServerCommandSource> ctx, String msg) {
		return sendMsg(ctx, new LiteralText(fixResets(msg)).setStyle(DS));
	}

	public static int sendMsg(CommandContext<ServerCommandSource> ctx, Text msg) {
		ctx.getSource().sendFeedback(msg.shallowCopy().formatted(DF), true);
		return 1;
	}

	public static int sendError(CommandContext<ServerCommandSource> ctx, String msg) {
		return sendError(ctx, new LiteralText(fixResets(msg, Formatting.RED)));
	}

	public static int sendError(CommandContext<ServerCommandSource> ctx, Text msg) {
		ctx.getSource().sendError(msg);
		return 1;
	}

	static String fixResets(String s) {
		return fixResets(s, DF);
	}

	static String fixResets(String s, Formatting formatting) {
		return s.replace(Formatting.RESET.toString(), Formatting.RESET.toString() + formatting).replaceAll("\n", "\n" + formatting);
	}

	public static int sendMsg(Entity entity, String msg) {
		return sendMsg(entity, new LiteralText(msg).setStyle(DS));
	}

	public static int sendMsg(Entity entity, Text msg) {
		msg = msg.shallowCopy().setStyle(msg.getStyle() == null ? DS : msg.getStyle());
		if (entity instanceof PlayerEntity) ((PlayerEntity) entity).sendMessage(msg, false);
		else entity.sendSystemMessage(msg, entity.getUuid());
		return 1;
	}

	public static void broadcast(MinecraftServer server, String msg) {
		broadcast(server, new LiteralText(msg).setStyle(DS));
	}

	public static void broadcast(MinecraftServer server, Text msg) {
		for (PlayerEntity player : server.getPlayerManager().getPlayerList())
			sendMsg(player, msg);
	}

	public static LiteralArgumentBuilder<ServerCommandSource> literal(String literal) {
		return CommandManager.literal(literal);
	}

	public static LiteralArgumentBuilder<ServerCommandSource> literalReqOp(String literal) {
		return literal(literal).requires(hasPermissionOrOp("morecommands." + literal));
	}

	public static LiteralArgumentBuilder<ServerCommandSource> literalReq(String literal) {
		return literal(literal).requires(hasPermission("morecommands." + literal));
	}

	public static <T> RequiredArgumentBuilder<ServerCommandSource, T> argument(String name, ArgumentType<T> type) {
		return CommandManager.argument(name, type);
	}

	public static String translateFormats(String s) {
		for (Formatting f : Formatting.values())
			s = s.replaceAll("&" + getChar(f), f.toString());
		return s.replaceAll("&#", "\u00A7#");
	}

	public static String joinNicely(Collection<String> strings) {
		return joinNicely(strings, SF, DF);
	}

	public static String joinNicely(Collection<String> strings, Formatting colour, Formatting commaColour) {
		List<String> l = new ArrayList<>(strings);
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < l.size(); i++)
			s.append(colour == null ? "" : colour).append(l.get(i)).append(commaColour == null ? "" : commaColour).append(i == l.size()-2 ? " and" : i == l.size()-1 ? "" : ",").append(i == l.size()-1 ? "" : " ");
		return s.toString();
	}

	public static Formatting formatFromBool(boolean b) {
		return b ? Formatting.GREEN : Formatting.RED;
	}

	public static String formatFromBool(boolean b, String yes, String no) {
		return formatFromBool(b) + (b ? yes : no);
	}

	public static String formatFromFloat(float v, float max, float yellow, float green, boolean includeMax) {
		float percent = v/max;
		return "" + (percent >= green ? Formatting.GREEN : percent >= yellow ? Formatting.YELLOW : Formatting.RED) + (includeMax ? DF + "/" + Formatting.GREEN + max : "");
	}

	public static boolean isOp(CommandContext<ServerCommandSource> ctx) {
		return IS_OP.test(ctx.getSource());
	}

	public static boolean isOp(ServerPlayerEntity player) {
		return player.hasPermissionLevel(Objects.requireNonNull(player.getServer()).getOpPermissionLevel());
	}

	public void setActiveInstance() {
		activeInstances.put(getClass(), this);
	}

	public static UUID getServerUuid(MinecraftServer server) {
		return UUID.nameUUIDFromBytes(server.getCommandSource().getName().getBytes(StandardCharsets.UTF_8));
	}

	public static void doInitialisations(MinecraftServer server) {
		for (Command cmd : activeInstances.values())
			try {
				cmd.init(server);
			} catch (Exception e) {
				log.error("Error invoking initialisation method on class " + cmd.getClass().getName() + ".", e);
			}
	}

	// I made the dumb mistake to register callbacks in the init method of commands even though
	// every command class gets initialised again whenever a server loads so also whenever you
	// join a new world.
	// Which means that exiting and joining a world would cause the same callback to be
	// registered twice.
	// So to avoid that, I initially used the Proxy class, although that wasn't really working out for me,
	// so I ended up just removing the previously registered callback instead.
	protected <T> void registerCallback(Event<T> event, T callback) {
		Class<?> c = null;
		try {
			c = Class.forName("net.fabricmc.fabric.impl.base.event.ArrayBackedEvent");
		} catch (ClassNotFoundException e) {
			log.error("Could not find ArrayBackedEvent class.", e);
		}
		if (event.getClass() == c && registeredCallbacks.containsKey(getClass()) && registeredCallbacks.get(getClass()).getOrDefault(event, null) != null) {
			Field handlersField = ReflectionHelper.getField(c, "handlers");
			T[] handlers = ReflectionHelper.getFieldValue(handlersField, event);
			if (handlers != null) {
				ReflectionHelper.setFieldValue(handlersField, event, ArrayUtils.removeElement(handlers, registeredCallbacks.get(getClass()).get(event)));
				ReflectionHelper.invokeMethod(c, "update", null, event);
			}
		}
		if (callback != null) event.register(callback); // So you can unregister callbacks by passing null as a callback to this method.
		if (!registeredCallbacks.containsKey(getClass())) registeredCallbacks.put(getClass(), new HashMap<>());
		registeredCallbacks.get(getClass()).put(event, callback);
	}

	protected static Predicate<ServerCommandSource> hasPermission(@NotNull String permission, int defaultRequiredLevel) {
		return FabricLoader.getInstance().isModLoaded("fabric-permissions-api-v0") ? Permissions.require(permission, defaultRequiredLevel) : source -> source.hasPermissionLevel(defaultRequiredLevel);
	}

	protected static Predicate<ServerCommandSource> hasPermissionOrOp(@NotNull String permission) {
		return hasPermission(permission, 2);
	}

	protected static Predicate<ServerCommandSource> hasPermission(@NotNull String permission) {
		return hasPermission(permission, 0);
	}
}
