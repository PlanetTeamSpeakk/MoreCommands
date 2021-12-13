package com.ptsmods.morecommands.commands.server.unelevated;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class KitCommand extends Command {
	private static KitCommand instance = null;
	private final Map<String, Kit> kits = new LinkedHashMap<>();

	@Override
	public void init(boolean serverOnly, MinecraftServer server) throws Exception {
		instance = this;
		kits.putAll(MoreObjects.firstNonNull(MoreCommands.<Map<String, Map<String, Object>>>readJson(new File(MoreCommands.getRelativePath(server) + "kits.json")), new HashMap<String, Map<String, Object>>()).entrySet().stream().map(entry -> new Pair<>(entry.getKey(), Kit.deserialise(entry.getValue()))).collect(Collectors.toMap(Pair::getLeft, Pair::getRight)));
	}

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) throws Exception {
		dispatcher.register(literalReq("kit").then(argument("kit", StringArgumentType.word()).executes(ctx -> {
			String kit = ctx.getArgument("kit", String.class).toLowerCase(Locale.ROOT);
			if (!kits.containsKey(kit)) sendError(ctx, "A kit by that name does not exist.");
			else if (kits.get(kit).onCooldown(ctx.getSource().getPlayer())) sendError(ctx, "You're still on cooldown! Please wait " + MoreCommands.formatSeconds(kits.get(kit).getRemainingCooldown(ctx.getSource().getPlayer()) / 1000, Formatting.RED, Formatting.RED) + Formatting.RED + ".");
			else if (isPermissionsLoaded() && !Permissions.check(ctx.getSource(), "morecommands.kit." + kit, true)) sendError(ctx, "You do not have permission to use that kit.");
			else {
				kits.get(kit).give(ctx.getSource().getPlayer());
				sendMsg(ctx, "You have been given the " + SF + kits.get(kit).getName() + DF + " kit.");
				return 1;
			}
			return 0;
		})));
		dispatcher.register(literalReq("createkit").requires(hasPermissionOrOp("morecommands.createkit")).then(argument("name", StringArgumentType.word()).then(argument("cooldown", IntegerArgumentType.integer(0)).executes(ctx -> {
			String name = ctx.getArgument("name", String.class);
			if (kits.containsKey(name.toLowerCase(Locale.ROOT))) sendError(ctx, "A kit with that name already exists.");
			else {
				PlayerInventory inv = Compat.getCompat().getInventory(ctx.getSource().getPlayer());
				kits.put(name.toLowerCase(Locale.ROOT), new Kit(name, ctx.getArgument("cooldown", Integer.class), Lists.newArrayList(inv.main, inv.armor, inv.offHand).stream().flatMap(List::stream).collect(Collectors.toList())));
				saveKits();
				sendMsg(ctx, "Kit " + SF + name + DF + " has been made.");
				return 1;
			}
			return 0;
		}))));
		dispatcher.register(literalReq("delkit").requires(hasPermissionOrOp("morecommands.delkit")).then(argument("kit", StringArgumentType.word()).executes(ctx -> {
			String kit = ctx.getArgument("kit", String.class);
			if (!kits.containsKey(kit)) sendError(ctx, "No kit by that name exists.");
			else {
				Kit kit0 = kits.remove(kit);
				saveKits();
				sendMsg(ctx, "Kit " + SF + kit0.getName() + DF + " has been removed.");
				return 1;
			}
			return 0;
		})));
	}

	@Override
	public boolean forDedicated() {
		return true;
	}

	private static Map<String, Object> serialiseItemStack(ItemStack stack) {
		Map<String, Object> data = new HashMap<>();
		data.put("item", Registry.ITEM.getId(stack.getItem()).toString());
		data.put("count", stack.getCount());
		data.put("tag", stack.hasNbt() ? nbtToByteString(stack.getNbt()) : null);
		return data;
	}

	private static ItemStack deserialiseItemStack(Map<String, Object> data) {
		ItemStack stack = new ItemStack(Registry.ITEM.get(new Identifier((String) data.get("item"))), ((Double) data.get("count")).intValue());
		stack.setNbt(nbtFromByteString((String) data.get("tag")));
		return stack;
	}

	private static String nbtToByteString(NbtCompound tag) {
		ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
		try {
			NbtIo.writeCompressed(tag, bytestream);
		} catch (IOException e) {
			// Shouldn't be possible as this is a memory output stream, it's not outputting to a file or whatever.
			log.error("An unknown error occurred while writing the NBT data to bytes.", e);
		}
		return Hex.encodeHexString(bytestream.toByteArray());
	}

	private static NbtCompound nbtFromByteString(String byteString) {
		if (byteString == null) return null;
		byte[] bytes;
		try {
			bytes = Hex.decodeHex(byteString.toCharArray());
		} catch (DecoderException e) {
			log.error("Could not decode byte string " + byteString, e);
			return null;
		}
		try {
			return NbtIo.readCompressed(new ByteArrayInputStream(bytes));
		} catch (IOException e) {
			log.error("Error reading decoded bytes.", e);
			return null;
		}
	}

	private static void saveKits() {
		try {
			MoreCommands.saveJson(new File(MoreCommands.getRelativePath() + "kits.json"), instance.kits.entrySet().stream().map(entry -> new Pair<>(entry.getKey(), entry.getValue().serialise())).collect(Collectors.toMap(Pair::getLeft, Pair::getRight)));
		} catch (IOException e) {
			log.error("Couldn't save kits file.", e);
		}
	}

	private static class Kit {
		private final String name;
		private final int cooldown;
		private final Map<UUID, Long> cooldowns = new HashMap<>();
		private final List<ItemStack> items = new ArrayList<>();

		public Kit(String name, int cooldown, List<ItemStack> items) {
			this.name = name;
			this.cooldown = cooldown;
			this.items.addAll(items);
		}

		public String getName() {
			return name;
		}

		public int getCooldown() {
			return cooldown;
		}

		public List<ItemStack> getItems() {
			return ImmutableList.copyOf(items);
		}

		public void give(PlayerEntity player) {
			if (!onCooldown(player)) {
				for (ItemStack stack : items) {
					ItemStack stack0 = stack.copy();
					if (!Compat.getCompat().getInventory(player).insertStack(stack0)) player.dropItem(stack0, false);
				}
				if (cooldown > 0) {
					cooldowns.put(player.getUuid(), System.currentTimeMillis() + cooldown * 1000L);
					saveKits();
				}
			}
		}

		public boolean onCooldown(PlayerEntity player) {
			return getRemainingCooldown(player) > 0;
		}

		public long getRemainingCooldown(PlayerEntity player) {
			return cooldowns.containsKey(player.getUuid()) ? cooldowns.get(player.getUuid()) - System.currentTimeMillis() : 0;
		}

		public Map<String, Object> serialise() {
			Map<String, Object> data = new LinkedHashMap<>();
			data.put("name", name);
			data.put("cooldown", cooldown);
			data.put("cooldowns", cooldowns.entrySet().stream().map(entry -> new Pair<>(entry.getKey().toString(), entry.getValue())).filter(pair -> pair.getRight() - System.currentTimeMillis() > 0).collect(Collectors.toMap(Pair::getLeft, Pair::getRight)));
			data.put("items", items.stream().filter(stack -> !stack.isEmpty()).map(KitCommand::serialiseItemStack).collect(Collectors.toList()));
			return data;
		}

		public static Kit deserialise(Map<String, Object> data) {
			String name = (String) data.get("name");
			int cooldown = ((Double) data.get("cooldown")).intValue();
			Map<UUID, Long> cooldowns = ((Map<String, Long>) data.get("cooldowns")).entrySet().stream().map(entry -> new Pair<>(UUID.fromString(entry.getKey()), entry.getValue())).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
			List<ItemStack> items = ((List<Map<String, Object>>) data.get("items")).stream().map(KitCommand::deserialiseItemStack).collect(Collectors.toList());
			Kit kit = new Kit(name, cooldown, items);
			kit.cooldowns.putAll(cooldowns);
			return kit;
		}
	}
}
