package com.ptsmods.morecommands.miscellaneous;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsArch;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.arguments.CompatArgumentType;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.api.util.text.EmptyTextBuilder;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.api.util.text.TextBuilder;
import com.ptsmods.morecommands.api.util.text.TranslatableTextBuilder;
import com.ptsmods.mysqlw.Database;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class Command {
    public static Formatting DF = MoreCommands.DF;
    public static Formatting SF = MoreCommands.SF;
    public static Style DS = MoreCommands.DS;
    public static Style SS = MoreCommands.SS;
    public static final Logger log = MoreCommands.LOG;
    public static final Predicate<ServerCommandSource> IS_OP = source -> source.hasPermissionLevel(source.getServer().getOpPermissionLevel());
    private static final Map<Class<?>, Command> activeInstances = new HashMap<>();
    private static final List<Pair<Consumer<MinecraftServer>, AtomicInteger>> scheduledTasks = new ArrayList<>();

    static {
        List<Pair<Consumer<MinecraftServer>, AtomicInteger>> finishedTasks = new ArrayList<>();
        TickEvent.SERVER_POST.register(server -> {
            scheduledTasks.forEach(pair -> {
                if (pair.getRight().getAndDecrement() > 0) return;

                pair.getLeft().accept(server);
                finishedTasks.add(pair);
            });

            scheduledTasks.removeAll(finishedTasks);
            finishedTasks.clear();
        });
    }

    /**
     * Gets called once when the command is initialised.
     * Mostly useless now as the no-arg constructor achieves the same thing.
     * @param serverOnly Whether server-only mode is enabled.
     * @throws Exception Can be anything
     */
    public void preinit(boolean serverOnly) throws Exception {
        preinit();
    }

    /**
     * Gets called once when the command is initialised.
     * Mostly useless now as the no-arg constructor achieves the same thing.
     * @throws Exception Can be anything
     */
    public void preinit() throws Exception {}

    /**
     * Gets called every time a new server is created.
     * So also whenever the player joins a singleplayer world.
     * @param serverOnly Whether server-only mode is enabled.
     * @param server The server that was created
     * @throws Exception Can be anything
     */
    public void init(boolean serverOnly, MinecraftServer server) throws Exception {}

    /**
     * Simple version of {@link #register(CommandDispatcher, boolean)}, called without dedicated param.
     * @param dispatcher The dispatcher to register on.
     * @throws Exception Can throw any exception.
     */
    public abstract void register(CommandDispatcher<ServerCommandSource> dispatcher) throws Exception;

    public void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) throws Exception {
        register(dispatcher);
    }

    public boolean isDedicatedOnly() {
        return false;
    }

    public Map<String, Boolean> getExtraPermissions() {
        return Collections.emptyMap();
    }

    public abstract String getDocsPath();

    public boolean doLateInit() {
        return false;
    }

    public Collection<String> getRegisteredNodes() {
        return null; // Default implementation just checks what nodes appear after registering this command.
    }

    // UTILITY METHODS

    public static Database getLocalDb() {
        return MoreCommands.getLocalDb();
    }

    public static Database getGlobalDb() {
        return MoreCommands.getGlobalDb();
    }

    public static int sendMsg(CommandContext<ServerCommandSource> ctx, String msg, Object... formats) {
        return sendMsg(ctx, LiteralTextBuilder.literal(fixResets(formats.length == 0 ? msg : formatted(msg, formats)), DS));
    }

    public static int sendMsg(CommandContext<ServerCommandSource> ctx, Text msg) {
        return sendMsg(ctx, Compat.get().builderFromText(msg));
    }

    public static int sendMsg(CommandContext<ServerCommandSource> ctx, TextBuilder<?> textBuilder) {
        ctx.getSource().sendFeedback(textBuilder.copy().withStyle(style -> style.isEmpty() ? DS : style).build(), true);
        return 1;
    }

    public static int sendError(CommandContext<ServerCommandSource> ctx, String msg, Object... formats) {
        return sendError(ctx, LiteralTextBuilder.literal(fixResets(formatted(msg, formats), Formatting.RED)));
    }

    public static int sendError(CommandContext<ServerCommandSource> ctx, TextBuilder<?> textBuilder) {
        return sendError(ctx, textBuilder.build());
    }

    public static int sendError(CommandContext<ServerCommandSource> ctx, Text msg) {
        ctx.getSource().sendError(msg);
        return 0;
    }

    public static int sendMsg(Entity entity, String msg, Object... formats) {
        return sendMsg(entity, LiteralTextBuilder.literal(formatted(msg, formats), DS));
    }

    public static int sendMsg(Entity entity, Text msg) {
        return sendMsg(entity, Compat.get().builderFromText(msg));
    }

    public static int sendMsg(Entity entity, TextBuilder<?> textBuilder) {
        TextBuilder<?> copy = textBuilder.copy().withStyle(style -> style.isEmpty() ? DS : style);

        entity.getCommandSource().sendFeedback(copy.build(), false);
        return 1;
    }

    public static void broadcast(MinecraftServer server, String msg, Object... formats) {
        broadcast(server, LiteralTextBuilder.literal(formatted(msg, formats), DS));
    }

    public static void broadcast(MinecraftServer server, Text msg) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList())
            sendMsg(player, msg);
    }

    static String fixResets(String s) {
        return fixResets(s, DF);
    }

    static String fixResets(String s, Formatting formatting) {
        return s.replace(Formatting.RESET.toString(), Formatting.RESET.toString() + formatting).replaceAll("\n", "\n" + formatting);
    }

    public static LiteralArgumentBuilder<ServerCommandSource> literal(String literal) {
        return CommandManager.literal(literal);
    }

    public static LiteralArgumentBuilder<ServerCommandSource> literalReqOp(String literal) {
        MoreCommands.registerPermission("morecommands." + literal, false);
        return literal(literal).requires(hasPermissionOrOp("morecommands." + literal));
    }

    public static LiteralArgumentBuilder<ServerCommandSource> literalReq(String literal) {
        MoreCommands.registerPermission("morecommands." + literal, true);
        return literal(literal).requires(hasPermission("morecommands." + literal));
    }

    public static <T> RequiredArgumentBuilder<ServerCommandSource, T> argument(String name, ArgumentType<T> type) {
        RequiredArgumentBuilder<ServerCommandSource, T> builder = CommandManager.argument(name, type instanceof CompatArgumentType<?, ?, ?> && IMoreCommands.get().isServerOnly() ?
                ((CompatArgumentType<?, T, ?>) type).toVanillaArgumentType() : type);

        if (IMoreCommands.get().isServerOnly()) builder.suggests(type::listSuggestions);
        return builder;
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

    public static String formatFromFloat(float v, float max, float yellow, float green, boolean colourOnly) {
        float percent = v/max;
        return "" + (percent >= green ? Formatting.GREEN : percent >= yellow ? Formatting.YELLOW : Formatting.RED) +
                (colourOnly ? "" : new DecimalFormat("#.##").format(v) + DF + "/" + Formatting.GREEN + max);
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
                cmd.init(false, server);
            } catch (Exception e) {
                log.error("Error invoking initialisation method on class " + cmd.getClass().getName() + ".", e);
            }
    }

    protected static Predicate<ServerCommandSource> hasPermission(@NotNull String permission, int defaultRequiredLevel) {
        return isPermissionsLoaded() ? MoreCommandsArch.requirePermission(permission, defaultRequiredLevel) : source -> source.hasPermissionLevel(defaultRequiredLevel);
    }

    protected static Predicate<ServerCommandSource> hasPermissionOrOp(@NotNull String permission) {
        return hasPermission(permission, 2);
    }

    protected static Predicate<ServerCommandSource> hasPermission(@NotNull String permission) {
        return hasPermission(permission, 0);
    }

    public static boolean isPermissionsLoaded() {
        return MoreCommandsArch.isFabricModLoaded("fabric-permissions-api-v0");
    }

    protected int getCountFromPerms(ServerCommandSource source, String prefix, int max) {
        final int finalMax = max;
        if (isPermissionsLoaded())
            for (int i = 0; i < 100; i++)
                if (MoreCommandsArch.checkPermission(source, prefix + i, i <= finalMax))
                    max = i;
        return max;
    }

    protected static String formatted(String s, Object... formats) {
        return formats == null || formats.length == 0 ? s : String.format(s, formats);
    }

    protected static String coloured(Object o) {
        return coloured(o, SF);
    }

    protected static String coloured(Object o, Formatting colour) {
        return "" + colour + o + DF;
    }

    protected static EmptyTextBuilder emptyText() {
        return EmptyTextBuilder.builder();
    }

    protected static EmptyTextBuilder emptyText(Style style) {
        return EmptyTextBuilder.builder(style);
    }

    protected static LiteralTextBuilder literalText(String text) {
        return literalText(text, Style.EMPTY);
    }

    protected static LiteralTextBuilder literalText(String text, Style style) {
        return LiteralTextBuilder.builder(text, style);
    }

    protected static TranslatableTextBuilder translatableText(String text, Object... args) {
        return TranslatableTextBuilder.builder(text, args);
    }

    public static void scheduleTask(Runnable task) {
        scheduleTask(task, 0);
    }

    public static void scheduleTask(Runnable task, int delay) {
        scheduleTask(s -> task.run(), delay);
    }

    public static void scheduleTask(Consumer<MinecraftServer> task) {
        scheduleTask(task, 0);
    }

    public static void scheduleTask(Consumer<MinecraftServer> task, int delay) {
        if (delay < 0) throw new IllegalArgumentException("Delay must be at least 0.");
        AtomicInteger atomicDelay = new AtomicInteger(delay);
        scheduledTasks.add(new Pair<>(task, atomicDelay));
    }
}
