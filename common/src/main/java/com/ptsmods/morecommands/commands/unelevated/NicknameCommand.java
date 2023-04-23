package com.ptsmods.morecommands.commands.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.IDataTrackerHelper;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.arguments.IgnorantStringArgumentType;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;
import java.util.Optional;

public class NicknameCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReq("nick")
                .then(argument("nickname", IgnorantStringArgumentType.word())
                        .executes(ctx -> execute(ctx, Util.translateFormats(ctx.getArgument("nickname", String.class)), null))
                        .then(argument("player", EntityArgument.player())
                                .requires(hasPermissionOrOp("morecommands.nickname.others"))
                                .executes(ctx -> execute(ctx, Util.translateFormats(ctx.getArgument("nickname", String.class)), EntityArgument.getPlayer(ctx, "player")))))
                .then(literal("off")
                        .executes(ctx -> execute(ctx, null, null))
                        .then(argument("player", EntityArgument.player())
                                .requires(hasPermissionOrOp("morecommands.nickname.others"))
                                .executes(ctx -> execute(ctx, null, EntityArgument.getPlayer(ctx, "player"))))));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/nickname";
    }

    private int execute(CommandContext<CommandSourceStack> ctx, String nickname, ServerPlayer player) throws CommandSyntaxException {
        boolean self = player == null;
        if (self) player = ctx.getSource().getPlayerOrException();

        String stripped = nickname == null ? null : Objects.requireNonNull(nickname);

        if (nickname != null && stripped.length() > ctx.getSource().getLevel().getGameRules().getInt(MoreGameRules.get().nicknameLimitRule()) && !isOp(ctx))
            sendError(ctx, "The maximum length of a nickname excluding formats is " + ctx.getSource().getLevel().getGameRules().getInt(MoreGameRules.get().nicknameLimitRule()) +
                    " characters which is exceeded by the length of the given nickname (" + stripped.length() + ").");
        else {
            player.getEntityData().set(IDataTrackerHelper.get().nickname(), nickname == null ? Optional.empty() : Optional.of(literalText(nickname).build()));
            ctx.getSource().getServer().getPlayerList().broadcastAll(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.UPDATE_DISPLAY_NAME, player));
            sendMsg(ctx, (self ? "Your" : IMoreCommands.get().textToString(player.getName(), SS, true) + "'s") + (nickname == null ? " nickname has been " + ChatFormatting.RED + "disabled" + DF + "." :
                    " nickname has been set to " + SF + IMoreCommands.get().textToString(MoreCommands.getNickname(player), null, true) + DF + "."));
            return 1;
        }
        return 0;
    }
}
