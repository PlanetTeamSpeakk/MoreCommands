package com.ptsmods.morecommands.miscellaneous;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.api.util.text.TextBuilder;
import com.ptsmods.morecommands.mixin.client.accessor.MixinEntitySelectorAccessor;
import dev.architectury.event.events.client.ClientTickEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public abstract class ClientCommand extends Command {
    private static Screen scheduledScreen = null;
    public static final Logger log = MoreCommandsClient.LOG;

    static {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (client.currentScreen == null && scheduledScreen != null) {
                client.setScreen(scheduledScreen);
                scheduledScreen = null;
            }
        });
    }

    public final void preinit(boolean serverOnly) {
        preinit();
    }

    public void preinit() {}

    public final void init(boolean serverOnly, MinecraftServer server) {}

    @Override
    public final void register(CommandDispatcher<ServerCommandSource> dispatcher) throws Exception {
        throw new IllegalAccessException("Client commands can only be registered via the cRegister method.");
    }

    @Override
    public final void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) throws Exception {
        register(dispatcher);
    }

    @Override
    public final boolean isDedicatedOnly() {
        return false;
    }

    @Override
    public final Map<String, Boolean> getExtraPermissions() {
        return Collections.emptyMap();
    }

    public abstract void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) throws Exception;

    public static LiteralArgumentBuilder<ClientCommandSource> cLiteral(String literal) {
        return LiteralArgumentBuilder.literal(literal);
    }

    public static <T> RequiredArgumentBuilder<ClientCommandSource, T> cArgument(String name, ArgumentType<T> argument) {
        return RequiredArgumentBuilder.argument(name, argument);
    }

    public static void sendMsg(String s, Object... formats) {
        sendMsg(LiteralTextBuilder.builder(fixResets(formatted(s, formats))).withStyle(DS).build());
    }

    public static void sendMsg(Text t) {
        sendMsg(Compat.get().builderFromText(t));
    }

    public static void sendMsg(TextBuilder<?> textBuilder) {
        getPlayer().sendMessage(textBuilder.copy().withStyle(style -> style.isEmpty() ? DS : style).build(), false);
    }

    public static void sendAbMsg(String s, Object... formats) {
        sendAbMsg(LiteralTextBuilder.builder(fixResets(formatted(s, formats))).withStyle(DS).build());
    }

    public static void sendAbMsg(TextBuilder<?> textBuilder) {
        sendAbMsg(textBuilder.build());
    }

    public static void sendAbMsg(Text t) {
        getPlayer().sendMessage(t, true);
    }

    public static void sendError(String error, Object... formats) {
        sendError(LiteralTextBuilder.builder(fixResets(formatted(error, formats), Formatting.RED)).withStyle(Style.EMPTY.withColor(Formatting.RED)).build());
    }

    public static void sendError(Text error) {
        sendError(Compat.get().builderFromText(error));
    }

    public static void sendError(TextBuilder<?> textBuilder) {
        getPlayer().sendMessage(textBuilder.copy().withStyle(style -> Style.EMPTY.withFormatting(Formatting.RED)).build(), false);
    }

    public static ClientPlayerEntity getPlayer() {
        return getClient().player;
    }

    public static ClientWorld getWorld() {
        return getClient().world;
    }

    public static MinecraftClient getClient() {
        return MinecraftClient.getInstance();
    }

    public static ServerCommandSource getServerCommandSource() {
        return new ServerCommandSource(CommandOutput.DUMMY, getPlayer().getPos(), getPlayer().getRotationClient(), null, 0, getPlayer().getEntityName(), getPlayer().getDisplayName(), null, getPlayer());
    }

    public static PlayerListEntry getEntry() {
        for (PlayerListEntry entry : getPlayer().networkHandler.getPlayerList())
            if (entry.getProfile().getId().equals(getPlayer().getUuid())) return entry;
        return null; // Kinda impossible, but you never know.
    }

    public static AbstractClientPlayerEntity getPlayerEntity(PlayerListEntry entry) {
        for (Entity entity : getWorld().getEntities())
            if (entity instanceof AbstractClientPlayerEntity && entity.getUuid().equals(entry.getProfile().getId())) return (AbstractClientPlayerEntity) entity;
        return null;
    }

    public static PlayerListEntry getPlayer(String username) {
        for (PlayerListEntry entry : getPlayer().networkHandler.getPlayerList())
            if (entry.getProfile().getName().equalsIgnoreCase(username)) return entry;
        return null;
    }

    public static PlayerListEntry getPlayer(UUID uuid) {
        for (PlayerListEntry entry : getPlayer().networkHandler.getPlayerList())
            if (entry.getProfile().getId().equals(uuid)) return entry;
        return null;
    }

    public static PlayerListEntry getPlayer(CommandContext<ClientCommandSource> ctx, String argument) throws CommandSyntaxException {
        List<PlayerListEntry> entries = getPlayers(ctx, argument);
        if (entries.size() != 1) throw EntityArgumentType.PLAYER_NOT_FOUND_EXCEPTION.create();
        else return entries.get(0);
    }

    public static List<PlayerListEntry> getPlayers(CommandContext<ClientCommandSource> ctx, String argument) {
        EntitySelector selector = ctx.getArgument(argument, EntitySelector.class);
        MixinEntitySelectorAccessor mselector = ReflectionHelper.cast(selector);
        PlayerListEntry player;
        if (mselector.getPlayerName() != null) {
            player = getPlayer(mselector.getPlayerName());
            return player == null ? Collections.emptyList() : Lists.newArrayList(player);
        } else if (mselector.getUuid() != null) {
            player = getPlayer(mselector.getUuid());
            return player == null ? Collections.emptyList() : Lists.newArrayList(player);
        } else {
            Vec3d pos = mselector.getPositionOffset().apply(getPlayer().getPos());
            Predicate<Entity> predicate = mselector.callGetPositionPredicate(pos);
            if (mselector.getSenderOnly() && predicate.test(getPlayer())) return Lists.newArrayList(getEntry());
            else {
                ClientWorld world = getWorld();
                List<? extends Entity> list = world.getPlayers();
                List<PlayerListEntry> entries = new ArrayList<>();
                if (list.size() > 0)
                    mselector.getSorter().accept(pos, list);
                return entries.subList(0, Math.min(list.size(), selector.getLimit()));
            }
        }
    }

    public static Entity getEntity(CommandContext<ClientCommandSource> ctx, String name) throws CommandSyntaxException {
        Collection<? extends Entity> entities = getEntities(ctx, name);
        if (entities.isEmpty()) throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();
        else if (entities.size() > 1) throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.create();
        else return entities.iterator().next();
    }

    public static Collection<? extends Entity> getEntities(CommandContext<ClientCommandSource> ctx, String name) throws CommandSyntaxException {
        EntitySelector selector = ctx.getArgument(name, EntitySelector.class);
        MixinEntitySelectorAccessor mselector = ReflectionHelper.cast(selector);
        if (!mselector.getIncludesNonPlayers()) {
            return getPlayers(ctx, name).stream().collect(Collector.<PlayerListEntry, List<AbstractClientPlayerEntity>>of(ArrayList::new, (list, entry) -> list.add(getPlayerEntity(entry)), (list1, list2) -> Stream.concat(list1.stream(), list2.stream()).collect(Collectors.toList())));
        } else if (mselector.getPlayerName() != null) {
            AbstractClientPlayerEntity player = getPlayerEntity(getPlayer(mselector.getPlayerName()));
            return (player == null ? Collections.emptyList() : Lists.newArrayList(player));
        } else if (mselector.getUuid() != null) {
            for (Entity entity : getWorld().getEntities())
                if (entity.getUuid().equals(mselector.getUuid()))
                    return Lists.newArrayList(entity);
            return Collections.emptyList();
        } else {
            Vec3d vec3d = mselector.getPositionOffset().apply(getPlayer().getPos());
            Predicate<Entity> predicate = getPositionPredicate(mselector, vec3d);
            if (mselector.getSenderOnly()) {
                return predicate.test(getPlayer()) ? Lists.newArrayList(getPlayer()) : Collections.emptyList();
            } else {
                List<Entity> list = Lists.newArrayList();
                appendEntitiesFromWorld(mselector, list, getWorld(), vec3d, predicate);
                return getEntities(selector, mselector, vec3d, list);
            }
        }
    }

    private static Predicate<Entity> getPositionPredicate(MixinEntitySelectorAccessor mselector, Vec3d vec3d) {
        Predicate<Entity> predicate = mselector.getBasePredicate();
        if (mselector.getBox() != null) predicate = predicate.and((entity) -> mselector.getBox().offset(vec3d).intersects(entity.getBoundingBox()));
        if (!mselector.getDistance().isDummy()) predicate = predicate.and((entity) -> mselector.getDistance().testSqrt(entity.squaredDistanceTo(vec3d)));
        return predicate;
    }

    private static void appendEntitiesFromWorld(MixinEntitySelectorAccessor mselector, List<Entity> list, ClientWorld world, Vec3d vec3d, Predicate<Entity> predicate) {
        List<Entity> entities = Lists.newArrayList(world.getEntities());
        Box box = mselector.getBox() == null ? new Box(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE) : mselector.getBox().offset(vec3d);
        entities.removeIf(entity -> !box.contains(entity.getPos()) || !predicate.test(entity));
        list.addAll(entities);
    }

    private static <T extends Entity> List<T> getEntities(EntitySelector selector, MixinEntitySelectorAccessor mselector, Vec3d vec3d, List<T> list) {
        if (list.size() > 1) mselector.getSorter().accept(vec3d, list);
        return list.subList(0, Math.min(selector.getLimit(), list.size()));
    }

    protected static void scheduleScreen(Screen screen) {
        scheduledScreen = screen;
    }

    public static BlockPos getLoadedBlockPos(CommandContext<ClientCommandSource> context, String name) throws CommandSyntaxException {
        BlockPos blockPos = context.getArgument(name, PosArgument.class).toAbsoluteBlockPos(getServerCommandSource());
        if (!getWorld().isChunkLoaded(blockPos))
            throw BlockPosArgumentType.UNLOADED_EXCEPTION.create();
        else if (!Compat.get().isInBuildLimit(MinecraftClient.getInstance().world, blockPos))
            throw BlockPosArgumentType.OUT_OF_WORLD_EXCEPTION.create();
        else return blockPos;
    }
}
