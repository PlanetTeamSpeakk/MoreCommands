package com.ptsmods.morecommands.commands.server.unelevated;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.MoreCommandsArch;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.callbacks.CreateWorldEvent;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.api.util.extensions.ObjectExtensions;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.miscellaneous.Location;
import dev.architectury.event.events.common.TickEvent;
import lombok.experimental.ExtensionMethod;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
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

    public List<Warp> getWarpsOf(ServerPlayer player) {
        return warps.getOrDefault(Compat.get().getUUID(player), Collections.emptyList());
    }

    public List<String> getWarpNamesOf(ServerPlayer player) {
        List<String> warps = new ArrayList<>();
        for (Warp warp : getWarpsOf(player))
            warps.add(warp.getName());
        return warps;
    }

    public List<Warp> getWarpsFor(ServerPlayer p) {
        List<Warp> warps = new ArrayList<>();
        for (Warp warp : getWarps())
            if (warp.mayTeleport(p.createCommandSourceStack()))
                warps.add(warp);
        return warps;
    }

    public List<String> getWarpNamesFor(ServerPlayer player) {
        List<String> warps = new ArrayList<>();
        for (Warp warp : getWarpsFor(player))
            warps.add(warp.getName());
        return warps;
    }

    public Warp createWarp(String name, UUID owner, Vec3 loc, Vec2 rotation, ServerLevel world, boolean isLimited) {
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
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReq("warp")
                .executes(ctx -> executeList(ctx, 1))
                .then(argument("page", IntegerArgumentType.integer(1))
                        .executes(ctx -> executeList(ctx, ctx.getArgument("page", Integer.class))))
                .then(argument("name", StringArgumentType.word())
                        .executes(ctx -> {
                            String name = ctx.getArgument("name", String.class);
                            if (MoreCommands.isInteger(name) && Integer.parseInt(name) > 0) return executeList(ctx, Integer.parseInt(name));
                            Warp warp = getWarp(ctx.getArgument("name", String.class));
                            if (warp == null) sendError(ctx, "A warp by that name could not be found.");
                            else if (!warp.mayTeleport(ctx.getSource())) sendError(ctx, "You may not go there, sorry!");
                            else {
                                warp.teleport(ctx.getSource().getPlayerOrException());
                                return 1;
                            }
                            return 0;
                        })));

        dispatcher.register(literalReq("setwarp")
                .then(argument("name", StringArgumentType.word())
                        .executes(ctx -> {
                            String name = ctx.getArgument("name", String.class);
                            if (getWarp(name) != null) sendError(ctx, "A warp by that name already exists, please delete it first.");
                            else {
                                Warp warp = createWarp(name, ctx.getSource().getEntity() instanceof ServerPlayer ? Compat.get().getUUID(ctx.getSource().getPlayerOrException()) :
                                        getServerUuid(ctx.getSource().getServer()), ctx.getSource().getPosition(), ctx.getSource().getRotation(), ctx.getSource().getLevel(), false);
                                sendMsg(ctx, "The warp has been created! You can teleport to it with " + SF + "/warp " + warp.getName() + DF + " and view its stats with " + SF +
                                        "/warpinfo " + warp.getName() + DF + "." + (isOp(ctx) ? " You can also limit it to only be allowed to be used by operators with " + SF +
                                        "/limitwarp " + warp.getName() + DF + "." : ""));
                                return 1;
                            }
                            return 0;
                        })));

        dispatcher.register(literalReq("delwarp")
                .then(argument("name", StringArgumentType.word())
                        .executes(ctx -> {
                            String name = ctx.getArgument("name", String.class);
                            Warp warp = getWarp(name);
                            UUID id = ctx.getSource().getEntity() instanceof ServerPlayer ? Compat.get().getUUID(ctx.getSource().getPlayerOrException()) : getServerUuid(ctx.getSource().getServer());
                            if (warp == null) sendError(ctx, "A warp by that name could not be found.");
                            else if (hasPermissionOrOp("morecommands.delwarp.others").test(ctx.getSource()) && !warp.getOwner().equals(id))
                                sendError(ctx, "You have no control over that warp.");
                            else {
                                warp.delete();
                                sendMsg(ctx, "The warp has been deleted.");
                                return 1;
                            }
                            return 0;
                        })));

        dispatcher.register(literalReqOp("limitwarp")
                .then(argument("name", StringArgumentType.word())
                        .executes(ctx -> {
                            Warp warp = getWarp(ctx.getArgument("name", String.class));
                            if (warp == null) sendError(ctx, "A warp by that name could not be found.");
                            else {
                                warp.setLimited(!warp.isLimited());
                                sendMsg(ctx, "The given warp is now " + Util.formatFromBool(warp.isLimited(), ChatFormatting.GREEN + "limited", ChatFormatting.RED + "unlimited") + DF + ".");
                                return 1;
                            }
                            return 0;
                        })));

        SimpleDateFormat format = new SimpleDateFormat("d MMMM yyyy HH:mm:ss");
        dispatcher.register(literalReq("warpinfo")
                .then(argument("name", StringArgumentType.word())
                        .executes(ctx -> {
                            Warp warp = getWarp(ctx.getArgument("name", String.class));
                            if (warp == null) sendError(ctx, "A warp by that name could not be found.");
                            else {
                                StringBuilder header = new StringBuilder();
                                for (int i = 0; i < 35; i++)
                                    if (i == 17)
                                        header.append(DF).append("WARPINFO FOR ").append(SF).append(warp.getName());
                                    else header.append(i % 16 % 2 == 0 ? SF + "-" : DF + "=");
                                sendMsg(ctx, header.toString());
                                sendMsg(ctx, "Owner: " + SF + (ctx.getSource().getServer().getPlayerList().getPlayer(warp.getOwner()) == null ?
                                        warp.getOwner() : IMoreCommands.get().textToString(Objects.requireNonNull(ctx.getSource().getServer().getPlayerList()
                                        .getPlayer(warp.getOwner())).getDisplayName(), null, true)));
                                sendMsg(ctx, "Created at: " + SF + format.format(warp.getCreationDate()));
                                sendMsg(ctx, "Location: " + SF + "X: " + warp.getPos().x + DF + ", " + SF + "Y: " + warp.getPos().y + DF + ", " + SF + "Z: " + warp.getPos().z);
                                sendMsg(ctx, "Rotation: " + SF + "yaw: " + warp.getYaw() + DF + ", " + SF + "pitch: " + warp.getPitch());
                                sendMsg(ctx, "World: " + SF + warp.getWorld().dimension().location().toString());
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

    @Override
    public Map<String, Boolean> getExtraPermissions() {
        return warps.values().stream()
                .flatMap(List::stream)
                .filter(Warp::isLimited)
                .map(warp -> "morecommands.warp." + warp.getName())
                .collect(Collectors.toMap(s -> s, s -> false));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/warp";
    }

    private int executeList(CommandContext<CommandSourceStack> ctx, int page) throws CommandSyntaxException {
        List<String> warps = getWarpNamesFor(ctx.getSource().getPlayerOrException());
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
        ResourceLocation worldId = new ResourceLocation((String) data.get("world"));
        Warp warp = new Warp(name, owner,
                new Location<>(server.getLevel(server.levelKeys().stream().filter(key -> key.location().equals(worldId)).findFirst().orElse(null)),
                        new Vec3((Double) data.get("x"), (Double) data.get("y"), (Double) data.get("z")),
                        new Vec2(((Double) data.get("yaw")).floatValue(), ((Double) data.get("pitch")).floatValue())),
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
        private final Location<ServerLevel> loc;
        private boolean isLimited;
        private int counter;
        private final Date creationDate;

        public Warp(String name, UUID owner, Location<ServerLevel> loc, boolean isLimited, int counter, Date creationDate) {
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

        public Vec3 getPos() {
            return loc.getPos();
        }

        public float getYaw() {
            return loc.getRot().y;
        }

        public float getPitch() {
            return loc.getRot().x;
        }

        public ServerLevel getWorld() {
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

        public void teleport(ServerPlayer p) {
            teleport(p, true);
        }

        public void teleport(ServerPlayer p, boolean count) {
            if (mayTeleport(p.createCommandSourceStack())) {
                MoreCommands.teleport(p, getWorld(), getPos(), getYaw(), getPitch());
                if (count) {
                    sendMsg(p, ChatFormatting.GREEN + "W" + ChatFormatting.BLUE + "h" + ChatFormatting.YELLOW + "oo" + ChatFormatting.RED + "s" + ChatFormatting.LIGHT_PURPLE + "h" + ChatFormatting.WHITE + "!");
                    counter++;
                    setDirty(true);
                }
            } else sendMsg(p, ChatFormatting.RED + "You must be an operator to teleport to this warp.");
        }

        public boolean isDirty() {
            return dirty.contains(getOwner());
        }

        public void setDirty(boolean b) {
            if (b) dirty.add(getOwner());
            else dirty.remove(getOwner());
        }

        public boolean mayTeleport(CommandSourceStack source) {
            return !isLimited ||hasPermissionOrOp("morecommands.warp." + getName()).test(source);
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
            map.put("world", getWorld().dimension().location().toString());
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
