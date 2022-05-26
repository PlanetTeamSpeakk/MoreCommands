package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.IDataTrackerHelper;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.arguments.IgnorantStringArgumentType;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

import java.util.Objects;
import java.util.Optional;

public class NicknameCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literalReq("nick")
                .then(argument("nickname", IgnorantStringArgumentType.word())
                        .executes(ctx -> execute(ctx, Util.translateFormats(ctx.getArgument("nickname", String.class)), null))
                        .then(argument("player", EntityArgumentType.player())
                                .requires(hasPermissionOrOp("morecommands.nickname.others"))
                                .executes(ctx -> execute(ctx, Util.translateFormats(ctx.getArgument("nickname", String.class)), EntityArgumentType.getPlayer(ctx, "player")))))
                .then(literal("off")
                        .executes(ctx -> execute(ctx, null, null))
                        .then(argument("player", EntityArgumentType.player())
                                .requires(hasPermissionOrOp("morecommands.nickname.others"))
                                .executes(ctx -> execute(ctx, null, EntityArgumentType.getPlayer(ctx, "player"))))));
    }

    private int execute(CommandContext<ServerCommandSource> ctx, String nickname, ServerPlayerEntity player) throws CommandSyntaxException {
        boolean self = player == null;
        if (self) player = ctx.getSource().getPlayerOrThrow();

        String stripped = nickname == null ? null : Objects.requireNonNull(nickname);

        if (nickname != null && stripped.length() > ctx.getSource().getWorld().getGameRules().getInt(MoreGameRules.get().nicknameLimitRule()) && !isOp(ctx))
            sendError(ctx, "The maximum length of a nickname excluding formats is " + ctx.getSource().getWorld().getGameRules().getInt(MoreGameRules.get().nicknameLimitRule()) +
                    " characters which is exceeded by the length of the given nickname (" + stripped.length() + ").");
        else {
            player.getDataTracker().set(IDataTrackerHelper.get().nickname(), nickname == null ? Optional.empty() : Optional.of(literalText(nickname).build()));
            ctx.getSource().getServer().getPlayerManager().sendToAll(Compat.get().newPlayerListS2CPacket(3, player)); // UPDATE_DISPLAY_NAME
            sendMsg(ctx, (self ? "Your" : IMoreCommands.get().textToString(player.getName(), SS, true) + "'s") + (nickname == null ? " nickname has been " + Formatting.RED + "disabled" + DF + "." :
                    " nickname has been set to " + SF + IMoreCommands.get().textToString(MoreCommands.getNickname(player), null, true) + DF + "."));
            return 1;
        }
        return 0;
    }
}
