package com.ptsmods.morecommands.commands.server.unelevated;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsArch;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.callbacks.CreateWorldEvent;
import com.ptsmods.morecommands.api.util.extensions.ObjectExtensions;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.miscellaneous.Location;
import dev.architectury.event.events.common.TickEvent;
import lombok.experimental.ExtensionMethod;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@ExtensionMethod(ObjectExtensions.class)
public class WarpCommand extends Command {
    private final Map<UUID, List<Warp>> warps = new HashMap<>();
    private final List<Warp> allWarps = new ArrayList<>();
    private final Set<UUID> dirty = new HashSet<>();

    public void init(boolean serverOnly, MinecraftServer server) {
        File oldDir = MoreCommandsArch.getConfigDirectory().resolve("warps/").toFile();
        if (oldDir.exists()) {
            for (File f : oldDir.listFiles().or(new File[0]))
                MoreCommands.tryMove(f.getAbsolutePath(), MoreCommands.getRelativePath() + "warps/" + f.getName());
            oldDir.delete();
        }
        File dir = new File(MoreCommands.getRelativePath() + "warps/");
        if (!dir.exists()) dir.mkdirs();
        CreateWorldEvent.EVENT.register(server0 -> {
            for (File f : dir.listFiles().or(new File[0])) {
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
                allWarps.addAll(warpList.stream().filter(Objects::nonNull).collect(Collectors.toList()));
                warps.put(owner, warpList);
            }
            allWarps.sort(Comparator.comparing(Warp::getCreationDate));
        });
        AtomicInteger i = new AtomicInteger(0);
        TickEvent.SERVER_PRE.register(server0 -> {
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
            for (Warp warp : warps.getOrDefault(owner, Collections.emptyList())) {
                Map<String, Object> warpData = warp.toMap();
                if (warpData != null) data.put(warp.getName(), warpData);
            }
            File f = getWarpsFile(owner);
            if (!f.exists()) f.createNewFile();
            dirty.remove(owner);
            MoreCommands.saveJson(f, data);
        }
    }

    public File getWarpsFile(UUID id) {
        return new File(MoreCommands.getRelativePath() + "warps/" + id + ".json");
    }

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literalReq("warp").executes(ctx -> executeList(ctx, 1)).then(argument("page", IntegerArgumentType.integer(1)).executes(ctx -> executeList(ctx, ctx.getArgument("page", Integer.class)))).then(argument("name", StringArgumentType.word()).executes(ctx -> {
            String name = ctx.getArgument("name", String.class);
            if (MoreCommands.isInteger(name) && Integer.parseInt(name) > 0) return executeList(ctx, Integer.parseInt(name));
            Warp warp = getWarp(ctx.getArgument("name", String.class));
            if (warp == null) sendError(ctx, "A warp by that name could not be found.");
            else if (!warp.mayTeleport(ctx.getSource().getPlayerOrThrow())) sendError(ctx, "You may not go there, sorry!");
            else {
                warp.teleport(ctx.getSource().getPlayerOrThrow());
                return 1;
            }
            return 0;
        })));
        dispatcher.register(literalReq("setwarp").then(argument("name", StringArgumentType.word()).executes(ctx -> {
            String name = ctx.getArgument("name", String.class);
            if (getWarp(name) != null) sendError(ctx, "A warp by that name already exists, please delete it first.");
            else {
                Warp warp = createWarp(name, ctx.getSource().getEntity() instanceof ServerPlayerEntity ? ctx.getSource().getPlayerOrThrow().getUuid() : getServerUuid(ctx.getSource().getServer()), ctx.getSource().getPosition(), ctx.getSource().getRotation(), ctx.getSource().getWorld(), false);
                sendMsg(ctx, "The warp has been created! You can teleport to it with " + SF + "/warp " + warp.getName() + DF + " and view its stats with " + SF + "/warpinfo " + warp.getName() + DF + "." + (isOp(ctx) ? " You can also limit it to only be allowed to be used by operators with " + SF + "/limitwarp " + warp.getName() + DF + "." : ""));
                return 1;
            }
            return 0;
        })));
        dispatcher.register(literalReq("delwarp").then(argument("name", StringArgumentType.word()).executes(ctx -> {
            String name = ctx.getArgument("name", String.class);
            Warp warp = getWarp(name);
            UUID id = ctx.getSource().getEntity() instanceof ServerPlayerEntity ? ctx.getSource().getPlayerOrThrow().getUuid() : getServerUuid(ctx.getSource().getServer());
            if (warp == null) sendError(ctx, "A warp by that name could not be found.");
            else if (!isOp(ctx) && !warp.getOwner().equals(id)) sendError(ctx, "You have no control over that warp.");
            else {
                warp.delete();
                sendMsg(ctx, "The warp has been deleted.");
                return 1;
            }
            return 0;
        })));
        dispatcher.register(literalReq("limitwarp").requires(hasPermissionOrOp("morecommands.limitwarp")).then(argument("name", StringArgumentType.word()).executes(ctx -> {
            Warp warp = getWarp(ctx.getArgument("name", String.class));
            if (warp == null) sendError(ctx, "A warp by that name could not be found.");
            else {
                warp.setLimited(!warp.isLimited());
                sendMsg(ctx, "The given warp is now " + Util.formatFromBool(warp.isLimited(), Formatting.GREEN + "limited", Formatting.RED + "unlimited") + DF + ".");
                return 1;
            }
            return 0;
        })));
        SimpleDateFormat format = new SimpleDateFormat("d MMMM yyyy HH:mm:ss");
        dispatcher.register(literalReq("warpinfo").then(argument("name", StringArgumentType.word()).executes(ctx -> {
            Warp warp = getWarp(ctx.getArgument("name", String.class));
            if (warp == null) sendError(ctx, "A warp by that name could not be found.");
            else {
                StringBuilder header = new StringBuilder();
                for (int i = 0; i < 35; i++)
                    if (i == 17)
                        header.append(DF).append("WARPINFO FOR ").append(SF).append(warp.getName());
                    else header.append(i % 16 % 2 == 0 ? SF + "-" : DF + "=");
                sendMsg(ctx, header.toString());
                sendMsg(ctx, "Owner: " + SF + (ctx.getSource().getServer().getPlayerManager().getPlayer(warp.getOwner()) == null ?
                        warp.getOwner() : IMoreCommands.get().textToString(ctx.getSource().getServer().getPlayerManager().getPlayer(warp.getOwner()).getDisplayName(), null, true)));
                sendMsg(ctx, "Created at: " + SF + format.format(warp.getCreationDate()));
                sendMsg(ctx, "Location: " + SF + "X: " + warp.getPos().x + DF + ", " + SF + "Y: " + warp.getPos().y + DF + ", " + SF + "Z: " + warp.getPos().z);
                sendMsg(ctx, "Rotation: " + SF + "yaw: " + warp.getYaw() + DF + ", " + SF + "pitch: " + warp.getPitch());
                sendMsg(ctx, "World: " + SF + warp.getWorld().getRegistryKey().getValue().toString());
                sendMsg(ctx, "Limited: " + Util.formatFromBool(warp.isLimited(), "true", "false"));
                sendMsg(ctx, "Used: " + SF + warp.getCounter() + " times");
                return 1;
            }
            return 0;
        })));
    }

    @Override
    public boolean isDedicatedOnly() {
        return true;
    }

    private int executeList(CommandContext<ServerCommandSource> ctx, int page) throws CommandSyntaxException {
        List<String> warps = getWarpNamesFor(ctx.getSource().getPlayerOrThrow());
        if (warps.isEmpty()) sendError(ctx, "There are no warps set as of right now.");
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
        for (String key : new String[] {"world", "x", "y", "z", "yaw", "pitch", "counter", "creationDate"}) if (!data.containsKey(key)) return null;
        Identifier worldId = new Identifier((String) data.get("world"));
        Warp warp = new Warp(name, owner,
                new Location<>(server.getWorld(server.getWorldRegistryKeys().stream().filter(key -> key.getValue().equals(worldId)).findFirst().orElse(null)),
                        new Vec3d((Double) data.get("x"), (Double) data.get("y"), (Double) data.get("z")),
                        new Vec2f(((Double) data.get("yaw")).floatValue(), ((Double) data.get("pitch")).floatValue())),
                data.containsKey("isLimited") && (Boolean) data.get("isLimited"),
                ((Double) data.get("counter")).intValue(),
                new Date(((Double) data.get("creationDate")).longValue())
        );
        // Forgot to save the isLimited variable at first, so old configs don't yet have it.
        // For that reason, the warp is set to be dirty so it will be saved again later.
        if (!data.containsKey("isLimited")) warp.setDirty(true);
        return warp;
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
            return loc.getRot().y;
        }

        public float getPitch() {
            return loc.getRot().x;
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
            if (b) dirty.add(getOwner());
            else dirty.remove(getOwner());
        }

        public boolean mayTeleport(ServerPlayerEntity player) {
            return !isLimited || player.hasPermissionLevel(Objects.requireNonNull(player.getServer()).getOpPermissionLevel());
        }

        public void delete() {
            Optional.ofNullable(warps.get(owner)).ifPresent(warps -> warps.remove(this));
            allWarps.remove(this);
            setDirty(true);
        }

        public Map<String, Object> toMap() {
            if (getWorld() == null) return null; // Cannot use Collections#emptyMap() as that causes issues with gson.
            Map<String, Object> map = new HashMap<>();
            map.put("counter", counter);
            map.put("world", getWorld().getRegistryKey().getValue().toString());
            map.put("x", loc.getPos().x);
            map.put("y", loc.getPos().y);
            map.put("z", loc.getPos().z);
            map.put("yaw", getYaw());
            map.put("pitch", getPitch());
            map.put("isLimited", isLimited());
            map.put("creationDate", getCreationDate().getTime());
            return map;
        }

    }
}
