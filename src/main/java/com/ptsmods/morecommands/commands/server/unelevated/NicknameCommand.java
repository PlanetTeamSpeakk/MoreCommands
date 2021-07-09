package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.arguments.IgnorantStringArgumentType;
import com.ptsmods.morecommands.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.Objects;
import java.util.Optional;

public class NicknameCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literalReq("nick")
				.then(argument("nickname", IgnorantStringArgumentType.word()).executes(ctx -> execute(ctx, translateFormats(ctx.getArgument("nickname", String.class)), null)).then(argument("player", EntityArgumentType.player()).requires(IS_OP).executes(ctx -> execute(ctx, translateFormats(ctx.getArgument("nickname", String.class)), EntityArgumentType.getPlayer(ctx, "player")))))
				.then(literal("off").executes(ctx -> execute(ctx, null, null)).then(argument("player", EntityArgumentType.player()).requires(IS_OP))));
	}

	private int execute(CommandContext<ServerCommandSource> ctx, String nickname, ServerPlayerEntity player) throws CommandSyntaxException {
		boolean self = player == null;
		if (self) player = ctx.getSource().getPlayer();
		if (nickname != null && Objects.requireNonNull(Formatting.strip(nickname)).length() > ctx.getSource().getWorld().getGameRules().getInt(MoreCommands.nicknameLimitRule) && !isOp(ctx)) sendError(ctx, "The maximum length of a nickname excluding formats is " + ctx.getSource().getWorld().getGameRules().getInt(MoreCommands.nicknameLimitRule) + " characters which is exceeded by the length of the given nickname (" + Formatting.strip(nickname).length() + ").");
		else {
			player.getDataTracker().set(MoreCommands.NICKNAME, nickname == null ? Optional.empty() : Optional.of(new LiteralText(nickname)));
			ctx.getSource().getServer().getPlayerManager().sendToAll(Compat.getCompat().newPlayerListS2CPacket(3, player)); // UPDATE_DISPLAY_NAME
			sendMsg(ctx, (self ? "Your" : MoreCommands.textToString(player.getName(), SS, true) + "'s") + (nickname == null ? " nickname has been " + Formatting.RED + "disabled" + DF + "." : " nickname has been set to " + SF + nickname + DF + "."));
			return 1;
		}
		return 0;
	}
}
