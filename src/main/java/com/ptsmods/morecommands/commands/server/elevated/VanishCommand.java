package com.ptsmods.morecommands.commands.server.elevated;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import com.ptsmods.morecommands.util.DataTrackerHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.network.MessageType;
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
import java.util.Objects;

public class VanishCommand extends Command {
	public static final Map<ServerPlayerEntity, EntityTrackerEntry> trackers = new HashMap<>();

	public void preinit(boolean serverOnly) {
		// Gotta tick them manually since they don't get ticked when they're not tracked which breaks stuff like altering attributes (and thus altering reach).
		registerCallback(ServerTickEvents.START_SERVER_TICK, server -> trackers.values().forEach(EntityTrackerEntry::tick));
	}

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.getRoot().addChild(MoreCommands.createAlias("v", dispatcher.register(literalReqOp("vanish").executes(ctx -> execute(ctx, null)).then(argument("players", EntityArgumentType.players()).executes(ctx -> execute(ctx, EntityArgumentType.getPlayers(ctx, "players")))))));
	}

	private int execute(CommandContext<ServerCommandSource> ctx, Collection<ServerPlayerEntity> p) throws CommandSyntaxException {
		if (p == null) p = Lists.newArrayList(ctx.getSource().getPlayer());
		else if (!isOp(ctx)) {
			sendError(ctx, "You must be op to toggle vanish for others.");
			return 0;
		}
		for (ServerPlayerEntity player : p) {
			boolean b = !player.getDataTracker().get(DataTrackerHelper.VANISH);
			if (b) vanish(player, true);
			else unvanish(player);
			sendMsg(player, "You are " + formatFromBool(b, "now", "no longer") + DF + " vanished.");
		}
		return p.size();
	}

	public static void vanish(ServerPlayerEntity player, boolean sendmsg) {
		player.getDataTracker().set(DataTrackerHelper.VANISH, true);
		player.getDataTracker().set(DataTrackerHelper.VANISH_TOGGLED, true);
		Objects.requireNonNull(player.getServer()).getPlayerManager().sendToAll(Compat.getCompat().newPlayerListS2CPacket(4, player)); // REMOVE_PLAYER
		player.getWorld().getChunkManager().unloadEntity(player);
		if (sendmsg && MoreGameRules.checkBooleanWithPerm(player.getWorld().getGameRules(), MoreGameRules.doJoinMessageRule, player)) player.getServer().getPlayerManager().broadcast(new TranslatableText("multiplayer.player.left", player.getDisplayName()).setStyle(Style.EMPTY.withFormatting(Formatting.YELLOW)), MessageType.SYSTEM, Util.NIL_UUID);
	}

	public static void unvanish(ServerPlayerEntity player) {
		if (player.getDataTracker().get(DataTrackerHelper.VANISH)) {
			player.getDataTracker().set(DataTrackerHelper.VANISH, false);
			Objects.requireNonNull(player.getServer()).getPlayerManager().sendToAll(Compat.getCompat().newPlayerListS2CPacket(0, player)); // ADD_PLAYER
			trackers.remove(player);
			player.getWorld().getChunkManager().loadEntity(player);
			if (MoreGameRules.checkBooleanWithPerm(player.getWorld().getGameRules(), MoreGameRules.doJoinMessageRule, player)) player.getServer().getPlayerManager().broadcast(new TranslatableText("multiplayer.player.joined", player.getDisplayName()).setStyle(Style.EMPTY.withFormatting(Formatting.YELLOW)), MessageType.SYSTEM, Util.NIL_UUID);
		}
	}
}
