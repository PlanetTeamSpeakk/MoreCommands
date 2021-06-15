package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.arikia.dev.drpc.DiscordUser;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class DiscordCommand extends Command {

	private static File dataFile = null;
	private static String discordUrl = null;

	@Override
	public void init(MinecraftServer server) throws Exception {
		if (new File("config/MoreCommands/discordUrl.url").exists()) MoreCommands.tryMove("config/MoreCommands/discordUrl.url", MoreCommands.getRelativePath() + "discordUrl.url");
		dataFile = new File(MoreCommands.getRelativePath() + "discordUrl.url");
		try {
			discordUrl = MoreCommands.readString(dataFile);
			if (discordUrl.isEmpty()) discordUrl = null;
			else discordUrl = discordUrl.split("\n")[1].split("=", 2)[1];
		} catch (IOException e) {
			log.catching(e);
		}
		if (discordUrl != null)
			try {
				new URL(discordUrl);
			} catch (MalformedURLException e) {
				discordUrl = null;
				dataFile.delete();
			}
	}

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("discord").executes(ctx -> {
			sendMsg(ctx, discordUrl == null ? Formatting.RED + "This server does not have a Discord url set." : "Join our Discord server at " + Formatting.BLUE + Formatting.UNDERLINE + discordUrl + DF + ".");
			return 1;
		}).then(literal("set").requires(IS_OP).then(argument("url", StringArgumentType.greedyString()).executes(ctx -> {
			try {
				URL url = new URL(ctx.getArgument("url", String.class));
				discordUrl = url.toString();
				MoreCommands.saveString(dataFile, "[InternetShortcut]\nURL=" + discordUrl);
				sendMsg(ctx, "The url has been set.");
				return 1;
			} catch (MalformedURLException e) {
				sendMsg(ctx, Formatting.RED + "That is not a valid URL.");
			} catch (IOException e) {
				log.catching(e);
				sendMsg(ctx, Formatting.RED + "An error occurred while saving the file.");
			}
			return 0;
		}))).then(argument("player", EntityArgumentType.player()).executes(ctx -> {
			ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
			if (!MoreCommands.discordTags.containsKey(player)) {
				sendMsg(ctx, "That player does not have Discord or has not shared their tag.");
				return 0;
			} else if (MoreCommands.discordTagNoPerm.contains(player)) sendDiscordTag(ctx, player);
			else {
				sendMsg(player, ctx.getSource().getPlayer().getDisplayName().shallowCopy().append(new LiteralText(" has requested your ").setStyle(DS).append(new LiteralText("Discord tag").setStyle(SS).append(new LiteralText(". Click ").setStyle(DS).append(new LiteralText("here").setStyle(SS.withFormatting(Formatting.UNDERLINE).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "discord send " + ctx.getSource().getPlayer().getEntityName()))).append(new LiteralText(" to send it to them.").setStyle(DS)))))));
				sendMsg(ctx, "A request has been sent to the player.");
			}
			return 1;
		})).then(literal("send").then(argument("player", EntityArgumentType.player()).executes(ctx -> {
			PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
			sendDiscordTag(ctx, player);
			sendMsg(ctx, MoreCommands.textToString(player.getDisplayName(), SS, true) + DF + " has been sent your Discord tag.");
			return 1;
		}))));
	}

	private void sendDiscordTag(CommandContext<ServerCommandSource> ctx, PlayerEntity player) {
		DiscordUser user = MoreCommands.discordTags.get(player);
		String tag = user.username + "#" + user.discriminator;
		sendMsg(ctx, player.getDisplayName().shallowCopy().append(new LiteralText("'s ").setStyle(SS).append(new LiteralText( "Discord tag is ").setStyle(DS).append(new LiteralText(tag).setStyle(SS.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, tag)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Click to ").setStyle(DS).append(new LiteralText("copy").setStyle(SS).append(new LiteralText(".").setStyle(DS)))))).append(new LiteralText(".").setStyle(DS))))));
	}

	@Override
	public boolean forDedicated() {
		return true;
	}
}
