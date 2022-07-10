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
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
        if (p == null) p = Lists.newArrayList(ctx.getSource().getPlayerOrException());
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
        return p.size();
    }

    public static void vanish(ServerPlayer player, boolean sendmsg) {
        player.getEntityData().set(IDataTrackerHelper.get().vanish(), true);
        player.getEntityData().set(IDataTrackerHelper.get().vanishToggled(), true);
        Objects.requireNonNull(player.getServer()).getPlayerList().broadcastAll(Compat.get().newPlayerListS2CPacket(4, player)); // REMOVE_PLAYER
        player.getLevel().getChunkSource().removeEntity(player);
        if (sendmsg && MoreGameRules.get().checkBooleanWithPerm(player.getLevel().getGameRules(), MoreGameRules.get().doJoinMessageRule(), player))
            Compat.get().broadcast(player.getServer().getPlayerList(), new Tuple<>(1, new ResourceLocation("system")), translatableText("multiplayer.player.left", player.getDisplayName())
                    .withStyle(Style.EMPTY.applyFormat(ChatFormatting.YELLOW)).build());
    }

    public static void unvanish(ServerPlayer player) {
        if (player.getEntityData().get(IDataTrackerHelper.get().vanish())) {
            player.getEntityData().set(IDataTrackerHelper.get().vanish(), false);
            Objects.requireNonNull(player.getServer()).getPlayerList().broadcastAll(Compat.get().newPlayerListS2CPacket(0, player)); // ADD_PLAYER
            trackers.remove(player);
            player.getLevel().getChunkSource().addEntity(player);
            if (MoreGameRules.get().checkBooleanWithPerm(player.getLevel().getGameRules(), MoreGameRules.get().doJoinMessageRule(), player))
                Compat.get().broadcast(player.getServer().getPlayerList(), new Tuple<>(1, new ResourceLocation("system")), translatableText("multiplayer.player.joined",
                        player.getDisplayName())
                        .withStyle(Style.EMPTY.applyFormat(ChatFormatting.YELLOW)).build());
        }
    }
}
