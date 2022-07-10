package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import com.ptsmods.morecommands.mixin.common.accessor.MixinEntityAccessor;
import com.ptsmods.mysqlw.query.QueryCondition;
import com.ptsmods.mysqlw.query.builder.InsertBuilder;
import com.ptsmods.mysqlw.table.ColumnType;
import com.ptsmods.mysqlw.table.TableIndex;
import com.ptsmods.mysqlw.table.TablePreset;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class HomeCommand extends Command {
    private final Map<UUID, List<Home>> homes = new HashMap<>();

    @Override
    public void preinit() throws Exception {
        PlayerEvent.PLAYER_JOIN.register(player -> {
            if (homes.containsKey(player.getUUID())) return; // Still in cache

            getLocalDb().selectBuilder("homes")
                    .select("*")
                    .where(QueryCondition.equals("owner", player.getUUID()))
                    .executeAsync()
                    .thenAccept(res -> {
                        homes.put(player.getUUID(), getLocalDb().select("homes", "*", QueryCondition.equals("owner", player.getUUID())).stream()
                                .map(row -> new Home(row.getString("name"), row.getDouble("x"), row.getDouble("y"), row.getDouble("z"),
                                        row.getFloat("pitch"), row.getFloat("yaw"), row.get("dimension", ResourceLocation.class))).collect(Collectors.toList()));
                    });
        });

        PlayerEvent.PLAYER_QUIT.register(player -> {
            MinecraftServer server0 = player.getServer();
            UUID id = player.getUUID();
            scheduleTask(() -> {
                if (Objects.requireNonNull(server0).getPlayerList().getPlayer(id) == null) homes.remove(id);
            }, 600); // Clear cache if player does not relog within 30 seconds
        });
    }

    public void init(boolean serverOnly, MinecraftServer server) throws IOException {
        Path dataFile = MoreCommands.getRelativePath().resolve("homes.json");
        Map<String, Map<String, Map<String, Object>>> data = null;
        try {
            data = MoreCommands.readJson(dataFile);
        } catch (IOException e) {
            log.catching(e);
        } catch (NullPointerException ignored) {}
        homes.clear();

        TablePreset.create("homes")
                .putColumn("owner", ColumnType.CHAR.createStructure()
                        .configure(f -> f.apply(36))
                        .setPrimary(true)
                        .setNullAllowed(false))
                .putColumn("name", ColumnType.VARCHAR.createStructure()
                        .configure(f -> f.apply(255))
                        .setNullAllowed(false))
                .putColumn("x", ColumnType.DOUBLE.createStructure()
                        .configure(f -> f.apply(null, null)))
                .putColumn("y", ColumnType.DOUBLE.createStructure()
                        .configure(f -> f.apply(null, null)))
                .putColumn("z", ColumnType.DOUBLE.createStructure()
                        .configure(f -> f.apply(null, null)))
                .putColumn("pitch", ColumnType.FLOAT.createStructure()
                        .configure(f -> f.apply(null, null)))
                .putColumn("yaw", ColumnType.FLOAT.createStructure()
                        .configure(f -> f.apply(null, null)))
                .putColumn("dimension", ColumnType.TEXT.createStructure())
                .addIndex(TableIndex.index("name", TableIndex.Type.INDEX))
                .create(getLocalDb());

        Files.delete(dataFile);
        if (data == null) return;

        InsertBuilder insert = getLocalDb().insertBuilder("homes", "owner", "name", "x", "y", "z", "pitch", "yaw", "dimension");
        data.forEach((key, value) -> {
            UUID owner = UUID.fromString(key);
            List<Home> homes = new ArrayList<>();

            for (Map.Entry<String, Map<String, Object>> entry : value.entrySet()) {
                homes.add(Home.fromMap(entry));
                insert.insert(owner, entry.getKey(), entry.getValue().get("x"), entry.getValue().get("y"), entry.getValue().get("z"), entry.getValue().get("pitch"), entry.getValue().get("yaw"), entry.getValue().get("dimension"));
            }
            this.homes.put(owner, homes);
        });
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReq("home")
                .executes(ctx -> executeHome(ctx, null))
                .then(argument("home", StringArgumentType.word())
                        .executes(ctx -> executeHome(ctx, ctx.getArgument("home", String.class)))));

        dispatcher.register(literalReq("homes")
                .executes(ctx -> sendHomes(ctx.getSource().getPlayerOrException())));

        dispatcher.register(literalReq("sethome")
                .executes(ctx -> executeSetHome(ctx, "home"))
                .then(argument("name", StringArgumentType.word())
                        .executes(ctx -> executeSetHome(ctx, ctx.getArgument("name", String.class)))));

        dispatcher.register(literalReq("delhome")
                .then(argument("home", StringArgumentType.word())
                        .executes(ctx -> {
                            Player p = ctx.getSource().getPlayerOrException();
                            Home home = getHome(p, ctx.getArgument("home", String.class));
                            if (!homes.containsKey(p.getUUID())) sendHomes(p); // Will send error msg.
                            else if (home == null) sendError(ctx, "Could not find a home by that name.");
                            else {
                                getHomes(p).remove(home);
                                if (getHomes(p).isEmpty()) homes.remove(p.getUUID());
                                getLocalDb().delete("homes", QueryCondition.equals("owner", p.getUUID()).and(QueryCondition.equals("name", home.name)));
                                sendMsg(ctx, "Your home " + SF + home.name + DF + " was removed.");
                                return 1;
                            }
                            return 0;
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/home";
    }

    private int executeSetHome(CommandContext<CommandSourceStack> ctx, String name) throws CommandSyntaxException {
        Player p = ctx.getSource().getPlayerOrException();
        int globalMax = p.getCommandSenderWorld().getGameRules().getInt(MoreGameRules.get().maxHomesRule());
        int max = getCountFromPerms(ctx.getSource(), "morecommands.sethome.", globalMax);
        if (max < 0) max = Integer.MAX_VALUE;
        boolean bypass = isOp(ctx);
        if (max == 0 && !bypass) sendError(ctx, "Homes are currently disabled" + (globalMax > 0 ? " (for you)" : "") + ".");
        else if (getHomes(p).size() >= max && !bypass) sendError(ctx, "You cannot set more than " + max + " homes.");
        else {
            if (!homes.containsKey(p.getUUID())) homes.put(p.getUUID(), new ArrayList<>());

            Home home = new Home(name, p.position().x, p.position().y, p.position().z, ((MixinEntityAccessor) p).getXRot_(), ((MixinEntityAccessor) p).getYRot_(), p.getCommandSenderWorld().dimension().location());
            getHomes(p).add(home);
            getLocalDb().insert("homes", new String[] {"owner", "name", "x", "y", "z", "pitch", "yaw", "dimension"},
                    new Object[] {p.getUUID(), home.name, home.x, home.y, home.z, home.pitch, home.yaw, home.dimension});

            sendMsg(ctx, "A home by the name of " + SF + name + DF + " has been set.");
        }
        return homes.get(p.getUUID()).size();
    }

    private int executeHome(CommandContext<CommandSourceStack> ctx, String name) throws CommandSyntaxException {
        Player player = ctx.getSource().getPlayerOrException();
        if (getHomes(player).isEmpty() || name == null && getHomes(player).size() > 1) sendHomes(player);
        else {
            // Get home named 'home' if no home was given or, if that does not exist, get the first set home.
            Home home = getHome(player, name == null ? getHome(player, "home") == null ? getHomes(player).get(0).name : "home" : name);
            if (home == null) sendHomes(player);
            else return tpHome(player, home);
        }
        return 0;
    }

    private int sendHomes(Player player) {
        sendMsg(player, getHomes(player).isEmpty() ? ChatFormatting.RED + "You do not have any homes set yet, set one with /sethome [name]." : "You have set the following homes: " +
                joinNicely(getHomes(player).stream().collect(Collector.of(ArrayList::new, (l, home) -> l.add(home.name), BinaryOperator.maxBy(Comparator.comparingInt(List::size))))) + ".");
        return getHomes(player).size();
    }

    private int tpHome(Player player, Home home) {
        MoreCommands.teleport(player, Objects.requireNonNull(player.getServer()).getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, home.dimension)), home.x, home.y, home.z, home.yaw, home.pitch);
        ResourceKey<Level> registryKey = player.getCommandSenderWorld().dimension();
        if (Level.NETHER.equals(registryKey)) return 9;
        else if (Level.OVERWORLD.equals(registryKey)) return 10;
        else if (Level.END.equals(registryKey)) return 11;
        return 12;
    }

    private Home getHome(Player player, String name) {
        for (Home home : getHomes(player))
            if (home.name.equalsIgnoreCase(name))
                return home;
        return null;
    }

    private List<Home> getHomes(Player player) {
        return homes.getOrDefault(player.getUUID(), Collections.emptyList());
    }

    private static class Home {
        private final String name;
        private final double x, y, z;
        private final float pitch, yaw;
        private final ResourceLocation dimension;

        private Home(String name, double x, double y, double z, float pitch, float yaw, ResourceLocation dimension) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.z = z;
            this.pitch = pitch;
            this.yaw = yaw;
            this.dimension = dimension;
        }

        private static Home fromMap(Map.Entry<String, Map<String, Object>> data) {
            Map<String, Object> v = data.getValue();
            return new Home(data.getKey(), (Double) v.get("x"), (Double) v.get("y"), (Double) v.get("z"), ((Double) v.get("pitch")).floatValue(), ((Double) v.get("yaw")).floatValue(), new ResourceLocation((String) v.get("dimension")));
        }
    }
}
