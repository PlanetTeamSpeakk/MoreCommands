package com.ptsmods.morecommands.commands.elevated;

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
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;

import java.util.*;

public class VanishCommand extends Command {
    public static final Map<ServerPlayer, ServerEntity> trackers = new HashMap<>();

    public void preinit(boolean serverOnly) {
        // Gotta tick them manually since they don't get ticked when they're not tracked which breaks stuff like altering attributes (and thus altering reach).
        TickEvent.SERVER_PRE.register(server -> trackers.values().forEach(ServerEntity::sendChanges));
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.getRoot().addChild(MoreCommands.createAlias("v", dispatcher.register(literalReqOp("vanish")
                .executes(ctx -> execute(ctx, null))
                .then(argument("players", EntityArgument.players())
                        .requires(hasPermissionOrOp("morecommands.vanish.others"))
                        .executes(ctx -> execute(ctx, EntityArgument.getPlayers(ctx, "players")))))));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/vanish";
    }

    private int execute(CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> p) throws CommandSyntaxException {
        if (p == null) p = Collections.singletonList(ctx.getSource().getPlayerOrException());
        else if (!isOp(ctx)) {
            sendError(ctx, "You must be op to toggle vanish for others.");
            return 0;
        }

        for (ServerPlayer player : p) {
            boolean b = !player.getEntityData().get(IDataTrackerHelper.get().vanish());
            if (b) vanish(player, true);
            else unvanish(player);
            sendMsg(player, "You are " + Util.formatFromBool(b, "now", "no longer") + DF + " vanished.");
        }

        if (p.size() > 1 || !p.stream().allMatch(p0 -> p0 == ctx.getSource().getPlayer()))
            sendMsg(ctx, p.size() > 1 ? "Toggled vanish for " + SF + p.size() + DF + " players." :
                    SF + ctx.getSource().getPlayerOrException().getGameProfile().getName() + DF + " is " +
                            Util.formatFromBool(ctx.getSource().getPlayerOrException().getEntityData().get(IDataTrackerHelper.get().vanish()),
                                    "now", "no longer") + DF + " vanished.");

        return p.size();
    }

    public static void vanish(ServerPlayer player, boolean sendMsg) {
        player.getEntityData().set(IDataTrackerHelper.get().vanish(), true);
        player.getEntityData().set(IDataTrackerHelper.get().vanishToggled(), true);
        Objects.requireNonNull(player.getServer()).getPlayerList().broadcastAll(Compat.get().newClientboundPlayerInfoRemovePacket(player));
        ((ServerChunkCache) player.getCommandSenderWorld().getChunkSource()).removeEntity(player);

        if (player.getServer().isDedicatedServer() && sendMsg && MoreGameRules.get().checkBooleanWithPerm(player.getCommandSenderWorld().getGameRules(), MoreGameRules.get().doJoinMessageRule(), player))
            Compat.get().broadcast(player.getServer().getPlayerList(), new Tuple<>(1, new ResourceLocation("system")), translatableText("multiplayer.player.left", player.getDisplayName())
                    .withStyle(Style.EMPTY.applyFormat(ChatFormatting.YELLOW)).build());
    }

    public static void unvanish(ServerPlayer player) {
        if (!player.getEntityData().get(IDataTrackerHelper.get().vanish())) return;

        player.getEntityData().set(IDataTrackerHelper.get().vanish(), false);
        Objects.requireNonNull(player.getServer()).getPlayerList().broadcastAll(Compat.get().newClientboundPlayerInfoUpdatePacket("ADD_PLAYER", player));
        trackers.remove(player);
        ((ServerChunkCache) player.getCommandSenderWorld().getChunkSource()).addEntity(player);
        if (player.getServer().isDedicatedServer() && MoreGameRules.get().checkBooleanWithPerm(player.getCommandSenderWorld().getGameRules(), MoreGameRules.get().doJoinMessageRule(), player))
            Compat.get().broadcast(player.getServer().getPlayerList(), new Tuple<>(1, new ResourceLocation("system")), translatableText("multiplayer.player.joined",
                    player.getDisplayName())
                    .withStyle(Style.EMPTY.applyFormat(ChatFormatting.YELLOW)).build());
    }
}
