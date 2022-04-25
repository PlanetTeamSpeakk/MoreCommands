package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.arguments.IgnorantStringArgumentType;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import com.ptsmods.morecommands.util.CompatHolder;
import com.ptsmods.morecommands.util.DataTrackerHelper;
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
						.executes(ctx -> execute(ctx, translateFormats(ctx.getArgument("nickname", String.class)), null))
						.then(argument("player", EntityArgumentType.player())
								.requires(IS_OP)
								.executes(ctx -> execute(ctx, translateFormats(ctx.getArgument("nickname", String.class)), EntityArgumentType.getPlayer(ctx, "player")))))
				.then(literal("off")
						.executes(ctx -> execute(ctx, null, null))
						.then(argument("player", EntityArgumentType.player())
								.requires(IS_OP)
								.executes(ctx -> execute(ctx, null, EntityArgumentType.getPlayer(ctx, "player"))))));
	}

	private int execute(CommandContext<ServerCommandSource> ctx, String nickname, ServerPlayerEntity player) throws CommandSyntaxException {
		boolean self = player == null;
		if (self) player = ctx.getSource().getPlayer();

		String stripped = nickname == null ? null : Objects.requireNonNull(nickname);

		if (nickname != null && stripped.length() > ctx.getSource().getWorld().getGameRules().getInt(MoreGameRules.nicknameLimitRule) && !isOp(ctx))
			sendError(ctx, "The maximum length of a nickname excluding formats is " + ctx.getSource().getWorld().getGameRules().getInt(MoreGameRules.nicknameLimitRule) +
					" characters which is exceeded by the length of the given nickname (" + stripped.length() + ").");
		else {
			player.getDataTracker().set(DataTrackerHelper.NICKNAME, nickname == null ? Optional.empty() : Optional.of(literalText(nickname).build()));
			ctx.getSource().getServer().getPlayerManager().sendToAll(CompatHolder.getCompat().newPlayerListS2CPacket(3, player)); // UPDATE_DISPLAY_NAME
			sendMsg(ctx, (self ? "Your" : MoreCommands.textToString(player.getName(), SS, true) + "'s") + (nickname == null ? " nickname has been " + Formatting.RED + "disabled" + DF + "." :
					" nickname has been set to " + SF + MoreCommands.textToString(MoreCommands.getNickname(player), null, true) + DF + "."));
			return 1;
		}
		return 0;
	}
}
