package com.ptsmods.morecommands.commands.server.elevated;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.IDataTrackerHelper;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class VanishCommand extends Command {
    public static final Map<ServerPlayerEntity, EntityTrackerEntry> trackers = new HashMap<>();

    public void preinit(boolean serverOnly) {
        // Gotta tick them manually since they don't get ticked when they're not tracked which breaks stuff like altering attributes (and thus altering reach).
        TickEvent.SERVER_PRE.register(server -> trackers.values().forEach(EntityTrackerEntry::tick));
    }

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.getRoot().addChild(MoreCommands.createAlias("v", dispatcher.register(literalReqOp("vanish")
                .executes(ctx -> execute(ctx, null))
                .then(argument("players", EntityArgumentType.players())
                        .requires(hasPermissionOrOp("morecommands.vanish.others"))
                        .executes(ctx -> execute(ctx, EntityArgumentType.getPlayers(ctx, "players")))))));
    }

    @Override
    public String getDocsPath() {
        return "/server/elevated/vanish";
    }

    private int execute(CommandContext<ServerCommandSource> ctx, Collection<ServerPlayerEntity> p) throws CommandSyntaxException {
        if (p == null) p = Lists.newArrayList(ctx.getSource().getPlayerOrThrow());
        else if (!isOp(ctx)) {
            sendError(ctx, "You must be op to toggle vanish for others.");
            return 0;
        }
        for (ServerPlayerEntity player : p) {
            boolean b = !player.getDataTracker().get(IDataTrackerHelper.get().vanish());
            if (b) vanish(player, true);
            else unvanish(player);
            sendMsg(player, "You are " + Util.formatFromBool(b, "now", "no longer") + DF + " vanished.");
        }
        return p.size();
    }

    public static void vanish(ServerPlayerEntity player, boolean sendmsg) {
        player.getDataTracker().set(IDataTrackerHelper.get().vanish(), true);
        player.getDataTracker().set(IDataTrackerHelper.get().vanishToggled(), true);
        Objects.requireNonNull(player.getServer()).getPlayerManager().sendToAll(Compat.get().newPlayerListS2CPacket(4, player)); // REMOVE_PLAYER
        player.getWorld().getChunkManager().unloadEntity(player);
        if (sendmsg && MoreGameRules.get().checkBooleanWithPerm(player.getWorld().getGameRules(), MoreGameRules.get().doJoinMessageRule(), player))
            Compat.get().broadcast(player.getServer().getPlayerManager(), new Pair<>(1, new Identifier("system")), translatableText("multiplayer.player.left", player.getDisplayName())
                    .withStyle(Style.EMPTY.withFormatting(Formatting.YELLOW)).build());
    }

    public static void unvanish(ServerPlayerEntity player) {
        if (player.getDataTracker().get(IDataTrackerHelper.get().vanish())) {
            player.getDataTracker().set(IDataTrackerHelper.get().vanish(), false);
            Objects.requireNonNull(player.getServer()).getPlayerManager().sendToAll(Compat.get().newPlayerListS2CPacket(0, player)); // ADD_PLAYER
            trackers.remove(player);
            player.getWorld().getChunkManager().loadEntity(player);
            if (MoreGameRules.get().checkBooleanWithPerm(player.getWorld().getGameRules(), MoreGameRules.get().doJoinMessageRule(), player))
                Compat.get().broadcast(player.getServer().getPlayerManager(), new Pair<>(1, new Identifier("system")), translatableText("multiplayer.player.joined",
                        player.getDisplayName())
                        .withStyle(Style.EMPTY.withFormatting(Formatting.YELLOW)).build());
        }
    }
}
