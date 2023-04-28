package com.ptsmods.morecommands.commands.unelevated;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.datafixers.util.Pair;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.MoreCommandsArch;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.api.util.extensions.CollectionExtensions;
import com.ptsmods.morecommands.api.util.extensions.ObjectExtensions;
import com.ptsmods.morecommands.miscellaneous.Command;
import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@ExtensionMethod(ObjectExtensions.class)
public class KitCommand extends Command {
	private static KitCommand instance = null;
	private final Map<String, Kit> kits = new LinkedHashMap<>();

	@Override
	public void init(boolean serverOnly, MinecraftServer server) throws Exception {
		instance = this;
		kits.putAll(MoreCommands.<Map<String, Map<String, Object>>>readJson(MoreCommands.getRelativePath(server).resolve("kits.json").toFile())
				.or(new HashMap<String, Map<String, Object>>()).entrySet().stream()
				.map(entry -> Pair.of(entry.getKey(), Kit.deserialise(entry.getValue())))
				.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
	}

	@Override
	public void register(CommandDispatcher<CommandSourceStack> dispatcher) throws Exception {
		dispatcher.register(literalReq("kit").then(argument("kit", StringArgumentType.word()).executes(ctx -> {
			String kit = ctx.getArgument("kit", String.class).toLowerCase(Locale.ROOT);
			if (!kits.containsKey(kit))
				return sendError(ctx, "A kit by that name does not exist.");
			if (kits.get(kit).onCooldown(ctx.getSource().getPlayerOrException()))
				return sendError(ctx, "You're still on cooldown! Please wait " + MoreCommands.formatSeconds(kits.get(kit)
						.getRemainingCooldown(ctx.getSource().getPlayerOrException()) / 1000, ChatFormatting.RED, ChatFormatting.RED) + ChatFormatting.RED + ".");
			if (isPermissionsLoaded() && !MoreCommandsArch.checkPermission(ctx.getSource(), "morecommands.kit." + kit, true))
				return sendError(ctx, "You do not have permission to use that kit.");

			kits.get(kit).give(ctx.getSource().getPlayer());
			sendMsg(ctx, "You have been given the " + SF + kits.get(kit).getName() + DF + " kit.");
			return 1;
		})));
		dispatcher.register(literalReq("createkit").requires(hasPermissionOrOp("morecommands.createkit"))
				.then(argument("name", StringArgumentType.word())
						.then(argument("cooldown", IntegerArgumentType.integer(0))
								.executes(ctx -> {
									String name = ctx.getArgument("name", String.class);
									if (kits.containsKey(name.toLowerCase(Locale.ROOT))) sendError(ctx, "A kit with that name already exists.");
									else {
										Inventory inv = ctx.getSource().getPlayerOrException().getInventory();
										kits.put(name.toLowerCase(Locale.ROOT), new Kit(name, ctx.getArgument("cooldown", Integer.class),
												Lists.newArrayList(inv.items, inv.armor, inv.offhand).stream()
														.flatMap(List::stream)
														.collect(Collectors.toList())));
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
	public boolean isDedicatedOnly() {
		return true;
	}

	@Override
	public Map<String, Boolean> getExtraPermissions() {
		return kits.keySet().stream()
				.map(kit -> "morecommands.kit." + kit)
				.collect(Collectors.toMap(s -> s, s -> true));
	}

	@Override
	public String getDocsPath() {
		return "/unelevated/kit";
	}

	private static Map<String, Object> serialiseItemStack(ItemStack stack) {
		Map<String, Object> data = new HashMap<>();
		data.put("item", Objects.requireNonNull(Compat.get().<Item>getBuiltInRegistry("item").getKey(stack.getItem())).toString());
		data.put("count", stack.getCount());
		data.put("tag", stack.hasTag() ? nbtToByteString(stack.getTag()) : null);
		return data;
	}

	private static ItemStack deserialiseItemStack(Map<String, Object> data) {
		ItemStack stack = new ItemStack(Compat.get().<Item>getBuiltInRegistry("item").get(new ResourceLocation((String) data.get("item"))), ((Double) data.get("count")).intValue());
		stack.setTag(nbtFromByteString((String) data.get("tag")));
		return stack;
	}

	private static String nbtToByteString(CompoundTag tag) {
		ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
		try {
			NbtIo.writeCompressed(tag, bytestream);
		} catch (IOException e) {
			// Shouldn't be possible as this is a memory output stream, it's not outputting to a file or whatever.
			log.error("An unknown error occurred while writing the NBT data to bytes.", e);
		}
		return MoreCommands.encodeHex(bytestream.toByteArray());
	}

	private static CompoundTag nbtFromByteString(String byteString) {
		if (byteString == null) return null;
		byte[] bytes;
		try {
			bytes = MoreCommands.decodeHex(byteString);
		} catch (Exception e) {
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
			MoreCommands.saveJson(MoreCommands.getRelativePath().resolve("kits.json"), instance.kits.entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().serialise())));
		} catch (IOException e) {
			log.error("Couldn't save kits file.", e);
		}
	}

	@ExtensionMethod(CollectionExtensions.class)
	private static class Kit {
		@Getter
		private final String name;
		@Getter
		private final int cooldown;
		private final Map<UUID, Long> cooldowns = new HashMap<>();
		private final List<ItemStack> items = new ArrayList<>();

		public Kit(String name, int cooldown, List<ItemStack> items) {
			this.name = name;
			this.cooldown = cooldown;
			this.items.addAll(items);
		}

		public List<ItemStack> getItems() {
			return items.immutable();
		}

		public void give(Player player) {
			if (!onCooldown(player)) {
				for (ItemStack stack : items) {
					ItemStack stack0 = stack.copy();
					if (!player.getInventory().add(stack0)) player.drop(stack0, false);
				}
				if (cooldown > 0) {
					cooldowns.put(Compat.get().getUUID(player), System.currentTimeMillis() + cooldown * 1000L);
					saveKits();
				}
			}
		}

		public boolean onCooldown(Player player) {
			return getRemainingCooldown(player) > 0;
		}

		public long getRemainingCooldown(Player player) {
			return cooldowns.containsKey(Compat.get().getUUID(player)) ? cooldowns.get(Compat.get().getUUID(player)) - System.currentTimeMillis() : 0;
		}

		public Map<String, Object> serialise() {
			Map<String, Object> data = new LinkedHashMap<>();
			data.put("name", name);
			data.put("cooldown", cooldown);
			data.put("cooldowns", cooldowns.entrySet().stream()
					.map(entry -> Pair.of(entry.getKey().toString(), entry.getValue()))
					.filter(pair -> pair.getSecond() - System.currentTimeMillis() > 0)
					.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
			data.put("items", items.stream().filter(stack -> !stack.isEmpty()).map(KitCommand::serialiseItemStack).collect(Collectors.toList()));
			return data;
		}

		public static Kit deserialise(Map<String, Object> data) {
			String name = (String) data.get("name");
			int cooldown = ((Double) data.get("cooldown")).intValue();
			Map<UUID, Long> cooldowns = ((Map<String, Long>) data.get("cooldowns")).entrySet().stream()
					.map(entry -> Pair.of(UUID.fromString(entry.getKey()), entry.getValue()))
					.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
			List<ItemStack> items = ((List<Map<String, Object>>) data.get("items")).stream().map(KitCommand::deserialiseItemStack).collect(Collectors.toList());
			Kit kit = new Kit(name, cooldown, items);
			kit.cooldowns.putAll(cooldowns);
			return kit;
		}
	}
}
