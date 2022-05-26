package com.ptsmods.morecommands.commands.client;

import com.google.gson.Gson;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.api.util.text.TextBuilder;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class UrbanCommand extends ClientCommand {
    private static final Map<String, List<Map<String, ?>>> cache = new HashMap<>();
    private static final SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
    private static final SimpleDateFormat formatDate = new SimpleDateFormat("MMMM d yyyy");
    private static final SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");

    @Override
    public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) {
        parseFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        dispatcher.register(cLiteral("urban")
                .then(cArgument("query", StringArgumentType.greedyString())
                        .executes(this::execute)));
    }

    @Override
    public String getDocsPath() {
        return "/client/urban";
    }

    @SuppressWarnings("unchecked")
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
                results = cache.containsKey(query.toLowerCase()) ? cache.get(query.toLowerCase()) : (List<Map<String, ?>>) new Gson().fromJson(MoreCommands.getHTML("https://api.urbandictionary.com/v0/define?term=" +
                        query.replace(' ', '+')), Map.class).get("list");
                cache.put(query.toLowerCase(), results);
            } catch (IOException e) {
                log.catching(e);
                sendMsg(Formatting.RED + "An unknown error occurred while trying to get the search results from urban dictionary.");
                return;
            }
            if (result >= results.size()) sendError("Only found %s%d %sresults for query %s%s %swhile result %s%d %swas requested.", Formatting.DARK_RED, results.size(), Formatting.RED, Formatting.DARK_RED, query,
                    Formatting.RED, Formatting.DARK_RED, result, Formatting.RED);
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
                LiteralTextBuilder link = literalText("Click ");
                link.withStyle(DS);
                LiteralTextBuilder linkChild = literalText("here");
                linkChild.withStyle(Style.EMPTY.withFormatting(Formatting.BLUE, Formatting.UNDERLINE).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, data.get("permalink").toString())));
                link.append(linkChild);
                linkChild = literalText(" to view this result in the browser.");
                linkChild.withStyle(DS);
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

                LiteralTextBuilder pager = literalText("");
                LiteralTextBuilder prev = literalText("[<<<]");
                if (result > 0) prev.withStyle(Style.EMPTY.withFormatting(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/urban " + (result-1) + " " + query)));
                else prev.withStyle(Style.EMPTY.withFormatting(Formatting.GRAY));
                pager.append(prev);
                pager.append(literalText("  "));
                LiteralTextBuilder next = literalText("[>>>]");

                if (result < results.size()-1) next.withStyle(Style.EMPTY.withFormatting(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/urban " + (result+1) + " " + query)));
                else next.withStyle(Style.EMPTY.withFormatting(Formatting.GRAY));
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

    private TextBuilder<?> parseText(String s) {
        s += "[]";
        LiteralTextBuilder text = literalText("  ");
        text.withStyle(DS);
        while (s.contains("[")) {
            text.append(literalText(s.substring(0, s.indexOf('['))));
            String term = s.substring(s.indexOf('[')+1, s.indexOf(']'));
            s = s.substring(s.indexOf(']')+1);
            if (term.isEmpty()) continue;

            LiteralTextBuilder child = literalText(term);

            LiteralTextBuilder hoverText = literalText("Click to look up ");
            hoverText.withStyle(DS);

            LiteralTextBuilder hoverTextChild = literalText(term);
            hoverTextChild.withStyle(SS);
            hoverText.append(hoverTextChild);

            hoverTextChild = literalText(".");
            hoverTextChild.withStyle(DS);
            hoverText.append(hoverTextChild);

            child.withStyle(SS
                    .withFormatting(Formatting.UNDERLINE)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/urban " + term))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText.build())));
            text.append(child);
        }

        return text;
    }
}
