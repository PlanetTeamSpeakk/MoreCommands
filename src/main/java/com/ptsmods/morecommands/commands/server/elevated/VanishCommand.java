package com.ptsmods.morecommands.commands.server.elevated;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class VanishCommand extends Command {

    public static final Map<ServerPlayerEntity, EntityTrackerEntry> trackers = new HashMap<>();

    private void init() {
        // Gotta tick them manually since they don't get ticked when they're not tracked which breaks stuff like altering attributes (and thus altering reach).
        ServerTickEvents.START_SERVER_TICK.register(server -> trackers.values().forEach(EntityTrackerEntry::tick));
    }

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.getRoot().addChild(MoreCommands.createAlias("v", dispatcher.register(literal("vanish").requires(IS_OP).executes(ctx -> execute(ctx, null)).then(argument("players", EntityArgumentType.players()).executes(ctx -> execute(ctx, EntityArgumentType.getPlayers(ctx, "players")))))));
    }

    @Override
    public boolean forDedicated() {
        return true;
    }

    private int execute(CommandContext<ServerCommandSource> ctx, Collection<ServerPlayerEntity> p) throws CommandSyntaxException {
        if (p == null) p = Lists.newArrayList(ctx.getSource().getPlayer());
        else if (!isOp(ctx)) {
            sendMsg(ctx, Formatting.RED + "You must be op to toggle vanish for others.");
            return 0;
        }
        for (ServerPlayerEntity player : p) {
            boolean b = !player.getDataTracker().get(MoreCommands.VANISH);
            if (b) vanish(player, true);
            else unvanish(player);
            sendMsg(player, "You are " + formatFromBool(b, "now", "no longer") + DF + " vanished.");
        }
        return p.size();
    }

    public static void vanish(ServerPlayerEntity player, boolean sendmsg) {
        player.getDataTracker().set(MoreCommands.VANISH, true);
        player.getDataTracker().set(MoreCommands.VANISH_TOGGLED, true);
        player.getServer().getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.REMOVE_PLAYER, player));
        player.getServerWorld().getChunkManager().unloadEntity(player);
        if (sendmsg && player.getServerWorld().getGameRules().getBoolean(MoreCommands.doJoinMessageRule)) player.getServer().getPlayerManager().broadcastChatMessage(new TranslatableText("multiplayer.player.left", player.getDisplayName()).setStyle(Style.EMPTY.withFormatting(Formatting.YELLOW)), MessageType.SYSTEM, Util.NIL_UUID);
    }

    public static void unvanish(ServerPlayerEntity player) {
        if (player.getDataTracker().get(MoreCommands.VANISH)) {
            player.getDataTracker().set(MoreCommands.VANISH, false);
            player.getServer().getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, player));
            trackers.remove(player);
            player.getServerWorld().getChunkManager().loadEntity(player);
            if (player.getServerWorld().getGameRules().getBoolean(MoreCommands.doJoinMessageRule)) player.getServer().getPlayerManager().broadcastChatMessage(new TranslatableText("multiplayer.player.joined", player.getDisplayName()).setStyle(Style.EMPTY.withFormatting(Formatting.YELLOW)), MessageType.SYSTEM, Util.NIL_UUID);
        }
    }
}
