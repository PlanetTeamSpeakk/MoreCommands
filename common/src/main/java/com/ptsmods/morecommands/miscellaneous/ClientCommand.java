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
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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
            if (client.screen == null && scheduledScreen != null) {
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
    public final void register(CommandDispatcher<CommandSourceStack> dispatcher) throws Exception {
        throw new IllegalAccessException("Client commands can only be registered via the cRegister method.");
    }

    @Override
    public final void register(CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicated) throws Exception {
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

    public abstract void cRegister(CommandDispatcher<ClientSuggestionProvider> dispatcher) throws Exception;

    public static LiteralArgumentBuilder<ClientSuggestionProvider> cLiteral(String literal) {
        return LiteralArgumentBuilder.literal(literal);
    }

    public static <T> RequiredArgumentBuilder<ClientSuggestionProvider, T> cArgument(String name, ArgumentType<T> argument) {
        return RequiredArgumentBuilder.argument(name, argument);
    }

    public static int sendMsg(String s, Object... formats) {
        return sendMsg(LiteralTextBuilder.literal(fixResets(formatted(s, formats)), DS));
    }

    public static int sendMsg(Component t) {
        return sendMsg(Compat.get().builderFromText(t));
    }

    public static int sendMsg(TextBuilder<?> textBuilder) {
        getPlayer().displayClientMessage(textBuilder.copy().withStyle(style -> style.isEmpty() ? DS : style).build(), false);
        return 1;
    }

    public static void sendAbMsg(String s, Object... formats) {
        sendAbMsg(LiteralTextBuilder.literal(fixResets(formatted(s, formats)), DS));
    }

    public static void sendAbMsg(TextBuilder<?> textBuilder) {
        sendAbMsg(textBuilder.build());
    }

    public static void sendAbMsg(Component t) {
        getPlayer().displayClientMessage(t, true);
    }

    public static int sendError(String error, Object... formats) {
        return sendError(LiteralTextBuilder.literal(fixResets(formatted(error, formats), ChatFormatting.RED), Style.EMPTY.withColor(ChatFormatting.RED)));
    }

    public static int sendError(Component error) {
        return sendError(Compat.get().builderFromText(error));
    }

    public static int sendError(TextBuilder<?> textBuilder) {
        getPlayer().displayClientMessage(textBuilder.copy().withStyle(style -> Style.EMPTY.applyFormat(ChatFormatting.RED)).build(), false);
        return 0;
    }

    public static LocalPlayer getPlayer() {
        return getClient().player;
    }

    public static ClientLevel getWorld() {
        return getClient().level;
    }

    public static Minecraft getClient() {
        return Minecraft.getInstance();
    }

    public static CommandSourceStack getServerCommandSource() {
        return new CommandSourceStack(CommandSource.NULL, getPlayer().position(), getPlayer().getRotationVector(), null, 0, getPlayer().getScoreboardName(), getPlayer().getDisplayName(), null, getPlayer());
    }

    public static PlayerInfo getEntry() {
        for (PlayerInfo entry : getPlayer().connection.getOnlinePlayers())
            if (entry.getProfile().getId().equals(Compat.get().getUUID(getPlayer()))) return entry;
        return null; // Kinda impossible, but you never know.
    }

    public static AbstractClientPlayer getPlayerEntity(PlayerInfo entry) {
        for (Entity entity : getWorld().entitiesForRendering())
            if (entity instanceof AbstractClientPlayer && Compat.get().getUUID(entity).equals(entry.getProfile().getId()))
                return (AbstractClientPlayer) entity;
        return null;
    }

    public static PlayerInfo getPlayer(String username) {
        for (PlayerInfo entry : getPlayer().connection.getOnlinePlayers())
            if (entry.getProfile().getName().equalsIgnoreCase(username)) return entry;
        return null;
    }

    public static PlayerInfo getPlayer(UUID uuid) {
        for (PlayerInfo entry : getPlayer().connection.getOnlinePlayers())
            if (entry.getProfile().getId().equals(uuid)) return entry;
        return null;
    }

    public static PlayerInfo getPlayer(CommandContext<ClientSuggestionProvider> ctx, String argument) throws CommandSyntaxException {
        List<PlayerInfo> entries = getPlayers(ctx, argument);
        if (entries.size() != 1) throw EntityArgument.NO_PLAYERS_FOUND.create();
        else return entries.get(0);
    }

    public static List<PlayerInfo> getPlayers(CommandContext<ClientSuggestionProvider> ctx, String argument) {
        EntitySelector selector = ctx.getArgument(argument, EntitySelector.class);
        MixinEntitySelectorAccessor mselector = ReflectionHelper.cast(selector);
        PlayerInfo player;
        if (mselector.getPlayerName() != null) {
            player = getPlayer(mselector.getPlayerName());
            return player == null ? Collections.emptyList() : Lists.newArrayList(player);
        } else if (mselector.getEntityUUID() != null) {
            player = getPlayer(mselector.getEntityUUID());
            return player == null ? Collections.emptyList() : Lists.newArrayList(player);
        } else {
            Vec3 pos = mselector.getPosition().apply(getPlayer().position());
            Predicate<Entity> predicate = mselector.callGetPredicate(pos);
            if (mselector.getCurrentEntity() && predicate.test(getPlayer())) return Lists.newArrayList(getEntry());
            else {
                ClientLevel world = getWorld();
                List<? extends Entity> list = world.players();
                List<PlayerInfo> entries = new ArrayList<>();
                if (list.size() > 0)
                    mselector.getOrder().accept(pos, list);
                return entries.subList(0, Math.min(list.size(), selector.getMaxResults()));
            }
        }
    }

    public static Entity getEntity(CommandContext<ClientSuggestionProvider> ctx, String name) throws CommandSyntaxException {
        Collection<? extends Entity> entities = getEntities(ctx, name);
        if (entities.isEmpty()) throw EntityArgument.NO_ENTITIES_FOUND.create();
        else if (entities.size() > 1) throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
        else return entities.iterator().next();
    }

    public static Collection<? extends Entity> getEntities(CommandContext<ClientSuggestionProvider> ctx, String name) throws CommandSyntaxException {
        EntitySelector selector = ctx.getArgument(name, EntitySelector.class);
        MixinEntitySelectorAccessor mselector = ReflectionHelper.cast(selector);
        if (!mselector.getIncludesEntities()) {
            return getPlayers(ctx, name).stream()
                    .collect(Collector.<PlayerInfo, List<AbstractClientPlayer>>
                            of(ArrayList::new, (list, entry) -> list.add(getPlayerEntity(entry)), (list1, list2) ->
                            Stream.concat(list1.stream(), list2.stream()).collect(Collectors.toList())));
        } else if (mselector.getPlayerName() != null) {
            AbstractClientPlayer player = getPlayerEntity(getPlayer(mselector.getPlayerName()));
            return (player == null ? Collections.emptyList() : Lists.newArrayList(player));
        } else if (mselector.getEntityUUID() != null) {
            for (Entity entity : getWorld().entitiesForRendering())
                if (Compat.get().getUUID(entity).equals(mselector.getEntityUUID()))
                    return Lists.newArrayList(entity);
            return Collections.emptyList();
        } else {
            Vec3 vec3d = mselector.getPosition().apply(getPlayer().position());
            Predicate<Entity> predicate = getPositionPredicate(mselector, vec3d);
            if (mselector.getCurrentEntity()) {
                return predicate.test(getPlayer()) ? Lists.newArrayList(getPlayer()) : Collections.emptyList();
            } else {
                List<Entity> list = Lists.newArrayList();
                appendEntitiesFromWorld(mselector, list, getWorld(), vec3d, predicate);
                return getEntities(selector, mselector, vec3d, list);
            }
        }
    }

    private static Predicate<Entity> getPositionPredicate(MixinEntitySelectorAccessor mselector, Vec3 vec3d) {
        Predicate<Entity> predicate = mselector.getPredicate();
        if (mselector.getAabb() != null) predicate = predicate.and(entity -> mselector.getAabb().move(vec3d).intersects(Compat.get().getBoundingBox(entity)));
        if (!mselector.getRange().isAny()) predicate = predicate.and(entity -> mselector.getRange().matchesSqr(entity.distanceToSqr(vec3d)));
        return predicate;
    }

    private static void appendEntitiesFromWorld(MixinEntitySelectorAccessor mselector, List<Entity> list, ClientLevel world, Vec3 vec3d, Predicate<Entity> predicate) {
        List<Entity> entities = Lists.newArrayList(world.entitiesForRendering());
        AABB box = mselector.getAabb() == null ? new AABB(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE) : mselector.getAabb().move(vec3d);
        entities.removeIf(entity -> !box.contains(entity.position()) || !predicate.test(entity));
        list.addAll(entities);
    }

    private static <T extends Entity> List<T> getEntities(EntitySelector selector, MixinEntitySelectorAccessor mselector, Vec3 vec3d, List<T> list) {
        if (list.size() > 1) mselector.getOrder().accept(vec3d, list);
        return list.subList(0, Math.min(selector.getMaxResults(), list.size()));
    }

    protected static void scheduleScreen(Screen screen) {
        scheduledScreen = screen;
    }

    public static BlockPos getLoadedBlockPos(CommandContext<ClientSuggestionProvider> context, String name) throws CommandSyntaxException {
        BlockPos blockPos = context.getArgument(name, Coordinates.class).getBlockPos(getServerCommandSource());
        if (!getWorld().hasChunkAt(blockPos))
            throw BlockPosArgument.ERROR_NOT_LOADED.create();
        else if (!Objects.requireNonNull(Minecraft.getInstance().level).isInWorldBounds(blockPos))
            throw BlockPosArgument.ERROR_OUT_OF_WORLD.create();
        else return blockPos;
    }
}
