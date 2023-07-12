package com.ptsmods.morecommands.miscellaneous;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.MoreCommandsArch;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.api.util.text.EmptyTextBuilder;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.api.util.text.TextBuilder;
import com.ptsmods.morecommands.api.util.text.TranslatableTextBuilder;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.MappedRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class Command {
    public static ChatFormatting DF = MoreCommands.DF;
    public static ChatFormatting SF = MoreCommands.SF;
    public static Style DS = MoreCommands.DS;
    public static Style SS = MoreCommands.SS;
    public static final Logger log = MoreCommands.LOG;
    public static final Predicate<CommandSourceStack> IS_OP = source -> source.hasPermission(source.getServer().getOperatorUserPermissionLevel());
    private static final Map<Class<?>, Command> activeInstances = new HashMap<>();
    private static final List<Tuple<Consumer<MinecraftServer>, AtomicInteger>> scheduledTasks = new ArrayList<>();

    static {
        List<Tuple<Consumer<MinecraftServer>, AtomicInteger>> finishedTasks = new ArrayList<>();
        TickEvent.SERVER_POST.register(server -> {
            scheduledTasks.forEach(pair -> {
                if (pair.getB().getAndDecrement() > 0) return;

                pair.getA().accept(server);
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
    public abstract void register(CommandDispatcher<CommandSourceStack> dispatcher) throws Exception;

    public void register(CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicated) throws Exception {
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

    public @Nullable Set<String> nodeNames() {
        return null;
    }

    public boolean registerInServerOnly() {
        return true;
    }

    public Collection<String> getRegisteredNodes() {
        return null; // Default implementation just checks what nodes appear after registering this command.
    }

    // UTILITY METHODS

    public static int sendMsg(CommandContext<CommandSourceStack> ctx, String msg, Object... formats) {
        return sendMsg(ctx, LiteralTextBuilder.literal(fixResets(formats.length == 0 ? msg : formatted(msg, formats)), DS));
    }

    public static int sendMsg(CommandContext<CommandSourceStack> ctx, Component msg) {
        return sendMsg(ctx, Compat.get().builderFromText(msg));
    }

    public static int sendMsg(CommandContext<CommandSourceStack> ctx, TextBuilder<?> textBuilder) {
        ctx.getSource().sendSuccess(textBuilder.copy().withStyle(style -> style.isEmpty() ? DS : style).build(), true);
        return 1;
    }

    public static int sendError(CommandContext<CommandSourceStack> ctx, String msg, Object... formats) {
        return sendError(ctx, LiteralTextBuilder.literal(fixResets(formatted(msg, formats), ChatFormatting.RED)));
    }

    public static int sendError(CommandContext<CommandSourceStack> ctx, TextBuilder<?> textBuilder) {
        return sendError(ctx, textBuilder.build());
    }

    public static int sendError(CommandContext<CommandSourceStack> ctx, Component msg) {
        ctx.getSource().sendFailure(msg);
        return 0;
    }

    public static int sendMsg(Entity entity, String msg, Object... formats) {
        return sendMsg(entity, LiteralTextBuilder.literal(formatted(msg, formats), DS));
    }

    public static int sendMsg(Entity entity, Component msg) {
        entity.sendSystemMessage(msg);
        return 1;
    }

    public static int sendMsg(Entity entity, TextBuilder<?> textBuilder) {
        TextBuilder<?> copy = textBuilder.copy().withStyle(style -> style.isEmpty() ? DS : style);

        entity.sendSystemMessage(copy.build());
        return 1;
    }

    public static void broadcast(MinecraftServer server, String msg, Object... formats) {
        broadcast(server, LiteralTextBuilder.literal(formatted(msg, formats), DS));
    }

    public static void broadcast(MinecraftServer server, Component msg) {
        for (ServerPlayer player : server.getPlayerList().getPlayers())
            sendMsg(player, msg);
    }

    protected static String fixResets(String s) {
        return fixResets(s, DF);
    }

    protected static String fixResets(String s, ChatFormatting formatting) {
        return s.replace(ChatFormatting.RESET.toString(), ChatFormatting.RESET.toString() + formatting).replaceAll("\n", "\n" + formatting);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> literal(String literal) {
        return Commands.literal(literal);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> literalReqOp(String literal) {
        IMoreCommands.get().registerPermission("morecommands." + literal, false);
        return literal(literal).requires(hasPermissionOrOp("morecommands." + literal));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> literalReq(String literal) {
        IMoreCommands.get().registerPermission("morecommands." + literal, true);
        return literal(literal).requires(hasPermission("morecommands." + literal));
    }

    public static <T> RequiredArgumentBuilder<CommandSourceStack, T> argument(String name, ArgumentType<T> type) {
        return Commands.argument(name, type);
    }

    public static String joinNicely(Collection<String> strings) {
        return joinNicely(strings, SF, DF);
    }

    public static String joinNicely(Collection<String> strings, ChatFormatting colour, ChatFormatting commaColour) {
        List<String> l = new ArrayList<>(strings);
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < l.size(); i++)
            s.append(colour == null ? "" : colour).append(l.get(i)).append(commaColour == null ? "" : commaColour).append(i == l.size()-2 ? " and" : i == l.size()-1 ? "" : ",").append(i == l.size()-1 ? "" : " ");
        return s.toString();
    }

    public static String formatFromFloat(float v, float max, float yellow, float green, boolean colourOnly) {
        float percent = v/max;
        return "" + (percent >= green ? ChatFormatting.GREEN : percent >= yellow ? ChatFormatting.YELLOW : ChatFormatting.RED) +
                (colourOnly ? "" : new DecimalFormat("#.##").format(v) + DF + "/" + ChatFormatting.GREEN + max);
    }

    public static boolean isOp(CommandContext<CommandSourceStack> ctx) {
        return IS_OP.test(ctx.getSource());
    }

    public static boolean isOp(ServerPlayer player) {
        return player.hasPermissions(Objects.requireNonNull(player.getServer()).getOperatorUserPermissionLevel());
    }

    public void setActiveInstance() {
        activeInstances.put(getClass(), this);
    }

    public static UUID getServerUuid(MinecraftServer server) {
        return UUID.nameUUIDFromBytes(server.createCommandSourceStack().getTextName().getBytes(StandardCharsets.UTF_8));
    }

    public static void doInitialisations(MinecraftServer server) {
        for (Command cmd : activeInstances.values())
            try {
                cmd.init(false, server);
            } catch (Exception e) {
                log.error("Error invoking initialisation method on class " + cmd.getClass().getName() + ".", e);
            }
    }

    protected static Predicate<CommandSourceStack> hasPermission(@NotNull String permission, int defaultRequiredLevel) {
        return isPermissionsLoaded() ? MoreCommandsArch.requirePermission(permission, defaultRequiredLevel) : source -> source.hasPermission(defaultRequiredLevel);
    }

    protected static Predicate<CommandSourceStack> hasPermissionOrOp(@NotNull String permission) {
        return hasPermission(permission, 2);
    }

    protected static Predicate<CommandSourceStack> hasPermission(@NotNull String permission) {
        return hasPermission(permission, 0);
    }

    public static boolean isPermissionsLoaded() {
        return MoreCommandsArch.isFabricModLoaded("fabric-permissions-api-v0");
    }

    protected int getCountFromPerms(CommandSourceStack source, String prefix, int max) {
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

    protected static String coloured(Object o, ChatFormatting colour) {
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
        scheduledTasks.add(new Tuple<>(task, atomicDelay));
    }

    public <S> RequiredArgumentBuilder<S, ?> newResourceArgument(String argName, String registryName) {
        MappedRegistry<?> r = Compat.get().getBuiltInRegistry(registryName);
        return RequiredArgumentBuilder.<S, ResourceLocation>argument(argName, ResourceLocationArgument.id())
                .suggests((ctx, builder) -> SharedSuggestionProvider.suggestResource(r.keySet(), builder));
    }

    public <T> T getResource(CommandContext<?> ctx, String argName, String registryName) throws CommandSyntaxException {
        MappedRegistry<T> registry = Compat.get().getBuiltInRegistry(registryName);
        ResourceLocation location = ctx.getArgument(argName, ResourceLocation.class);
        if (!registry.containsKey(location)) throw new SimpleCommandExceptionType(LiteralTextBuilder.literal(
                "The given resource could not be found.")).create();

        return registry.get(location);
    }
    
    protected static int runSuccess(Runnable run) {
        run.run();
        return 1;
    }
    
    protected static int runError(Runnable run) {
        run.run();
        return 0;
    }
}
