package com.ptsmods.morecommands.commands.server.unelevated;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.MoreCommandsArch;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.api.util.extensions.CollectionExtensions;
import com.ptsmods.morecommands.api.util.extensions.ObjectExtensions;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.mysqlw.Database;
import com.ptsmods.mysqlw.query.QueryCondition;
import com.ptsmods.mysqlw.query.SelectResults;
import com.ptsmods.mysqlw.table.ColumnType;
import com.ptsmods.mysqlw.table.TableIndex;
import com.ptsmods.mysqlw.table.TablePreset;
import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@ExtensionMethod({ObjectExtensions.class, CollectionExtensions.class})
public class KitCommand extends Command {
    private final Map<String, Kit> kits = new LinkedHashMap<>();

    @Override
    public void init(boolean serverOnly, MinecraftServer server) throws Exception {
        Database db = getLocalDb();

        CompletableFuture.allOf(
                TablePreset.create("kits")
                        .putColumn("name", ColumnType.VARCHAR.struct()
                                .configure(f -> f.apply(255))
                                .setPrimary(true))
                        .putColumn("cooldown", ColumnType.INT.struct()
                                .configure(f -> f.apply(null)))
                        .putColumn("items", ColumnType.LONGTEXT.struct())
                        .createAsync(db),
                TablePreset.create("kit_cooldowns")
                        .putColumn("kit", ColumnType.VARCHAR.struct()
                                .configure(f -> f.apply(255)))
                        .putColumn("player", ColumnType.CHAR.struct()
                                .configure(f -> f.apply(36)))
                        .putColumn("epoch", ColumnType.TIMESTAMP.struct())
                        .addIndex(TableIndex.index("kit", TableIndex.Type.INDEX))
                        .addIndex(TableIndex.index("player", TableIndex.Type.INDEX))
                        .createAsync(db))
                .thenAccept(p -> {
                    for (SelectResults.SelectResultRow row : db.select("kits", "*"))
                        kits.put(row.getString("name"), Kit.deserialiseDb(row));
                });


        kits.putAll(MoreCommands.<Map<String, Map<String, Object>>>readJson(MoreCommands.getRelativePath(server).resolve("kits.json").toFile())
                .or(new HashMap<String, Map<String, Object>>()).entrySet().stream()
                .map(entry -> new Tuple<>(entry.getKey(), Kit.deserialise(entry.getValue())))
                .collect(Collectors.toMap(Tuple::getA, Tuple::getB)));
        Files.deleteIfExists(MoreCommands.getRelativePath(server).resolve("kits.json"));
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) throws Exception {
        dispatcher.register(literalReq("kit")
                .then(argument("kit", StringArgumentType.word())
                        .executes(ctx -> {
                            String kit = ctx.getArgument("kit", String.class).toLowerCase(Locale.ROOT);
                            if (!kits.containsKey(kit)) sendError(ctx, "A kit by that name does not exist.");
                            else if (kits.get(kit).onCooldown(ctx.getSource().getPlayerOrException())) {
                                Kit kit0 = kits.get(kit);

                                if (kit0.getCooldown() > 0)
                                    sendError(ctx, "You're still on cooldown! Please wait " +
                                        MoreCommands.formatSeconds(kits.get(kit).getRemainingCooldown(ctx.getSource().getPlayerOrException()) / 1000, ChatFormatting.RED, ChatFormatting.RED) + ChatFormatting.RED + ".");
                                else sendError(ctx, "That kit can only be used once.");
                            }
                            else if (!MoreCommandsArch.checkPermission(ctx.getSource(), "morecommands.kit." + kit, true)) sendError(ctx, "You do not have permission to use that kit.");
                            else {
                                kits.get(kit).give(ctx.getSource().getPlayerOrException());
                                sendMsg(ctx, "You have been given the " + SF + kits.get(kit).getName() + DF + " kit.");
                                return 1;
                            }
                            return 0;
                        })));

        dispatcher.register(literalReqOp("createkit")
                .then(argument("name", StringArgumentType.word())
                        .then(argument("cooldown", IntegerArgumentType.integer(-1))
                                .executes(ctx -> {
                                    String name = ctx.getArgument("name", String.class);
                                    if (kits.containsKey(name.toLowerCase(Locale.ROOT))) sendError(ctx, "A kit with that name already exists.");
                                    else {
                                        Inventory inv = Compat.get().getInventory(ctx.getSource().getPlayerOrException());
                                        Kit kit = new Kit(name, ctx.getArgument("cooldown", Integer.class), Lists.newArrayList(inv.items, inv.armor, inv.offhand).stream()
                                                .flatMap(List::stream)
                                                .collect(Collectors.toList()));
                                        kits.put(name.toLowerCase(Locale.ROOT), kit);

                                        getLocalDb().insertBuilder("kits", "name", "cooldown", "items")
                                                .insert(kit.getName(), kit.getCooldown(), kit.getItems().stream()
                                                        .map(stack -> MoreCommands.nbtToByteString(serialiseStackToNBT(stack)))
                                                        .collect(Collectors.joining(";")))
                                                .executeAsync()
                                                .thenAccept(i -> scheduleTask(() -> sendMsg(ctx, "Kit " + SF + name + DF + " has been made.")));
                                        return 1;
                                    }
                                    return 0;
                                }))));

        dispatcher.register(literalReqOp("delkit")
                .then(argument("kit", StringArgumentType.word())
                        .executes(ctx -> {
                            String kit = ctx.getArgument("kit", String.class);
                            if (!kits.containsKey(kit)) sendError(ctx, "No kit by that name exists.");
                            else {
                                Kit kit0 = kits.remove(kit);
                                getLocalDb().deleteAsync("kits", QueryCondition.equals("name", kit0.getName()))
                                        .thenAccept(i -> scheduleTask(() -> sendMsg(ctx, "Kit " + SF + kit0.getName() + DF + " has been removed.")));
                                return 1;
                            }
                            return 0;
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/kit";
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

    private static CompoundTag serialiseStackToNBT(ItemStack stack) {
        CompoundTag compound = new CompoundTag();
        compound.putString("item", Registry.ITEM.getKey(stack.getItem()).toString());
        compound.putInt("count", stack.getCount());
        compound.put("tag", stack.getTag());
        return compound;
    }

    private static ItemStack deserialiseStack(Map<String, Object> data) {
        ItemStack stack = new ItemStack(Registry.ITEM.get(new ResourceLocation((String) data.get("item"))), ((Double) data.get("count")).intValue());
        stack.setTag(MoreCommands.nbtFromByteString((String) data.get("tag")));
        return stack;
    }

    private static ItemStack deserialiseStack(CompoundTag compound) {
        ResourceLocation item = new ResourceLocation(compound.getString("item"));
        int count = compound.getInt("count");
        CompoundTag tag = compound.getCompound("tag");

        ItemStack stack = new ItemStack(Registry.ITEM.get(item), count);
        stack.setTag(tag);
        return stack;
    }

    @ExtensionMethod(CollectionExtensions.class)
    private static class Kit {
        @Getter
        private final String name;
        @Getter
        private final int cooldown;
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
            if (onCooldown(player)) return;

            for (ItemStack stack : items) {
                ItemStack stack0 = stack.copy();
                if (!Compat.get().getInventory(player).add(stack0)) player.drop(stack0, false);
            }

            if (cooldown == 0) return;

            MoreCommands.getLocalDb().insertBuilder("kit_cooldowns", "kit", "player", "time")
                    .insert(name, player.getUUID(), Timestamp.from(Instant.now().plusMillis(cooldown)))
                    .executeAsync();
        }

        public boolean onCooldown(Player player) {
            return getRemainingCooldown(player) > 0;
        }

        public long getRemainingCooldown(Player player) {
            return getLocalDb().selectBuilder("kit_cooldowns")
                    .select("epoch")
                    .where(QueryCondition.equals("kit", getName()).and(QueryCondition.equals("player", player.getUUID())))
                    .execute()
                    .stream()
                    .findFirst()
                    .map(row -> cooldown < 0 ? Long.MAX_VALUE : row.getTimestamp("epoch").getTime() - System.currentTimeMillis())
                    .orElse(0L);
        }

        public static Kit deserialise(Map<String, Object> data) {
            String name = (String) data.get("name");
            int cooldown = ((Double) data.get("cooldown")).intValue();
            Map<UUID, Long> cooldowns = ((Map<String, Long>) data.get("cooldowns")).entrySet().stream()
                    .map(entry -> new Tuple<>(UUID.fromString(entry.getKey()), entry.getValue()))
                    .collect(Collectors.toMap(Tuple::getA, Tuple::getB));
            List<ItemStack> items = ((List<Map<String, Object>>) data.get("items")).stream()
                    .map(KitCommand::deserialiseStack)
                    .collect(Collectors.toList());
            Kit kit = new Kit(name, cooldown, items);

            getLocalDb().insertBuilder("kits", "name", "cooldown", "items")
                    .insert(kit.getName(), kit.getCooldown(), kit.getItems().stream()
                            .map(stack -> MoreCommands.nbtToByteString(serialiseStackToNBT(stack)))
                            .collect(Collectors.joining(";")))
                    .executeAsync();

            getLocalDb().insertBuilder("kit_cooldowns", "kit", "player", "time")
                    .insert(cooldowns.entrySet().stream()
                            .map(entry -> new Object[] {kit.getName(), entry.getKey(), entry.getValue()})
                            .collect(Collectors.toList()));

            return kit;
        }

        public static Kit deserialiseDb(SelectResults.SelectResultRow row) {
            String name = row.getString("name");
            int cooldown = row.getInt("cooldown");
            List<ItemStack> stacks = Arrays.stream(row.getString("items").split(";")).map(s -> deserialiseStack(MoreCommands.nbtFromByteString(s))).collect(Collectors.toList()).immutable();

            //kit.cooldowns.putAll(cooldownRows.stream().collect(Collectors.toMap(cdRow -> cdRow.get("player", UUID.class), cdRow -> cdRow.getTimestamp("epoch").getTime())));
            return new Kit(name, cooldown, stacks);
        }
    }
}
