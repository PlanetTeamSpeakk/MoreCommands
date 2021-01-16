package com.ptsmods.morecommands.commands.client;

import com.google.gson.Gson;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class UrbanCommand extends ClientCommand {

    private static Map<String, List<Map<String, ?>>> cache = new HashMap<>();
    private static final SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
    private static final SimpleDateFormat formatDate = new SimpleDateFormat("MMMM d yyyy");
    private static final SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");

    @Override
    public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) {
        parseFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        dispatcher.register(cLiteral("urban").then(cArgument("query", StringArgumentType.greedyString()).executes(ctx -> execute(ctx))));
    }

    private int execute(CommandContext<ClientCommandSource> ctx) {
        MoreCommands.execute(() -> {
            int result = 0;
            String query = ctx.getArgument("query", String.class);
            if (MoreCommands.isInteger(query.split(" ")[0])) {
                result = Integer.parseInt(query.split(" ")[0]);
                query = query.substring(query.indexOf(' ')+1);
            }
            List<Map<String, ?>> results;
            try {
                results = cache.containsKey(query.toLowerCase()) ? cache.get(query.toLowerCase()) : (List<Map<String, ?>>) new Gson().fromJson(MoreCommands.getHTML("https://api.urbandictionary.com/v0/define?term=" + query.replace(' ', '+')), Map.class).get("list");
                cache.put(query.toLowerCase(), results);
            } catch (IOException e) {
                log.catching(e);
                sendMsg(Formatting.RED + "An unknown error occurred while trying to get the search results from urban dictionary.");
                return;
            }
            if (result >= results.size()) sendMsg(Formatting.RED + "Only found " + Formatting.DARK_RED + results.size() + Formatting.RED + " results for query " + Formatting.DARK_RED + query + Formatting.RED + " while result " + Formatting.DARK_RED + result + Formatting.RED + " was requested.");
            else {
                Map<String, ?> data = results.get(result);
                sendMsg("Result for query " + SF + query + DF + ":");
                sendMsg(parseText(cleanString(data.get("definition").toString())));
                String ex = data.get("example") == null || data.get("example").toString().isEmpty() ? null : data.get("example").toString();
                if (ex != null) {
                    sendMsg("Example:");
                    sendMsg(parseText(cleanString(ex)));
                }
                sendMsg("\u00A0");
                sendMsg(Formatting.GREEN + "Thumbs up: " + ((Double) data.get("thumbs_up")).intValue());
                sendMsg(Formatting.RED + "Thumbs down: " + ((Double) data.get("thumbs_down")).intValue());
                LiteralText link = new LiteralText("Click ");
                link.setStyle(DS);
                LiteralText linkChild = new LiteralText("here");
                linkChild.setStyle(Style.EMPTY.withFormatting(Formatting.BLUE, Formatting.UNDERLINE).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, data.get("permalink").toString())));
                link.append(linkChild);
                linkChild = new LiteralText(" to view this result in the browser.");
                linkChild.setStyle(DS);
                link.append(linkChild);
                sendMsg(link);
                sendMsg("\u00A0");
                Date date = null;
                try {
                    date = parseFormat.parse(data.get("written_on").toString());
                } catch (ParseException e) {
                    log.catching(e);
                }
                sendMsg("Written by " + SF + data.get("author") + DF + " on " + SF + (date == null ? "UNKNOWN" : formatDate.format(date) + DF + " at " + SF + formatTime.format(date)) + DF + ".");
                LiteralText pager = new LiteralText("");
                LiteralText prev = new LiteralText("[<<<]");
                if (result > 0) prev.setStyle(Style.EMPTY.withFormatting(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/urban " + (result-1) + " " + query)));
                else prev.setStyle(Style.EMPTY.withFormatting(Formatting.GRAY));
                pager.append(prev);
                pager.append(new LiteralText("  "));
                LiteralText next = new LiteralText("[>>>]");
                if (result < results.size()-1) next.setStyle(Style.EMPTY.withFormatting(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/urban " + (result+1) + " " + query)));
                else next.setStyle(Style.EMPTY.withFormatting(Formatting.GRAY));
                pager.append(next);
                sendMsg(pager);
            }
        });
        return 1;
    }

    private String cleanString(String s) {
        s = s.replaceAll("\r", "");
        while (s.contains("\n\n")) s = s.replaceAll("\n\n", "\n");
        return s.trim();
    }

    private LiteralText parseText(String s) {
        s += "[]";
        LiteralText text = new LiteralText("  ");
        text.setStyle(DS);
        while (s.contains("[")) {
            text.append(new LiteralText(s.substring(0, s.indexOf('['))));
            String term = s.substring(s.indexOf('[')+1, s.indexOf(']'));
            s = s.substring(s.indexOf(']')+1);
            if (term.isEmpty()) continue;
            LiteralText child = new LiteralText(term);
            LiteralText hoverText = new LiteralText("Click to look up ");
            hoverText.setStyle(DS);
            LiteralText hoverTextChild = new LiteralText(term);
            hoverTextChild.setStyle(SS);
            hoverText.append(hoverTextChild);
            hoverTextChild = new LiteralText(".");
            hoverTextChild.setStyle(DS);
            hoverText.append(hoverTextChild);
            child.setStyle(SS.withFormatting(Formatting.UNDERLINE).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/urban " + term)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText)));
            text.append(child);
        }
        return text;
    }
}
