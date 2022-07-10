package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsArch;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class DiscordCommand extends Command {
    private static File dataFile = null;
    private static String discordUrl = null;

    @Override
    public void init(boolean serverOnly, MinecraftServer server) throws Exception {
        if (MoreCommandsArch.getConfigDirectory().resolve("discordUrl.url").toFile().exists()) MoreCommands.tryMove("config/MoreCommands/discordUrl.url", MoreCommands.getRelativePath().resolve("discordUrl.url").toString());
        dataFile = MoreCommands.getRelativePath().resolve("discordUrl.url").toFile();
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
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReq("discord")
                .executes(ctx -> {
                    sendMsg(ctx, discordUrl == null ? ChatFormatting.RED + "This server does not have a Discord url set." : "Join our Discord server at " + ChatFormatting.BLUE + ChatFormatting.UNDERLINE + discordUrl + DF + ".");
                    return 1;
                })
                .then(literal("set")
                        .requires(IS_OP)
                        .then(argument("url", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    try {
                                        URL url = new URL(ctx.getArgument("url", String.class));
                                        discordUrl = url.toString();
                                        MoreCommands.saveString(dataFile, "[InternetShortcut]\nURL=" + discordUrl);
                                        sendMsg(ctx, "The url has been set.");
                                        return 1;
                                    } catch (MalformedURLException e) {
                                        sendError(ctx, "That is not a valid URL.");
                                    } catch (IOException e) {
                                        log.catching(e);
                                        sendError(ctx, "An error occurred while saving the file.");
                                    }
                                    return 0;
                                })))
                .then(argument("player", EntityArgument.player())
                        .executes(ctx -> {
                            ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
                            if (!MoreCommands.discordTags.containsKey(player)) {
                                sendMsg(ctx, "That player does not have Discord or has not shared their tag.");
                                return 0;
                            } else if (MoreCommands.discordTagNoPerm.contains(player)) sendDiscordTag(ctx, player);
                            else {
                                sendMsg(player, literalText("")
                                        .append(Compat.get().builderFromText(ctx.getSource().getPlayerOrException().getDisplayName()))
                                        .append(literalText(" has requested your ", DS))
                                        .append(literalText("Discord tag", SS))
                                        .append(literalText(". Click ")
                                                .append(literalText("here", SS.applyFormat(ChatFormatting.UNDERLINE)
                                                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "discord send " + ctx.getSource().getPlayerOrException().getScoreboardName()))))
                                        .append(literalText(" to send it to them.", DS))));
                                sendMsg(ctx, "A request has been sent to the player.");
                            }
                            return 1;
                        }))
                .then(literal("send")
                        .then(argument("player", EntityArgument.player())
                                .executes(ctx -> {
                                    Player player = EntityArgument.getPlayer(ctx, "player");
                                    sendDiscordTag(ctx, player);
                                    sendMsg(ctx, IMoreCommands.get().textToString(player.getDisplayName(), SS, true) + DF + " has been sent your Discord tag.");
                                    return 1;
                                }))));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/discord";
    }

    private void sendDiscordTag(CommandContext<CommandSourceStack> ctx, Player player) {
        String tag = MoreCommands.discordTags.get(player);
        sendMsg(ctx, literalText("")
                .append(Compat.get().builderFromText(player.getDisplayName()))
                .append(literalText("'s ", SS))
                .append(literalText( "Discord tag is ")
                        .withStyle(DS))
                .append(literalText(tag)
                        .withStyle(SS
                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, tag))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, literalText("Click to ", DS)
                                        .append(literalText("copy", SS))
                                                .append(literalText(".", DS)).build()))))
                        .append(literalText(".")));
    }

    @Override
    public boolean isDedicatedOnly() {
        return true;
    }
}
