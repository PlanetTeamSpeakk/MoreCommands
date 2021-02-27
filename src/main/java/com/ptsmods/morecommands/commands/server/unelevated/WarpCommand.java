package com.ptsmods.morecommands.commands.server.unelevated;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.miscellaneous.Location;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WarpCommand extends Command {

    private final Map<UUID, List<Warp>> warps = new HashMap<>();
    private final List<Warp> allWarps = new ArrayList<>();
    private final List<UUID> dirty = new ArrayList<>();

    public void init(MinecraftServer server) {
        File oldDir = new File("config/MoreCommands/warps/");
        if (oldDir.exists()) {
            for (File f : MoreObjects.firstNonNull(oldDir.listFiles(), new File[0]))
                MoreCommands.tryMove(f.getAbsolutePath(), MoreCommands.getRelativePath() + "warps/" + f.getName());
            oldDir.delete();
        }
        File dir = new File(MoreCommands.getRelativePath() + "warps/");
        if (!dir.exists()) dir.mkdirs();
        for (File f : MoreObjects.firstNonNull(dir.listFiles(), new File[0])) {
            UUID owner = UUID.fromString(f.getName().split("\\.")[0]);
            Map<String, Map<String, ?>> data;
            try {
                data = MoreCommands.readJson(getWarpsFile(owner));
            } catch (IOException e) {
                log.error("Unknown error while reading warps file of player " + owner + ".", e);
                continue;
            }
            if (data == null) data = new HashMap<>();
            List<Warp> warpList = new ArrayList<>();
            for (String name : data.keySet())
                warpList.add(fromMap(server, name, owner, data.get(name)));
            allWarps.addAll(warpList);
            warps.put(owner, warpList);
        }
        allWarps.sort(Comparator.comparing(Warp::getCreationDate));
        AtomicInteger i = new AtomicInteger(0);
        ServerTickEvents.START_SERVER_TICK.register(server0 -> {
            if (i.incrementAndGet() % 100 == 0) {
                i.set(0);
                try {
                    save();
                } catch (IOException e) {
                    log.error("An unknown error occurred while saving the warps.", e);
                }
            }
        });
    }

    public List<Warp> getWarps() {
        return ImmutableList.copyOf(allWarps);
    }

    public List<String> getWarpNames() {
        List<String> names = new ArrayList<>();
        for (Warp warp : getWarps())
            names.add(warp.getName());
        return names;
    }

    public List<Warp> getWarpsOf(ServerPlayerEntity player) {
        return warps.getOrDefault(player.getUuid(), Collections.emptyList());
    }

    public List<String> getWarpNamesOf(ServerPlayerEntity player) {
        List<String> warps = new ArrayList<>();
        for (Warp warp : getWarpsOf(player))
            warps.add(warp.getName());
        return warps;
    }

    public List<Warp> getWarpsFor(ServerPlayerEntity p) {
        List<Warp> warps = new ArrayList<>();
        for (Warp warp : getWarps())
            if (warp.mayTeleport(p))
                warps.add(warp);
        return warps;
    }

    public List<String> getWarpNamesFor(ServerPlayerEntity player) {
        List<String> warps = new ArrayList<>();
        for (Warp warp : getWarpsFor(player))
            warps.add(warp.getName());
        return warps;
    }

    public Warp createWarp(String name, UUID owner, Vec3d loc, Vec2f rotation, ServerWorld world, boolean isLimited) {
        if (getWarp(name) != null) return null;
        Warp warp = new Warp(name, owner, new Location<>(world, loc, rotation), isLimited, 0, new Date());
        warp.setDirty(true);
        if (owner == null) owner = getServerUuid(world.getServer());
        if (!warps.containsKey(owner))
            warps.put(owner, new ArrayList<>());
        warps.get(owner).add(warp);
        allWarps.add(warp);
        return warp;
    }

    public Warp getWarp(String name) {
        for (Warp warp : allWarps)
            if (warp.getName().equalsIgnoreCase(name))
                return warp;
        return null;
    }

    public void save() throws IOException {
        for (UUID player : ImmutableList.copyOf(dirty))
            save(player);
    }

    public void save(UUID owner) throws IOException {
        if (dirty.contains(owner)) {
            Map<String, Map<String, Object>> data = new HashMap<>();
            for (Warp warp : warps.getOrDefault(owner, Collections.emptyList()))
                data.put(warp.getName(), warp.toMap());
            File f = getWarpsFile(owner);
            if (!f.exists()) f.createNewFile();
            dirty.remove(owner);
            MoreCommands.saveJson(f, data);
        }
    }

    public File getWarpsFile(UUID id) {
        return new File("config/MoreCommands/warps/" + id + ".json");
    }

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("warp").executes(ctx -> executeList(ctx, 1)).then(argument("page", IntegerArgumentType.integer(1)).executes(ctx -> executeList(ctx, ctx.getArgument("page", Integer.class)))).then(argument("name", StringArgumentType.word()).executes(ctx -> {
            String name = ctx.getArgument("name", String.class);
            if (MoreCommands.isInteger(name) && Integer.parseInt(name) > 0) return executeList(ctx, Integer.parseInt(name));
            Warp warp = getWarp(ctx.getArgument("name", String.class));
            if (warp == null) sendMsg(ctx, Formatting.RED + "A warp by that name could not be found.");
            else if (!warp.mayTeleport(ctx.getSource().getPlayer())) sendMsg(ctx, Formatting.RED + "You may not go there, sorry!");
            else {
                warp.teleport(ctx.getSource().getPlayer());
                return 1;
            }
            return 0;
        })));
        dispatcher.register(literal("setwarp").then(argument("name", StringArgumentType.word()).executes(ctx -> {
            String name = ctx.getArgument("name", String.class);
            if (getWarp(name) != null) sendMsg(ctx, Formatting.RED + "A warp by that name already exists, please delete it first.");
            else {
                Warp warp = createWarp(name, ctx.getSource().getEntity() instanceof ServerPlayerEntity ? ctx.getSource().getPlayer().getUuid() : getServerUuid(ctx.getSource().getMinecraftServer()), ctx.getSource().getPosition(), ctx.getSource().getRotation(), ctx.getSource().getWorld(), false);
                sendMsg(ctx, "The warp has been created! You can teleport to it with " + SF + "/warp " + warp.getName() + DF + " and view its stats with " + SF + "/warpinfo " + warp.getName() + DF + "." + (isOp(ctx) ? " You can also limit it to use by only other operators with " + SF + "/limitwarp " + warp.getName() + DF + "." : ""));
                return 1;
            }
            return 0;
        })));
        dispatcher.register(literal("delwarp").then(argument("name", StringArgumentType.word()).executes(ctx -> {
            String name = ctx.getArgument("name", String.class);
            Warp warp = getWarp(name);
            UUID id = ctx.getSource().getEntity() instanceof ServerPlayerEntity ? ctx.getSource().getPlayer().getUuid() : getServerUuid(ctx.getSource().getMinecraftServer());
            if (warp == null) sendMsg(ctx, Formatting.RED + "A warp by that name could not be found.");
            else if (!isOp(ctx) && !warp.getOwner().equals(id)) sendMsg(ctx, Formatting.RED + "You have no control over that warp.");
            else {
                warp.delete();
                sendMsg(ctx, "The warp has been deleted.");
                return 1;
            }
            return 0;
        })));
        dispatcher.register(literal("limitwarp").requires(IS_OP).then(argument("name", StringArgumentType.word()).executes(ctx -> {
            Warp warp = getWarp(ctx.getArgument("name", String.class));
            if (warp == null) sendMsg(ctx, Formatting.RED + "A warp by that name could not be found.");
            else {
                warp.setLimited(!warp.isLimited());
                sendMsg(ctx, "The given warp is now " + formatFromBool(warp.isLimited(), Formatting.GREEN + "limited", Formatting.RED + "unlimited") + DF + ".");
                return 1;
            }
            return 0;
        })));
        SimpleDateFormat format = new SimpleDateFormat("d MMMM yyyy HH:mm:ss");
        dispatcher.register(literal("warpinfo").then(argument("name", StringArgumentType.word()).executes(ctx -> {
            Warp warp = getWarp(ctx.getArgument("name", String.class));
            if (warp == null) sendMsg(ctx, Formatting.RED + "A warp by that name could not be found.");
            else {
                StringBuilder header = new StringBuilder();
                for (int i = 0; i < 35; i++)
                    if (i == 17)
                        header.append(DF).append("WARPINFO FOR ").append(SF).append(warp.getName());
                    else header.append(i % 16 % 2 == 0 ? SF + "-" : DF + "=");
                sendMsg(ctx, header.toString());
                sendMsg(ctx, "Owner: " + SF + (ctx.getSource().getMinecraftServer().getPlayerManager().getPlayer(warp.getOwner()) == null ? warp.getOwner() : MoreCommands.textToString(ctx.getSource().getMinecraftServer().getPlayerManager().getPlayer(warp.getOwner()).getDisplayName(), null)));
                sendMsg(ctx, "Created at: " + SF + format.format(warp.getCreationDate()));
                sendMsg(ctx, "Location: " + SF + "X: " + warp.getPos().x + DF + ", " + SF + "Y: " + warp.getPos().y + DF + ", " + SF + "Z: " + warp.getPos().z);
                sendMsg(ctx, "Rotation: " + SF + "yaw: " + warp.getYaw() + DF + ", " + SF + "pitch: " + warp.getPitch());
                sendMsg(ctx, "World: " + SF + warp.getWorld().getRegistryKey().getValue().toString());
                sendMsg(ctx, "Limited: " + formatFromBool(warp.isLimited, "true", "false"));
                sendMsg(ctx, "Used: " + SF + warp.getCounter() + " times");
                return 1;
            }
            return 0;
        })));
    }

    @Override
    public boolean forDedicated() {
        return true;
    }

    private int executeList(CommandContext<ServerCommandSource> ctx, int page) throws CommandSyntaxException {
        List<String> warps = getWarpNamesFor(ctx.getSource().getPlayer());
        if (warps.isEmpty()) sendMsg(ctx, Formatting.RED + "There are no warps set as of right now.");
        else {
            int pages = warps.size() / 15 + 1;
            if (page > pages) page = pages;
            warps = warps.subList((page - 1) * 15, Math.min(page * 15, warps.size()));
            StringBuilder header = new StringBuilder();
            for (int i = 0; i < 35; i++)
                if (i == 17)
                    header.append(DF).append("PAGE ").append(SF).append(page).append(DF).append("/").append(SF).append(pages);
                else header.append(i % 16 % 2 == 0 ? SF + "-" : DF + "=");
            sendMsg(ctx, header.toString());
            sendMsg(ctx, joinNicely(warps));
        }
        return warps.size();
    }

    private Warp fromMap(MinecraftServer server, String name, UUID owner, Map<?, ?> data) {
        return new Warp(name, owner, new Location<>(server.getWorld(RegistryKey.of(Registry.DIMENSION, new Identifier((String) data.get("world")))), new Vec3d((Double) data.get("x"), (Double) data.get("y"), (Double) data.get("z")), new Vec2f(((Double) data.get("yaw")).floatValue(), ((Double) data.get("pitch")).floatValue())), (Boolean) data.get("isLimited"), ((Double) data.get("counter")).intValue(), new Date(((Double) data.get("creationDate")).longValue()));
    }

    public class Warp {
        private final String name;
        private final UUID owner;
        private final Location<ServerWorld> loc;
        private boolean isLimited;
        private int counter;
        private final Date creationDate;

        public Warp(String name, UUID owner, Location<ServerWorld> loc, boolean isLimited, int counter, Date creationDate) {
            this.name = name;
            this.owner = owner;
            this.loc = loc;
            this.isLimited = isLimited;
            this.counter = counter;
            this.creationDate = creationDate;
            if (loc.getWorld() == null) delete();
        }

        public String getName() {
            return name;
        }

        public UUID getOwner() {
            return owner;
        }

        public Vec3d getPos() {
            return loc.getPos();
        }

        public float getYaw() {
            return loc.getRot().x;
        }

        public float getPitch() {
            return loc.getRot().y;
        }

        public ServerWorld getWorld() {
            return loc.getWorld();
        }

        public boolean isLimited() {
            return isLimited;
        }

        public void setLimited(boolean isLimited) {
            this.isLimited = isLimited;
            setDirty(true);
        }

        public int getCounter() {
            return counter;
        }

        public Date getCreationDate() {
            return creationDate;
        }

        public void teleport(ServerPlayerEntity p) {
            teleport(p, true);
        }

        public void teleport(ServerPlayerEntity p, boolean count) {
            if (mayTeleport(p)) {
                MoreCommands.teleport(p, getWorld(), getPos(), getYaw(), getPitch());
                if (count) {
                    sendMsg(p, Formatting.GREEN + "W" + Formatting.BLUE + "h" + Formatting.YELLOW + "oo" + Formatting.RED + "s" + Formatting.LIGHT_PURPLE + "h" + Formatting.WHITE + "!");
                    counter++;
                    setDirty(true);
                }
            } else sendMsg(p, Formatting.RED + "You must be an operator to teleport to this warp.");
        }

        public boolean isDirty() {
            return dirty.contains(getOwner());
        }

        public void setDirty(boolean b) {
            if (b && !isDirty()) dirty.add(getOwner());
            else if (!b && isDirty()) dirty.remove(getOwner());
        }

        public boolean mayTeleport(ServerPlayerEntity player) {
            return !isLimited || player.hasPermissionLevel(Objects.requireNonNull(player.getServer()).getOpPermissionLevel());
        }

        public void delete() {
            warps.get(owner).remove(this);
            allWarps.remove(this);
            setDirty(true);
        }

        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("counter", counter);
            map.put("world", getWorld().getRegistryKey().getValue().toString());
            map.put("x", loc.getPos().x);
            map.put("y", loc.getPos().y);
            map.put("z", loc.getPos().z);
            map.put("yaw", getYaw());
            map.put("pitch", getPitch());
            map.put("creationDate", creationDate.getTime());
            return map;
        }

    }
}