package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collector;

public class HomeCommand extends Command {

	private static File dataFile = null;
	private final Map<UUID, List<Home>> homes = new HashMap<>();

	public void init(MinecraftServer server) {
		if (new File("config/MoreCommands/homes.json").exists()) MoreCommands.tryMove("config/MoreCommands/homes.json", MoreCommands.getRelativePath() + "homes.json");
		dataFile = new File(MoreCommands.getRelativePath() + "homes.json");
		Map<String, Map<String, Map<String, Object>>> data = null;
		try {
			data = MoreCommands.readJson(dataFile);
		} catch (IOException e) {
			log.catching(e);
		} catch (NullPointerException ignored) {}
		homes.clear();
		if (data != null)
			data.forEach((key, value) -> {
				List<Home> homes = new ArrayList<>();
				value.entrySet().forEach(entry0 -> homes.add(Home.fromMap(entry0)));
				this.homes.put(UUID.fromString(key), homes);
			});
	}

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("home").executes(ctx -> executeHome(ctx, null)).then(argument("home", StringArgumentType.word()).executes(ctx -> executeHome(ctx, ctx.getArgument("home", String.class)))));
		dispatcher.register(literal("homes").executes(ctx -> sendHomes(ctx.getSource().getPlayer())));
		dispatcher.register(literal("sethome").executes(ctx -> executeSetHome(ctx, "home")).then(argument("name", StringArgumentType.word()).executes(ctx -> executeSetHome(ctx, ctx.getArgument("name", String.class)))));
		dispatcher.register(literal("delhome").then(argument("home", StringArgumentType.word()).executes(ctx -> {
			PlayerEntity p = ctx.getSource().getPlayer();
			Home home = getHome(p, ctx.getArgument("home", String.class));
			if (!homes.containsKey(p.getUuid())) sendHomes(p); // Will send error msg.
			else if (home == null) sendMsg(ctx, Formatting.RED + "Could not find a home by that name.");
			else {
				getHomes(p).remove(home);
				if (getHomes(p).isEmpty()) homes.remove(p.getUuid());
				saveData();
				sendMsg(ctx, "Your home " + SF + home.name + DF + " was removed.");
				return 1;
			}
			return 0;
		})));
	}

	private int executeSetHome(CommandContext<ServerCommandSource> ctx, String name) throws CommandSyntaxException {
		PlayerEntity p = ctx.getSource().getPlayer();
		int max = p.getEntityWorld().getGameRules().getInt(MoreCommands.maxHomesRule);
		if (max < 0) max = Integer.MAX_VALUE;
		boolean bypass = isOp(ctx);
		if (max == 0 && !bypass) sendMsg(ctx, Formatting.RED + "Homes are currently disabled, you can enable them by setting the maxHomes gamerule to a value greater than 0.");
		else if (getHomes(p).size() >= max && !bypass) sendMsg(ctx, Formatting.RED + "You cannot set more than " + max + " homes.");
		else {
			if (!homes.containsKey(p.getUuid())) homes.put(p.getUuid(), new ArrayList<>());
			getHomes(p).add(new Home(name, p.getPos().x, p.getPos().y, p.getPos().z, p.pitch, p.yaw, p.getEntityWorld().getRegistryKey().getValue()));
			saveData();
			sendMsg(ctx, "A home by the name of " + SF + name + DF + " has been set.");
		}
		return homes.get(p.getUuid()).size();
	}

	private int executeHome(CommandContext<ServerCommandSource> ctx, String name) throws CommandSyntaxException {
		PlayerEntity player = ctx.getSource().getPlayer();
		if (getHomes(player).isEmpty() || name == null && getHomes(player).size() > 1) sendHomes(player);
		else {
			// Get home named 'home' if no home was given or, if that does not exist, get the first set home.
			Home home = getHome(player, name == null ? getHome(player, "home") == null ? getHomes(player).get(0).name : "home" : name);
			if (home == null) sendHomes(player);
			else return tpHome(player, home);
		}
		return 0;
	}

	private int sendHomes(PlayerEntity player) {
		sendMsg(player, getHomes(player).isEmpty() ? Formatting.RED + "You do not have any homes set yet, set one with /sethome [name]." : "You have set the following homes: " + joinNicely(getHomes(player).stream().collect(Collector.of(ArrayList::new, (l, home) -> l.add(home.name), BinaryOperator.maxBy(Comparator.comparingInt(List::size))))) + ".");
		return getHomes(player).size();
	}

	private int tpHome(PlayerEntity player, Home home) {
		MoreCommands.teleport(player, player.getServer().getWorld(RegistryKey.of(Registry.DIMENSION, home.dimension)), home.x, home.y, home.z, home.yaw, home.pitch);
		RegistryKey<World> registryKey = player.getEntityWorld().getRegistryKey();
		if (World.NETHER.equals(registryKey)) return 9;
		else if (World.OVERWORLD.equals(registryKey)) return 10;
		else if (World.END.equals(registryKey)) return 11;
		return 12;
	}

	private Home getHome(PlayerEntity player, String name) {
		for (Home home : getHomes(player))
			if (home.name.equalsIgnoreCase(name))
				return home;
		return null;
	}

	private List<Home> getHomes(PlayerEntity player) {
		return homes.getOrDefault(player.getUuid(), Collections.emptyList());
	}

	private void saveData() {
		Map<String, Map<String, Map<String, Object>>> data = new HashMap<>();
		for (Map.Entry<UUID, List<Home>> entry : homes.entrySet()) {
			Map<String, Map<String, Object>> homes = new HashMap<>();
			for (Home home : entry.getValue())
				homes.put(home.name, home.toMap());
			data.put(entry.getKey().toString(), homes);
		}
		try {
			MoreCommands.saveJson(dataFile, data);
		} catch (IOException e) {
			log.catching(e);
		}
	}

	private static class Home {

		private final String name;
		private final double x, y, z;
		private final float pitch, yaw;
		private final Identifier dimension;

		private Home(String name, double x, double y, double z, float pitch, float yaw, Identifier dimension) {
			this.name = name;
			this.x = x;
			this.y = y;
			this.z = z;
			this.pitch = pitch;
			this.yaw = yaw;
			this.dimension = dimension;
		}

		private Map<String, Object> toMap() {
			Map<String, Object> data = new HashMap<>();
			data.put("x", x);
			data.put("y", y);
			data.put("z", z);
			data.put("pitch", pitch);
			data.put("yaw", yaw);
			data.put("dimension", dimension.toString());
			return data;
		}

		private static Home fromMap(Map.Entry<String, Map<String, Object>> data) {
			Map<String, Object> v = data.getValue();
			return new Home(data.getKey(), (Double) v.get("x"), (Double) v.get("y"), (Double) v.get("z"), ((Double) v.get("pitch")).floatValue(), ((Double) v.get("yaw")).floatValue(), new Identifier((String) v.get("dimension")));
		}

	}

}
